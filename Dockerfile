# Use a lightweight JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built JAR file into the image
COPY target/QuizCraft-0.0.1-SNAPSHOT.jar app.jar


# Let Railway set the port (important!)
ENV PORT=8080

# Expose the port
EXPOSE ${PORT}

# Run the app with dynamic port binding
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
