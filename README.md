# AgroGuardian 🌱🚀

Akıllı tarım uygulamaları için geliştirilmiş, uçtan uca izleme ve otomasyon sağlayan bir sistem.

## Özellikler

- **Gerçek zamanlı sensör verisi takibi** (sıcaklık, nem, toprak nemi)
- ** Sulama ve Havalandırma kontrolü**
- **iOS (Apple Watch desteği) ve Android için native mobil uygulamalar**
- **Bildirim altyapısı (Firebase)**
- **Konteyner tabanlı altyapı (Docker Compose)**
- **Python tabanlı IoT emülatörleri (Automate & Observer)**

---

## Mimarinin Genel Görünümü

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

## Kullanılan Teknolojiler

- **Backend:** Java 17, Spring Boot, REST API, MQTT, RabbitMQ, PostgreSQL, InfluxDB, Firebase
- **Mobil:**  
  - iOS: Swift, SwiftUI, Apple Watch, Firebase  
  - Android: Kotlin, Jetpack Compose, Firebase
- **IoT Emülatörleri:** Python, paho-mqtt
- **Altyapı:** Docker Compose, Mosquitto (MQTT broker)

---

## Hızlı Başlangıç

### Gereksinimler
- Docker & Docker Compose
- Java 17
- Python 3.x
- Android Studio / Xcode (mobil geliştirme için)

### Servisleri Başlatmak

```bash
cd agroguardian_backend
docker-compose up -d
```

### IoT Emülatörlerini Çalıştırmak

Her emülatör için ayrı terminalde:

```bash
# Automate
cd agroguardian_automate
python main.py

# Observer
cd adroguardian_observer
python main.py
```

### Mobil Uygulamalar

- **iOS:** `agroguardian_app_ios` dizinini Xcode ile açıp çalıştırabilirsiniz.
- **Android:** `agroguardian_app_android` dizinini Android Studio ile açıp çalıştırabilirsiniz.

---

## Katkı ve Lisans

Katkıda bulunmak isterseniz lütfen bir issue açın veya pull request gönderin.  
Bu proje MIT lisansı ile lisanslanmıştır.