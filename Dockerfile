# Use a reliable JDK image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy the entire project
COPY . .

# Compile the Java code
RUN mkdir -p bin && javac -d bin src/*.java src/controllers/*.java src/dao/*.java src/models/*.java src/services/*.java src/utils/*.java

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-cp", "bin", "src.Main"]
