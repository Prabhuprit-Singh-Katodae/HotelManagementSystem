package com.hotelmanagement.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Read-only check that the real database has every table and column the entities use,
 * and that each column has a compatible kind of type. It only READS information_schema.
 *
 * Why not rely on Hibernate's own "validate"? Hibernate compares exact types (for example it
 * insists on DECIMAL for BigDecimal) and stops at the FIRST difference. This check accepts any
 * numeric type for numeric fields (INT, DECIMAL, DOUBLE all work at run time) and lists ALL
 * real problems in one go.
 *
 * Kinds: I = whole number, N = any number, S = text, D = date/datetime/timestamp,
 *        T = datetime/timestamp, A = anything (e.g. phone or room number may be text or number).
 */
public final class SchemaCheck {

    private static final String[] EXPECTED = {
        "customer: customer_id=I name=S phone=A email=S address=S",
        "room_type: type_id=I type_name=S price=N",
        "room: room_id=I room_number=A type_id=I status=S",
        "booking: booking_id=I customer_id=I room_id=I check_in=D check_out=D status=S",
        "payment: payment_id=I booking_id=I amount=N payment_date=D payment_method=S",
        "employee: employee_id=I name=S role=S phone=A salary=N",
        "service: service_id=I service_name=S price=N",
        "service_booking: service_booking_id=I booking_id=I service_id=I quantity=I service_date=D",
        "feedback: feedback_id=I customer_id=I rating=I comments=S feedback_date=D",
        "user_account: user_id=I username=S password_hash=S role=S customer_id=I employee_id=I status=S",
        "booking_status_history: history_id=I booking_id=I old_status=S new_status=S changed_by=I changed_at=T"
    };

    private SchemaCheck() { }

    /** Runs the check and prints the result to the Tomcat log. Never changes the database. */
    public static void run(SessionFactory factory) {
        List<String> problems;
        int checked = 0;
        try (Session session = factory.openSession()) {
            Map<String, Map<String, String>> actual = session.doReturningWork(conn -> {
                Map<String, Map<String, String>> map = new HashMap<>();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT LOWER(table_name), LOWER(column_name), LOWER(data_type) "
                        + "FROM information_schema.columns WHERE table_schema = DATABASE()");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        map.computeIfAbsent(rs.getString(1), k -> new HashMap<>()).put(rs.getString(2), rs.getString(3));
                    }
                }
                return map;
            });

            problems = new ArrayList<>();
            for (String line : EXPECTED) {
                String table = line.substring(0, line.indexOf(':'));
                Map<String, String> cols = actual.get(table);
                if (cols == null) {
                    problems.add("table '" + table + "' was not found");
                    continue;
                }
                for (String pair : line.substring(line.indexOf(':') + 1).trim().split(" ")) {
                    String col = pair.substring(0, pair.indexOf('='));
                    char kind = pair.charAt(pair.length() - 1);
                    String type = cols.get(col);
                    checked++;
                    if (type == null) {
                        problems.add("column '" + table + "." + col + "' was not found");
                    } else if (!compatible(kind, type)) {
                        problems.add("column '" + table + "." + col + "' is " + type.toUpperCase()
                                + " but the application expects " + describe(kind));
                    }
                }
            }
        }

        if (problems.isEmpty()) {
            System.out.println("[HotelManagement] Schema check OK: 11 tables and " + checked + " columns found with compatible types.");
        } else {
            System.err.println("=================================================================");
            System.err.println("[HotelManagement] SCHEMA CHECK found " + problems.size() + " difference(s):");
            for (String p : problems) System.err.println("  - " + p);
            System.err.println("  Nothing was created or changed in the database.");
            System.err.println("=================================================================");
        }
    }

    private static boolean compatible(char kind, String type) {
        boolean whole = type.matches("tinyint|smallint|mediumint|int|integer|bigint");
        boolean number = whole || type.matches("decimal|numeric|float|double|real");
        switch (kind) {
            case 'I': return whole;
            case 'N': return number;
            case 'S': return type.matches("varchar|char|text|tinytext|mediumtext|longtext|enum|set");
            case 'D': return type.matches("date|datetime|timestamp");
            case 'T': return type.matches("datetime|timestamp");
            default: return true;
        }
    }

    private static String describe(char kind) {
        switch (kind) {
            case 'I': return "a whole number (INT)";
            case 'N': return "a number";
            case 'S': return "text";
            case 'D': return "a date";
            case 'T': return "a date and time";
            default: return "any type";
        }
    }
}
