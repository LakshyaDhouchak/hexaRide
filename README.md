#!/bin/bash

# hexaRide Complete Setup and Run Script
# This script automates the entire process: cloning, DB setup, env config, build, run, and basic testing.
# Prerequisites: Java 21, MySQL 8.0+, Redis 6.0+, Maven 3.8+, Git, curl (for testing)
# Run as: ./complete-setup-hexaRide.sh
# Note: Update placeholders (DB_USER, etc.) before running.

set -e  # Exit on error

# --- Configuration (UPDATE THESE!) ---
REPO_URL="https://github.com/your-username/hexaRide.git"  # Replace with actual GitHub URL
DB_NAME="hexaride"
DB_USER="your_mysql_username"  # e.g., root or a user
DB_PASS="your_mysql_password"  # e.g., password123
MYSQL_ROOT_PASS="your_mysql_root_password"  # If DB_USER is root, use this
JWT_SECRET="your_base64_encoded_jwt_secret"  # Generate with: openssl rand -base64 32
REDIS_HOST="localhost"  # Default Redis host
REDIS_PORT="6379"  # Default Redis port

# --- Step 1: Clone Repository ---
echo "=== Step 1: Cloning hexaRide Repository ==="
if [ ! -d "hexaRide" ]; then
    git clone "$REPO_URL"
else
    echo "Repository already exists. Pulling latest changes..."
    cd hexaRide && git pull && cd ..
fi
cd hexaRide

# --- Step 2: Set Up MySQL Database ---
echo "=== Step 2: Setting Up MySQL Database ==="
mysql -u "$DB_USER" -p"$DB_PASS" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;" 2>/dev/null || {
    echo "MySQL connection failed. Ensure MySQL is running and credentials are correct."
    exit 1
}

# Create tables and alter as per provided SQL
mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" <<EOF
CREATE TABLE IF NOT EXISTS users(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(225) NOT NULL,
    email VARCHAR(225) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(225) NOT NULL,
    role ENUM('RIDER','DRIVER') NOT NULL,
    rating DECIMAL(3,2) DEFAULT 5.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicles(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT UNIQUE NOT NULL,
    vehicle_number VARCHAR(50) UNIQUE NOT NULL,
    model VARCHAR(100) NOT NULL,
    color VARCHAR(50),
    vehicle_type ENUM('SEDAN','SUV','LUXURY','BIKE') NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trips(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rider_id BIGINT NOT NULL,
    driver_id BIGINT DEFAULT NULL,
    status ENUM('REQUESTED','ACCEPTED','ARRIVED','IN_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
    pickup_lat DOUBLE NOT NULL,
    pickup_lng DOUBLE NOT NULL,
    dropoff_lat DOUBLE NOT NULL,
    dropoff_lng DOUBLE NOT NULL,
    fare DECIMAL(10,2),
    distance_km DECIMAL(10,2),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (rider_id) REFERENCES users(id),
    FOREIGN KEY (driver_id) REFERENCES users(id)
);

ALTER TABLE trips 
MODIFY COLUMN status ENUM('REQUESTED', 'ACCEPTED', 'ARRIVED', 'STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL;
EOF
echo "Database setup complete."

# --- Step 3: Configure Environment ---
echo "=== Step 3: Configuring Environment Variables ==="
export DB_USERNAME="$DB_USER"
export DB_PASSWORD="$DB_PASS"
export JWT_SECRET="$JWT_SECRET"
export SPRING_REDIS_HOST="$REDIS_HOST"
export SPRING_REDIS_PORT="$REDIS_PORT"

# Create .env file
cat > .env <<EOF
DB_USERNAME=$DB_USER
DB_PASSWORD=$DB_PASS
JWT_SECRET=$JWT_SECRET
SPRING_REDIS_HOST=$REDIS_HOST
SPRING_REDIS_PORT=$REDIS_PORT
EOF
echo "Environment configured."

# --- Step 4: Install Dependencies and Build ---
echo "=== Step 4: Building the Project ==="
mvn clean install -DskipTests  # Skip tests for speed; run 'mvn test' separately if needed
echo "Build complete."

# --- Step 5: Start Redis (if not running) ---
echo "=== Step 5: Ensuring Redis is Running ==="
if ! pgrep -x "redis-server" > /dev/null; then
    echo "Starting Redis..."
    redis-server --daemonize yes  # Assumes Redis is installed
else
    echo "Redis is already running."
fi

# --- Step 6: Run the Application ---
echo "=== Step 6: Starting hexaRide Application ==="
mvn spring-boot:run &
APP_PID=$!
echo "App started with PID: $APP_PID. Waiting for startup..."
sleep 15  # Wait for app to fully start

# --- Step 7: Verify and Test ---
echo "=== Step 7: Verifying Setup and Running Tests ==="

# Health Check
if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
    echo "✓ Health check passed."
else
    echo "✗ Health check failed. Check logs."
fi

# Test Signup
echo "Testing user signup..."
SIGNUP_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/users/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Rider","email":"rider@example.com","password":"password123","phone":"1234567890","role":"RIDER"}')
if echo "$SIGNUP_RESPONSE" | grep -q "id"; then
    echo "✓ Signup test passed."
else
    echo "✗ Signup test failed: $SIGNUP_RESPONSE"
fi

# Test Login
echo "Testing login..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"rider@example.com","password":"password123"}')
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
if [ -n "$TOKEN" ]; then
    echo "✓ Login test passed. Token: ${TOKEN:0:20}..."
else
    echo "✗ Login test failed: $LOGIN_RESPONSE"
fi

# Test Protected Endpoint (Get Profile)
if [ -n "$TOKEN" ]; then
    echo "Testing protected endpoint (get profile)..."
    PROFILE_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/users/1)
    if echo "$PROFILE_RESPONSE" | grep -q "email"; then
        echo "✓ Profile fetch test passed."
    else
        echo "✗ Profile fetch test failed: $PROFILE_RESPONSE"
    fi
fi

# Test Trip Request (if user exists)
if [ -n "$TOKEN" ]; then
    echo "Testing trip request..."
    TRIP_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v1/trips/request \
      -H "Authorization: Bearer $TOKEN" \
      -H "Content-Type: application/json" \
      -d '{"riderId":1,"preferredVehicleType":"SEDAN","pickupLat":37.7749,"pickupLng":-122.4194,"dropoffLat":37.7849,"dropoffLng":-122.4094}')
    if echo "$TRIP_RESPONSE" | grep -q "id"; then
        echo "✓ Trip request test passed."
    else
        echo "✗ Trip request test failed: $TRIP_RESPONSE"
    fi
fi

# --- Step 8: Cleanup and Instructions ---
echo "=== Setup Complete ==="
echo "hexaRide is running on http://localhost:8080"
echo "To stop the app: kill $APP_PID"
echo "To run tests manually: mvn test"
echo "Check logs: tail -f target/spring.log (if configured)"
echo "WebSocket endpoint: ws://localhost:8080/ws-trip"
echo "For production, update configs in application.properties and use a reverse proxy."
echo "If issues persist, ensure all prerequisites are installed and ports are free."

# Keep script running to monitor (optional)
# wait $APP_PID
