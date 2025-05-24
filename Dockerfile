# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/QuizCraft-0.0.1-SNAPSHOT.jar app.jar

# Let Railway set the port
ENV PORT=8080

# Expose the port
EXPOSE ${PORT}

# Run the app with dynamic port binding
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
