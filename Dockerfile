FROM eclipse-temurin:17

# Instalar Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY . .

RUN mvn clean install

CMD ["java", "-jar", "target/tallerweb-0.0.1-SNAPSHOT.jar"]