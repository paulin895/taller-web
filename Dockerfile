FROM eclipse-temurin:17

# Instalamos Maven
RUN apt-get update && apt-get install -y maven

# Creamos el directorio de trabajo y copiamos todo el repo
WORKDIR /app
COPY . .

# Entramos a la subcarpeta donde está el pom.xml y compilamos
WORKDIR /app/tallerweb
RUN mvn clean install

# Comando para ejecutar tu app
CMD ["java", "-jar", "target/tallerweb-0.0.1-SNAPSHOT.jar"]
