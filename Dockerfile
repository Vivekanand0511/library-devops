# Use the official Eclipse Temurin JDK 21 image
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Java source code into the container
COPY LibraryApp.java /app

# Compile the Java application
RUN javac LibraryApp.java

# Make port 8081 available to the outside
EXPOSE 8081

# Run the application
CMD ["java", "LibraryApp"]