package com.hotelmanagement.listener;

import com.hotelmanagement.service.AuthService;
import com.hotelmanagement.util.HibernateUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * Runs when the web application starts/stops.
 *  - Connects Hibernate to MySQL right away, so configuration problems appear in the Tomcat log at startup.
 *  - Optionally creates the FIRST admin account when the environment variables
 *    ADMIN_USERNAME and ADMIN_PASSWORD are set and no ADMIN exists yet.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("[HotelManagement] Hibernate started. Database connection OK.");

            String adminUser = readSetting("ADMIN_USERNAME");
            String adminPass = readSetting("ADMIN_PASSWORD");
            if (adminUser != null && !adminUser.isBlank() && adminPass != null && !adminPass.isBlank()) {
                boolean created = new AuthService().createFirstAdminIfMissing(adminUser.trim(), adminPass);
                System.out.println(created
                        ? "[HotelManagement] First admin account '" + adminUser.trim() + "' was created."
                        : "[HotelManagement] An admin already exists - ADMIN_USERNAME / ADMIN_PASSWORD ignored.");
            }
        } catch (Exception e) {
            System.err.println("[HotelManagement] STARTUP PROBLEM: could not start Hibernate / database.");
            System.err.println("Check DB_URL, DB_USER, DB_PASSWORD (or db.properties) and that MySQL is running.");
            e.printStackTrace();
        }
    }

    /** Reads an environment variable, or a -D system property with the same name (handy in NetBeans). */
    private String readSetting(String name) {
        String v = System.getenv(name);
        return (v != null && !v.isBlank()) ? v : System.getProperty(name);
    }

    /**
     * Runs when the application is stopped or redeployed. Cleans up in this order so Tomcat
     * has nothing left to complain about:
     *   1. close Hibernate (this also closes the Hikari connection pool)
     *   2. unregister the JDBC drivers that THIS web application registered
     *   3. stop the MySQL driver's "abandoned connection cleanup" thread
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.shutdown();
        deregisterJdbcDrivers();
        stopMysqlCleanupThread();
        System.out.println("[HotelManagement] Application stopped: connection pool closed, JDBC driver cleaned up.");
    }

    private void deregisterJdbcDrivers() {
        ClassLoader myLoader = AppInitListener.class.getClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            // only remove drivers loaded by this web app, never Tomcat's or the JDK's own
            if (driver.getClass().getClassLoader() == myLoader) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    System.err.println("[HotelManagement] Could not deregister driver " + driver + ": " + e);
                }
            }
        }
    }

    /** Called through reflection so the code compiles with any Connector/J 8.x or 9.x version. */
    private void stopMysqlCleanupThread() {
        try {
            Class<?> cleanup = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            try {
                cleanup.getMethod("checkedShutdown").invoke(null);
            } catch (NoSuchMethodException e) {
                cleanup.getMethod("uncheckedShutdown").invoke(null);
            }
        } catch (ClassNotFoundException e) {
            // MySQL driver not present (for example another driver is used) - nothing to stop
        } catch (Exception e) {
            System.err.println("[HotelManagement] Could not stop the MySQL cleanup thread: " + e);
        }
    }
}
