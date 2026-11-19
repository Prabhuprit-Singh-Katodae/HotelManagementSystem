# ---------- Stage 1: build the WAR with Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Stage 2: run it on Tomcat 10.1 ----------
FROM tomcat:10.1-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/HotelManagement.war /usr/local/tomcat/webapps/ROOT.war
COPY docker/start.sh /start.sh
RUN chmod +x /start.sh
# Database settings come from environment variables: DB_URL, DB_USER, DB_PASSWORD
# (and optionally ADMIN_USERNAME / ADMIN_PASSWORD for the first admin). Never put them in this file.
ENV JAVA_OPTS="-Xmx320m -Duser.timezone=Asia/Kolkata"
EXPOSE 8080
CMD ["/start.sh"]
