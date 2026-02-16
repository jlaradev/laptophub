# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy project files
COPY . .

# Build the jar (skip tests for faster builds on Render)
RUN ./mvnw -q -DskipTests clean package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
