# Use a lightweight JDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the entire project
COPY . .

# Compile the Java code
RUN mkdir -p bin && javac -d bin src/*.java src/controllers/*.java src/dao/*.java src/models/*.java src/services/*.java src/utils/*.java

# Expose the port (Render uses $PORT, but we'll default to 8080)
EXPOSE 8080

# Run the application
CMD ["java", "-cp", "bin", "src.Main"]
