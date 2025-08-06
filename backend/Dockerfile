# ------------------------------
# Build stage: use a full JDK + Maven to compile the Spring Boot app
# ------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Create working directory inside container
WORKDIR /app

# Copy the entire back-end source tree (including Maven wrapper)
COPY . .

# Build the jar (tests are skipped to speed up image builds)
RUN ./mvnw -B clean package -DskipTests

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
