<div align="center">

# 🚗 HexaRide: Geospatial Mobility-as-a-Service Engine

[![Java](https://img.shields.io/badge/Java-21-blue.svg?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-green.svg?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg?style=for-the-badge&logo=redis)](https://redis.io/)
[![Uber H3](https://img.shields.io/badge/Uber%20H3-Geospatial-lightgrey.svg?style=for-the-badge)](https://h3geo.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

**A high-concurrency, cloud-native ride-hailing backend engineered for sub-second matching and real-time geospatial telemetry using Uber's H3 Hexagonal Indexing.**

> *Standard databases struggle with "Nearest Neighbor" queries at scale. HexaRide solves this using mathematical hexagonal tiling and Redis-backed spatial sharding for the future of urban mobility.*

</div>

---

## 🌟 Key Features

- ⬡ **Uber H3 Geospatial Indexing**: Maps GPS coordinates to hexagonal cells (Resolution 7) for $O(1)$ driver discovery.
- ⚡ **Real-time Telemetry**: STOMP over WebSockets for live driver-to-rider location broadcasting.
- 🔐 **Secure Identity**: Role-based access control (`ROLE_RIDER`, `ROLE_DRIVER`) with JWT and BCrypt protection.
- 📅 **Trip Lifecycle Engine**: Robust state management (`REQUESTED` → `ACCEPTED` → `STARTED` → `COMPLETED`) with transactional integrity.
- 🏎️ **High-Speed Caching**: Redis-backed location storage with automatic TTL for data freshness and low-latency lookups.
- 📐 **Precision Billing**: Automated fare calculation using the **Haversine Formula** for great-circle distances.

---

## ⬡ The H3 Advantage

Traditional ride-hailing systems use expensive SQL `BETWEEN` queries for latitude and longitude, which degrade as the database grows. HexaRide uses **H3 Resolution 7** hexagons to treat the world as a discrete grid.

1. **Spatial Sharding**: Every driver is indexed in a Redis Set keyed by their Hexagon ID.
2. **Neighbor Search**: Matching lookups check the rider's cell + immediate neighbors (k-ring traversal).
3. **Efficiency**: Reduces millions of potential distance calculations down to a simple set lookup.



---

## 📡 Real-Time Telemetry (WebSockets)

HexaRide provides a seamless experience where riders see their driver moving on the map in real-time without page refreshes.

- **Protocol**: STOMP over WebSockets.
- **Connection URL**: `ws://localhost:8080/ws-ride`
- **Inbound**: Drivers publish coordinates to `/app/driver/location`.
- **Outbound**: Server broadcasts location packets to subscribers of `/topic/ride/{tripId}`.



---

## 🔗 API Documentation

### 🔐 1. Authentication & Users
| Method | Endpoint | Purpose | Security |
| :--- | :--- | :--- | :--- |
| **`POST`** | `/api/v1/auth/signup` | Register a new User (Rider/Driver) | Public |
| **`POST`** | `/api/v1/auth/login` | Authenticate and get JWT Token | Public |
| **`GET`** | `/api/v1/users/{id}` | Fetch user profile details | Private (JWT) |

### 🚙 2. Vehicle Management
| Method | Endpoint | Purpose | Role |
| :--- | :--- | :--- | :--- |
| **`POST`** | `/api/v1/vehicles` | Register a vehicle (Plate, Model) | `DRIVER` |
| **`GET`** | `/api/v1/vehicles/driver/{id}` | Get info for a specific driver | Public |

### 📅 3. Trip Management (State Machine)
| Method | Endpoint | Purpose | Status Change |
| :--- | :--- | :--- | :--- |
| **`POST`** | `/api/v1/trips/request` | Rider requests a ride | `REQUESTED` |
| **`PATCH`** | `/api/v1/trips/{id}/accept` | Driver claims the request | `ACCEPTED` |
| **`PATCH`** | `/api/v1/trips/{id}/start` | Trip starts (Updates telemetry) | `STARTED` |
| **`PATCH`** | `/api/v1/trips/{id}/complete`| Finalizes fare and status | `COMPLETED` |

---

## ⚠️ Exception Handling

All APIs use a **centralized global exception handler** for consistent error management:

- **`ResourceNotFoundException`**: Invalid IDs → 404.
- **`ConflictException`**: Overlapping trips or duplicate data → 409.
- **`ValidationException`**: Invalid input data → 400.

Sample error response:
```json
{
  "message": "Driver is already on an active trip.",
  "status": 409,
  "error": "CONFLICT",
  "timeStamp": "2026-01-19T12:00:00Z"
}
