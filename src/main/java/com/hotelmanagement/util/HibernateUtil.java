package com.hotelmanagement.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

/**
 * Builds the Hibernate SessionFactory and runs database work inside sessions/transactions.
 *
 * Database settings are looked up in this order (first one found wins):
 *   1. Environment variables  DB_URL, DB_USER, DB_PASSWORD
 *   2. Java system properties db.url, db.user, db.password
 *   3. src/main/resources/db.properties (local file, ignored by Git)
 *   4. Defaults for a local MySQL server (empty password)
 *
 * Other settings (same lookup order):
 *   DB_DIALECT          optional Hibernate dialect class. If empty: MariaDBDialect when the server is MariaDB
 *                       (XAMPP), otherwise Hibernate detects MySQL by itself.
 *   SCHEMA_VALIDATION   "tolerant" (default) = read-only check of tables/columns, lists all differences;
 *                       "hibernate" = Hibernate hbm2ddl "validate" (exact types, stops startup on a difference);
 *                       "off" = no check.
 */
public final class HibernateUtil {

    /** A piece of database work that returns a value. */
    public interface DbWork<T> {
        T run(Session session);
    }

    /** A piece of database work that returns nothing. */
    public interface DbVoid {
        void run(Session session);
    }

    private static SessionFactory sessionFactory;

    /**
     * Only one write transaction runs at a time inside this application.
     * This keeps ID generation (MAX(id)+1) and booking checks safe on a single Tomcat.
     */
    private static final ReentrantLock WRITE_LOCK = new ReentrantLock();

    private HibernateUtil() { }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = build();
        }
        return sessionFactory;
    }

    /**
     * Closes the SessionFactory (and with it the Hikari connection pool).
     * Called ONLY when the web application stops or is redeployed (see AppInitListener).
     */
    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            try {
                if (sessionFactory.isOpen()) sessionFactory.close();
            } finally {
                sessionFactory = null;
            }
        }
    }

    // ---------------------------------------------------------------- building

    /** Builds the SessionFactory exactly ONCE, so exactly one connection pool is started. */
    private static SessionFactory build() {
        String url = setting("DB_URL", "db.url",
                "jdbc:mysql://localhost:3306/hotel_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = setting("DB_USER", "db.user", "root");
        String password = setting("DB_PASSWORD", "db.password", "");
        String mode = setting("SCHEMA_VALIDATION", "schema.validation", "tolerant").trim().toLowerCase();

        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        cfg.setProperty("hibernate.connection.url", url);
        cfg.setProperty("hibernate.connection.username", user);
        cfg.setProperty("hibernate.connection.password", password);

        String dialect = setting("DB_DIALECT", "db.dialect", "");
        if (dialect.isBlank()) dialect = detectDialect(url, user, password);
        if (dialect != null && !dialect.isBlank()) cfg.setProperty("hibernate.dialect", dialect);

        // The schema is NEVER created or changed. "validate" is only used in "hibernate" mode.
        cfg.setProperty("hibernate.hbm2ddl.auto", "hibernate".equals(mode) ? "validate" : "none");

        SessionFactory factory = cfg.buildSessionFactory();

        if ("tolerant".equals(mode)) {
            try {
                SchemaCheck.run(factory);
            } catch (RuntimeException e) {
                System.err.println("[HotelManagement] Schema check could not run: " + e.getMessage());
            }
        }
        return factory;
    }

    /**
     * XAMPP ships MariaDB, which the MySQL driver reports with the odd version "5.5.5-10.x.x-MariaDB".
     * Without help Hibernate would treat it as MySQL 5.5 and warn that the version is unsupported.
     * A short probe connection (closed immediately - NOT a pool) tells us which server we have.
     * Returns null when the server is real MySQL (Hibernate then detects the dialect itself).
     */
    private static String detectDialect(String url, String user, String password) {
        try (Connection c = DriverManager.getConnection(url, user, password)) {
            String version = c.getMetaData().getDatabaseProductVersion();
            if (version != null && version.toLowerCase().contains("mariadb")) {
                return "org.hibernate.dialect.MariaDBDialect";
            }
        } catch (Exception ignored) {
            // Connection problems are reported properly by Hibernate right after this.
        }
        return null;
    }

    private static Properties fileProps;

    private static synchronized String setting(String envName, String propName, String defaultValue) {
        String v = System.getenv(envName);
        if (v != null && !v.isBlank()) return v;
        v = System.getProperty(propName);
        if (v != null && !v.isBlank()) return v;
        if (fileProps == null) {
            fileProps = new Properties();
            try (InputStream in = HibernateUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in != null) fileProps.load(in);
            } catch (Exception ignored) {
                // no local file - that is fine on a server
            }
        }
        v = fileProps.getProperty(propName);
        return (v != null && !v.isBlank()) ? v : defaultValue;
    }

    // ---------------------------------------------------------------- helpers

    /** Read-only work: opens a session, runs the work, closes the session. */
    public static <T> T read(DbWork<T> work) {
        try (Session session = getSessionFactory().openSession()) {
            return work.run(session);
        }
    }

    /** Write work inside ONE transaction: commit on success, rollback on any error. */
    public static <T> T tx(DbWork<T> work) {
        WRITE_LOCK.lock();
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            T result = work.run(session);
            transaction.commit();
            return result;
        } catch (RuntimeException e) {
            rollback(transaction);
            throw translate(e);
        } finally {
            session.close();
            WRITE_LOCK.unlock();
        }
    }

    /** Same as tx(...) for work that returns nothing. */
    public static void txRun(DbVoid work) {
        tx(session -> {
            work.run(session);
            return null;
        });
    }

    private static void rollback(Transaction transaction) {
        try {
            if (transaction != null && transaction.isActive()) transaction.rollback();
        } catch (RuntimeException ignored) {
            // nothing more we can do
        }
    }

    /** Turns database constraint errors into friendly messages. */
    private static RuntimeException translate(RuntimeException e) {
        if (e instanceof ServiceException) return e;
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof ConstraintViolationException) {
                int code = ((ConstraintViolationException) t).getErrorCode();
                if (code == 1062) return new ServiceException("A record with the same unique value already exists.");
                if (code == 1451) return new ServiceException("This record is used by other records (bookings, payments, accounts...) and cannot be deleted.");
                if (code == 1452) return new ServiceException("A linked record does not exist.");
                return new ServiceException("The database rejected this change (constraint violation).");
            }
        }
        return e;
    }
}
