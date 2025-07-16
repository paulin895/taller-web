FROM eclipse-temurin:17
WORKDIR /app
COPY . .
RUN ./mvnw clean install || mvn clean install
CMD ["java", "-jar", "target/tallerweb-0.0.1-SNAPSHOT.jar"]
