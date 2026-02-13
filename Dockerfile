# Stage 1: Build the native image
FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /app

# Copy the project files
COPY . .

# Fix windows line endings in mvnw script if built from windows source
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Build the native image
# The binary will be created in /app/target/allpad-api
RUN ./mvnw -Pnative native:compile -B --no-transfer-progress

# Stage 2: Create the runtime image
FROM debian:bookworm-slim

WORKDIR /app

# Copy the binary from the builder stage
COPY --from=builder /app/target/allpad-api .

# Expose the port the app runs on
EXPOSE 8080

# Run the binary
ENTRYPOINT ["./allpad-api"]
