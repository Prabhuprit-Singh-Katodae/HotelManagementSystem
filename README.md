# Grandstay Hotel Management System

A browser-based hotel management web application for the **Advanced Java Technology** course.

**JSP + Servlets + Hibernate (DAO + Service layers) + MySQL**, built with **Maven** as a WAR file and run on **Apache Tomcat 10.1**.

Two kinds of users: **customers** (browse, book, pay, give feedback) and **admin / staff** (run the hotel).

---

## 0. Honest status - please read first

This project was written **without the ability to compile it with Maven, connect to MySQL, or run it in a browser**.

| Check | Status |
|---|---|
| Java sources type-checked with `javac` against stand-in versions of the Hibernate/Jakarta/BCrypt APIs | Done - no errors |
| Every JSP link / form action matches a servlet URL; every servlet view name matches a JSP file | Done - no mismatches |
| XML files (`web.xml`, `hibernate.cfg.xml`, `pom.xml`) well-formed | Done |
| `mvn package` really builds the WAR | **Not run - you must do it** |
| Hibernate connects and validates against your real MySQL schema | **Not run - you must do it** |
| JSP pages render, buttons work, bookings save | **Not run - use the checklist in section 8** |

The project was built **without your SQL export**, so the entity mappings are based on the column list you wrote and your PDF screenshots. Section 3 explains exactly which assumptions were made and how to check them (5 minutes).

If something fails, send the **first error in the Tomcat/NetBeans log** and the fix will be small.

---

## 1. What you need

| Tool | Version |
|---|---|
| **JDK** | **17** (21 also works). Do **not** use JDK 8 or the very new JDK 26 |
| **Apache Tomcat** | **10.1.x** (Jakarta EE 10). Tomcat 8/9 and the XAMPP Tomcat will **not** work because the code uses `jakarta.servlet.*` |
| **Maven** | 3.9+ (NetBeans includes one) |
| **MySQL** | 8.x (XAMPP's MySQL/MariaDB 10.4+ is fine) |
| **NetBeans** | 21 or newer (any version with Maven + Tomcat support) |

Libraries (downloaded automatically by Maven): Hibernate ORM 6.4.4, HikariCP, MySQL Connector/J 8.3.0, jBCrypt 0.4, JSTL 3.0 (Jakarta).

---

## 2. Database connection (no passwords in code)

The database **already exists** (`hotel_management`, 11 tables). The application never creates, alters or drops tables.

1. Go to `src/main/resources/`.
2. Copy `db.properties.example` to **`db.properties`**.
3. Open it and put your MySQL user and password:

```properties
db.url=jdbc:mysql://localhost:3306/hotel_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

`db.properties` is in `.gitignore`, so it is **never uploaded to GitHub**.

On a server you use environment variables instead (they win over the file): `DB_URL`, `DB_USER`, `DB_PASSWORD`.

---

## 3. Check the schema assumptions (important)

Run `database/verify_schema.sql` (read-only) in phpMyAdmin and compare with the entity classes.

| Assumption | Why | If wrong |
|---|---|---|
| Table names are **lowercase** (`customer`, `room_type`, `service_booking`, `user_account`...) | phpMyAdmin in your PDF shows lowercase names | Edit `@Table(name=...)` in the entity |
| Column names are exactly the ones you listed | Your instructions | Edit `@Column(name=...)` |
| IDs of the first 9 tables may or may not be AUTO_INCREMENT | Your screenshots show room_id = 101..105, which suggests manual IDs | **Nothing to do**: the DAO assigns `MAX(id)+1` itself, which works in both cases |
| `user_account` and `booking_status_history` are AUTO_INCREMENT | You said so | - |
| Prices, amounts, salary are numeric (`INT` or `DECIMAL`) -> mapped to `BigDecimal` | Screenshots show whole numbers | Works with both at run time |
| `room_number` may be `VARCHAR` or `INT` -> mapped to `String` | Unknown | Works with both at run time |
| Dates are `DATE`; `changed_at` is `TIMESTAMP` | Screenshots | - |
| Booking status values: `Pending, Confirmed, Checked-In, Checked-Out, Cancelled`. Room status: `Available, Occupied, Maintenance` | Your data uses `Pending/Confirmed`, `Available/Occupied` | Edit the constants in `Booking.java` / `Room.java` |

**Schema check (startup):** by default the application runs a **read-only check** of `information_schema` and prints either `Schema check OK` or a list of **all** differences (missing table/column, or a column of a clearly wrong kind). Whole-number, decimal and double columns are all accepted for numeric fields. Nothing is ever created or changed. Setting `SCHEMA_VALIDATION`:
- `tolerant` (default) - the check above
- `hibernate` - Hibernate's own `validate` (exact types; **stops startup** on the first difference)
- `off` - no check

**Database server / dialect:** XAMPP uses **MariaDB**. The application detects this automatically and uses `MariaDBDialect`; with real MySQL 8 (for example a hosted database) Hibernate detects the dialect itself. To force one, set `DB_DIALECT` (for example `org.hibernate.dialect.MariaDBDialect` or `org.hibernate.dialect.MySQLDialect`).

**Linux hosting note:** MySQL on Linux treats table names as case-sensitive. When you move the database to a hosting provider, export from phpMyAdmin (lowercase names) and keep them lowercase.

---

## 4. Open and run in NetBeans

1. **Install Tomcat 10.1.x**: download the zip from tomcat.apache.org, extract it (e.g. `C:\tomcat10`).
2. NetBeans -> **Services** tab -> right-click **Servers** -> **Add Server** -> *Apache Tomcat or TomEE* -> choose the Tomcat 10.1 folder -> give a username/password -> Finish.
3. **File -> Open Project** -> select the `HotelManagementSystem` folder (it shows a Maven icon).
4. Right-click the project -> **Properties -> Run** -> Server: *Apache Tomcat 10.1*. Under **Build -> Compile** make sure the Java platform is **JDK 17+**.
5. Right-click the project -> **Clean and Build**. The first build downloads libraries (internet needed). You should see `BUILD SUCCESS` and `target/HotelManagement.war`.
6. Right-click -> **Run**. Open: **http://localhost:8080/HotelManagement/**

In the Tomcat log you should see:
`[HotelManagement] Hibernate started. Database connection OK.`

If you see `STARTUP PROBLEM` instead, jump to section 9.

---

## 5. First admin account

Passwords are stored as **BCrypt hashes**. Your sample rows in `user_account` may not contain BCrypt hashes, so create an admin using **one** of these ways:

**Way A - automatic at startup (easiest).** In NetBeans: Services -> Servers -> your Tomcat -> right-click -> **Properties -> Platform -> VM Options**, add (use your own password, at least 8 characters):

```
-DADMIN_USERNAME=admin -DADMIN_PASSWORD=ChooseAStrongPassword1
```

Restart. If no ADMIN exists, one is created. Afterwards **remove these two options**. On a server, set the same two names as environment variables.

**Way B - SQL.** Generate a hash: in NetBeans right-click `util/HashPasswordTool.java` -> *Run File* (set the argument via Project Properties -> Run -> Arguments), or run

```
java -cp target/classes:<path-to-jbcrypt-0.4.jar> com.hotelmanagement.util.HashPasswordTool YourPassword
```

then paste the printed hash into `database/first_admin_example.sql` and run it once.

Log in at **`/admin/login`** (staff and admin sign-in). Guests use **`/login`**. Each page only accepts its own kind of account: a guest account on the staff page, or a staff/admin account on the guest page, is refused with a clear message. Then use **User accounts** to create STAFF accounts or reset passwords of the sample users.

---

## 6. Roles and pages

| Role | Can open |
|---|---|
| Visitor | `/home`, `/rooms`, `/room?id=`, `/availability`, `/login` (guest login), `/register`, `/admin/login` (staff login page) |
| CUSTOMER | `/customer/book`, `/customer/bookings`, `/customer/booking-confirmation`, `/customer/payments`, `/customer/invoice`, `/customer/feedback`, `/customer/profile` - **only their own data** |
| STAFF | `/admin/dashboard`, `customers`, `rooms`, `room-types`, `bookings`, `booking-details`, `payments`, `invoice`, `services`, `service-bookings`, `feedback` |
| ADMIN | everything staff can, plus `/admin/employees`, `/admin/users`, and **all delete actions** |

Two login pages share the same `user_account` table: `/login` for CUSTOMER accounts and `/admin/login` for ADMIN and STAFF. Logging out as staff returns to `/admin/login`.

Security is enforced on the server by `AuthFilter` (not just hidden menus). Every POST form also carries a CSRF token (added automatically by `assets/js/common.js`).

**Booking rules** (in `BookingService`): check-out must be after check-in; no past check-in; overlapping dates on the same room are rejected (checked again inside a transaction with a row lock); allowed status flow is Pending -> Confirmed -> Checked-In -> Checked-Out, with Cancel allowed from Pending/Confirmed; every change writes a row to `booking_status_history`; check-in sets the room to *Occupied*, check-out sets it back to *Available*. Future availability is calculated from bookings, not from `Room.status`.

**Payments:** staff record Cash / UPI / Card payments. Customers can use a clearly labelled **demo payment** that only stores a record. **No real payment gateway is connected.**

---

## 7. Project structure

```
HotelManagementSystem/
  pom.xml  Dockerfile  README.md  .gitignore
  database/            verify_schema.sql, first_admin_example.sql   (no table creation)
  docker/start.sh
  src/main/java/com/hotelmanagement/
     entity/      11 Hibernate entities (+ Identifiable)
     dao/         BaseDAO + 11 DAO classes (take a Hibernate Session)
     service/     business rules, transactions, billing
     controller/  14 servlets + BaseServlet
     filter/      AuthFilter (login, roles, CSRF)
     listener/    AppInitListener (starts Hibernate, optional first admin)
     util/        HibernateUtil, PasswordUtil, Validator, LoginUser, ServiceException
  src/main/resources/ hibernate.cfg.xml, db.properties.example
  src/main/webapp/
     assets/css, assets/js, assets/images (all local, no external CDN)
     WEB-INF/web.xml
     WEB-INF/views/{customer, admin, common}/*.jsp     (JSPs live in WEB-INF so they can only be reached through servlets)
     index.jsp (forwards to /home)
```

Layers: **JSP** (presentation) -> **Servlet** (HTTP) -> **Service** (rules + transactions) -> **DAO** (queries) -> **Hibernate** -> **MySQL**.

---

## 8. Manual test checklist (please do this in NetBeans)

Tick each line. Stop at the first failure and send me the Tomcat log line.

**Startup**
- [ ] Clean and Build succeeds; `target/HotelManagement.war` exists
- [ ] Tomcat log shows `Database connection OK` (and no schema WARNING, or you understand it)
- [ ] `http://localhost:8080/HotelManagement/` shows the home page with your 4 room types

**Public / customer**
- [ ] Rooms page lists rooms 101-105 with prices; filter by type works
- [ ] Availability: check-out before check-in shows an error; valid dates list rooms
- [ ] Register a new customer -> new rows appear in `customer` and `user_account`
- [ ] Log in as that customer at `/login`; visiting `/admin/dashboard` gives *Access denied*; signing in with the same account at `/admin/login` is refused
- [ ] Book a room -> confirmation page; row in `booking` (Pending) and in `booking_status_history`
- [ ] Booking the same room for overlapping dates (second customer) is refused
- [ ] My bookings shows only your bookings; Cancel works (status Cancelled + history row)
- [ ] Payments page: demo payment of the balance -> row in `payment`; invoice shows Paid
- [ ] Feedback submits and appears in the list; profile update and password change work
- [ ] Log out destroys the session (Back button then login redirect)

**Staff / admin**
- [ ] Admin signs in at `/admin/login` -> dashboard numbers match the database; the same account is refused at `/login`
- [ ] Customers: add, search, edit, delete (delete visible to ADMIN only)
- [ ] Room types and Rooms: add, edit, change status, delete
- [ ] Bookings: create for a customer, Confirm, Check in (room becomes Occupied), Check out (room Available), Cancel; invalid jumps are refused
- [ ] Payments: record Cash/UPI/Card; amount above balance is refused; invoice prints
- [ ] Services and Service bookings: add, change quantity, remove; invoice shows service charges
- [ ] Feedback list shows average rating
- [ ] Employees and User accounts: ADMIN can open; a STAFF account gets *Access denied*
- [ ] User accounts: create STAFF, deactivate (that user can no longer log in), change role, reset password
- [ ] In MySQL run the last query of `verify_schema.sql`: it must return **0 rows**

---

## 9. Troubleshooting

| Symptom | Likely cause / fix |
|---|---|
| `ClassNotFoundException: jakarta.servlet...` or 404 for everything | Tomcat is 9 or older, or XAMPP's Tomcat - use **Tomcat 10.1** |
| `Communications link failure` / `Access denied for user` | MySQL not running, wrong `db.properties`, or wrong port |
| `Unknown database` / `Table '...' doesn't exist` | Database name or table names differ (section 3) |
| `SCHEMA CHECK found N difference(s)` in the log | Read the listed columns; a missing table/column means the entity name must match your database (section 3) |
| `HHH000511 ... version ... no longer supported` | Dialect/server mismatch: set `DB_DIALECT` (section 3) |
| Tomcat warns about a JDBC driver or `mysql-cj-abandoned-connection-cleanup` thread on stop | Should no longer appear (cleanup is in `AppInitListener.contextDestroyed`); if it does, send the full line |
| `Unable to find jakarta.servlet.jsp.jstl` | Run *Clean and Build* with internet access once |
| Login says invalid for sample users | Their hashes are not BCrypt - create an admin (section 5) and reset passwords |
| 403 after submitting a form | Session expired; reload the page and retry |
| Build error `release version 17 not supported` | NetBeans is using JDK 8 - set JDK 17 |

---

## 10. Upload to GitHub

1. Install Git and create an empty repository on github.com (no README).
2. In the project folder:
```
git init
git add .
git commit -m "Hotel Management System"
git branch -M main
git remote add origin https://github.com/<your-username>/<repo-name>.git
git push -u origin main
```
3. **Check before pushing:** `git status` must **not** list `db.properties`, `.env` or `target/`. If you ever committed a password, change that password - removing the file later does not remove it from Git history.

**GitHub Pages cannot host this application.** Pages only serves static files (HTML/CSS/JS). A Java Servlet/JSP application needs a server running Tomcat (or any Servlet container), and a MySQL database reachable from that server.

---

## 11. Deploy online (public URL)

You need two things: **(a) a host that runs the WAR/Docker image** and **(b) a MySQL database on the internet**.

### Recommended student route: Docker host + hosted MySQL

The included `Dockerfile` builds the WAR with Maven and runs it on Tomcat 10.1 (it is the ROOT application, so the site is at the root URL).

**(b) Database** - one option is **Aiven for MySQL free plan**: when last checked, no credit card was needed and the free plan was a single small node (about 1 GB storage). Create the service, then import your data with phpMyAdmin export -> `mysql` command / MySQL Workbench import into a database named `hotel_management` (or the name Aiven gives you). Copy the host, port, user and password.
Then set `DB_URL` like `jdbc:mysql://HOST:PORT/DBNAME?sslMode=REQUIRED&serverTimezone=UTC` (many hosted MySQL services require SSL).

**(a) Web host** - one option is **Render** ("Web Service" -> your GitHub repo -> language *Docker*). Add environment variables in its dashboard: `DB_URL`, `DB_USER`, `DB_PASSWORD`, and for the very first start `ADMIN_USERNAME`, `ADMIN_PASSWORD` (remove them afterwards).

### Honest limits (checked in October 2026 - verify on the providers' sites, they change)
- **Render free web service:** sleeps after about 15 minutes without traffic and takes roughly a minute to wake; about 512 MB RAM and a small CPU share. Tomcat + Hibernate runs in that, but slowly. A paid instance (from about US$7/month) removes the sleeping. Render's own databases were Postgres in what I found, so use an external MySQL.
- **Free MySQL plans** are small and can change or be limited at any time; do not keep the only copy of important data there. Keep a `mysqldump` backup.
- Other ways: a VPS (install Tomcat 10.1 + MySQL yourself, copy `HotelManagement.war` to `webapps/`), or any host that accepts Docker images. All need either a paid plan or accept the free-tier limits above.
- I have **not** deployed this project, so treat these steps as a guide and expect to adjust.

### Production changes to remember
- Use HTTPS (hosts usually provide it). The Docker start script trusts the proxy headers.
- Never use the MySQL `root` user online - create a limited user for this database.
- Remove `ADMIN_*` variables after the first admin exists; use strong passwords.
- To build the WAR yourself: `mvn clean package` -> `target/HotelManagement.war`.

---

## 12. Known limitations
- Not compiled with Maven or run against MySQL by the author (section 0).
- One write transaction runs at a time inside a single Tomcat (simple and safe for a college project). Row locks protect against double booking if you ever run several servers.
- Prices come from the current room-type price (the database stores no historical price per booking).
- Hotel name, address and phone are placeholders in `header/footer/invoice.jsp`.
- Images are simple local SVG illustrations, not photographs.
