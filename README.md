# AgroGuardian 🌱🚀

A smart agriculture system providing end-to-end monitoring and automation for modern farming practices.

---

## 🚀 Features

- **Real-time sensor data tracking** (temperature, humidity, soil moisture)
- **Irrigation and ventilation control**
- **Native mobile apps for iOS (Apple Watch support) and Android**
- **Notification system using Firebase**
- **Containerized infrastructure (Docker Compose)**
- **Python-based IoT emulators (Automate & Observer)**

---

## 🧭 Architecture Overview


```
+-------------------+      MQTT      +-------------------+      REST      +-------------------+
|   IoT Emulators   | <------------> |     Backend       | <-----------> |   Mobile Apps     |
| (Automate/Observer)|               | (Spring Boot)     |               | (iOS & Android)   |
+-------------------+                +-------------------+               +-------------------+
         |                                 |   |   |   |
         |                                 |   |   |   |
         |                                 v   v   v   v
         |                        +--------+---+---+---+--------+
         |                        | PostgreSQL | InfluxDB |     |
         |                        | RabbitMQ   | Mosquitto|     |
         |                        +-----------------------+     |
         +------------------------------------------------------+
```

---


## 🛠 Technologies Used

### Backend
- Java 17
- Spring Boot
- REST API
- MQTT (Mosquitto)
- RabbitMQ
- PostgreSQL
- InfluxDB
- Firebase

### Mobile Applications
- **iOS:** Swift, SwiftUI, Apple Watch support, Firebase
- **Android:** Kotlin, Jetpack Compose, Firebase

### IoT Emulators
- Python 3.x
- `paho-mqtt` library

### Infrastructure
- Docker & Docker Compose

---

## ⚡️ Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17
- Python 3.x
- Android Studio / Xcode (for mobile development)

---

### 🔧 Launch Services


```bash
cd agroguardian_backend
docker-compose up -d
```

### Running IoT Emulators

In a separate terminal for each emulator:

```bash
# Automate
cd agroguardian_automate
python main.py

# Observer
cd adroguardian_observer
python main.py
```

### Mobile Applications

- **iOS:** You can open and run the `agroguardian_app_ios` directory with Xcode.
- **Android:** You can open and run the `agroguardian_app_android` directory with Android Studio.

---

## Contribution and License

If you would like to contribute, please open an issue or submit a pull request.
This project is licensed under the MIT license.
