# ------------------------------
# Build stage: use a full JDK + Maven to compile the Spring Boot app
# ------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory inside container
WORKDIR /app

# Copy ONLY the backend module into the image (faster build, ensures mvnw is at /app)
COPY backend/ .

# Make sure the Maven wrapper is executable and build the jar (skip tests to speed things up)
RUN chmod +x mvnw && ./mvnw -B clean package -DskipTests

# ------------------------------
# Run stage: slim JRE image to actually run the jar
# ------------------------------
FROM eclipse-temurin:17-jre-alpine

# Create a non-root user for better security (optional but recommended)
# RUN addgroup -S spring && adduser -S spring -G spring
# USER spring

WORKDIR /app

# Copy only the built artifact from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Render (and most PaaS platforms) provide the port via $PORT
ENV PORT=8080
EXPOSE 8080

# Launch the Spring Boot application
CMD ["java", "-jar", "app.jar", "--server.port=${PORT}"]
