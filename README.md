<div align="center">

# 🚗 HexaRide: Geospatial Mobility-as-a-Service (MaaS) Engine

[![Java](https://img.shields.io/badge/Java-21-blue.svg?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg?style=for-the-badge&logo=redis)](https://redis.io/)
[![Uber H3](https://img.shields.io/badge/Uber%20H3-Geospatial-lightgrey.svg?style=for-the-badge)](https://h3geo.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

**A high-concurrency, cloud-native ride-hailing backend engineered for sub-second matching and real-time geospatial telemetry.**

> *Standard databases struggle with "Nearest Neighbor" queries at scale. HexaRide solves this by using Uber's H3 Hexagonal Hierarchical Indexing and Redis-backed spatial sharding.*

</div>

---

## 📖 Table of Contents
- [Executive Summary](#-executive-summary)
- [System Architecture](#-system-architecture)
- [Geospatial Strategy (Uber H3)](#-geospatial-strategy-uber-h3)
- [Real-Time Telemetry (WebSockets)](#-real-time-telemetry-websockets)
- [API Deep Dive](#-api-deep-dive)
- [Database & Persistence](#-database--persistence)
- [Installation & Setup](#-installation--setup)

---

## 🚀 Executive Summary

HexaRide is designed to handle the core challenges of modern urban mobility: **Dynamic Driver Supply** and **Real-time Rider Demand**. Unlike traditional systems that use expensive SQL `BETWEEN` queries for coordinates, HexaRide treats the world as a grid of hexagons, allowing $O(1)$ lookup for nearby drivers.

### Core Engineering Wins:
- **Low Latency:** Driver locations are cached in Redis with a 60-second TTL to ensure stale data never reaches the rider.
- **Precision Billing:** Automated fare calculation using the Haversine formula with a configurable base-rate + distance multiplier.
- **Transactional Safety:** Uses `@Transactional` and custom JPA queries to prevent "Double Booking" (one driver accepting two rides simultaneously).

---

## 📐 System Architecture

HexaRide follows a **Layered Hexagonal Architecture** (conceptually) to decouple business logic from infrastructure.



1. **Ingress Layer:** Handled by Spring Security with JWT validation.
2. **Logic Layer:** Service implementations manage the complex state machine of a Trip.
3. **Data Layer:** - **MySQL:** Permanent records of users, vehicles, and completed trips.
    - **Redis:** High-speed storage for H3-indexed driver "Buckets."

---

## ⬡ Geospatial Strategy (Uber H3)

Standard Latitude/Longitude queries ($x^2 + y^2$) are computationally expensive on relational databases. 

**HexaRide uses H3 (Resolution 7):**
- **The Concept:** Every coordinate on earth is mapped to a unique 64-bit Hexagon ID.
- **The Benefit:** To find a driver, we don't calculate distances to *all* drivers. We only look at the **Rider's Hexagon ID** and the immediate **6 neighboring Hexagons**.



**Driver Location Workflow:**
1. Driver sends `Lat: 28.61, Lng: 77.20`.
2. Service generates H3 Address: `872830828ffffff`.
3. Service adds DriverID to Redis Set: `HEXAGON:872830828ffffff`.
4. Previous Hexagon entries are automatically evicted via TTL.

---

## ⚡ Real-Time Telemetry (WebSockets)

For a seamless user experience, riders must see their driver moving on the map in real-time without refreshing.

- **Protocol:** STOMP (Simple Text Oriented Messaging Protocol) over WebSockets.
- **In-Memory Broker:** Broadcasts location packets to all subscribers of a specific Trip ID.

### WebSocket Destinations:
- **Client Subscribe:** `/topic/ride/{tripId}` (Receives `DriverLocationDTO`)
- **Driver Send:** `/app/driver/location-update` (Sends raw `Lat/Lng` to the server)

---

## 🔗 API Deep Dive

### 🔐 Authentication (`Auth/User Controller`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/auth/signup` | Creates a user with hashed password (BCrypt). |
| `POST` | `/api/v1/auth/login` | Authenticates and generates a signed JWT. |
| `GET` | `/api/v1/users/profile` | Returns profile + aggregate rating. |

### 🚙 Vehicle Registry (`Vehicle Controller`)
| Method | Endpoint | Payload Requirement |
| :--- | :--- | :--- |
| `POST` | `/api/v1/vehicles` | Requires `Role: DRIVER` and valid license plate. |
| `PUT` | `/api/v1/vehicles/{id}` | Updates car model or color. |

### 📅 Trip Lifecycle (`Trip Controller`)
The "State Machine" of the application.



| Method | Endpoint | Logic Highlights |
| :--- | :--- | :--- |
| `POST` | `/trips/request` | Calculates estimated fare using Haversine. |
| `PATCH` | `/trips/{id}/accept` | Uses `existsByDriverAndStatusIn` to ensure driver is free. |
| `PATCH` | `/trips/{id}/start` | Timestamped at `startedAt`. Unlocks WebSocket stream. |
| `PATCH` | `/trips/{id}/complete` | Updates driver/rider aggregate ratings. |

---

## 🧪 Mathematical Foundation: Haversine Formula

To ensure fair pricing, we calculate the Great Circle Distance. Unlike straight-line Euclidean distance, Haversine accounts for the Earth's curvature.

$$d = 2R \cdot \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta\phi}{2}\right) + \cos\phi_1\cos\phi_2\sin^2\left(\frac{\Delta\lambda}{2}\right)}\right)$$

Where:
- $\phi$ is latitude, $\lambda$ is longitude.
- $R$ is earth radius (6,371 km).

---

## 🛠 Project Setup & Installation

### Prerequisites
- **Java 21**
- **MySQL 8.0+**
- **Redis Server** (Port 6379)
- **Maven 3.9+**

### Steps
1. **Clone & Install:**
   ```bash
   git clone [https://github.com/lakshya/hexaRide.git](https://github.com/lakshya/hexaRide.git)
   mvn clean install

## 💾 Database & Persistence

HexaRide employs a dual-database strategy to balance ACID compliance with high-speed geospatial indexing.

### 1. Relational Schema (MySQL)
Managed via **Flyway Migrations**, the schema is designed for referential integrity.
- **`users`**: Stores PII and roles.
- **`vehicles`**: Linked 1:1 with drivers.
- **`trips`**: Detailed audit log of every journey, including timestamps and finalized fares.



### 2. In-Memory Store (Redis)
Used for the "Hot Data" that changes every second.
- **Data Structure**: `Sets`
- **Key Pattern**: `HEXAGON:{h3_index}` -> Stores a list of active `driver_id`s in that specific cell.
- **Lifecycle**: Each entry has a 60-second TTL. This ensures that if a driver's app crashes, they are automatically removed from the "Available" pool within a minute.

---

## 📡 WebSocket Telemetry Flow

The real-time tracking is built on **STOMP** to provide a pub/sub model for location packets.

| Component | Responsibility |
| :--- | :--- |
| **Broker** | Manages client connections at `/ws-ride`. |
| **Inbound** | Drivers push coordinates to `/app/driver/location`. |
| **Outbound** | Server broadcasts to `/topic/ride/{tripId}`. |



---

## ⚠️ Robust Exception Handling

HexaRide implements a **Global Advice** pattern to ensure the frontend always receives a structured JSON error response instead of a raw stack trace.

| Exception | HTTP Status | Scenario |
| :--- | :--- | :--- |
| `ResourceNotFoundException` | `404 Not Found` | Invalid User/Trip ID. |
| `InvalidCredentialException` | `409 Conflict` | Driver trying to accept a ride while on another trip. |
| `ResourceAlreadyExistsException` | `400 Bad Request` | Email or Vehicle Number already registered. |

**Sample Error Response:**
```json
{
  "timestamp": "2026-01-19T01:40:00Z",
  "message": "Driver is already on another active trip.",
  "status": 409
}
