# 🏗️ SmartOrder - Mikroservis E-Ticaret Platformu

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-blue)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-326CE5)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

> **Eğitimsel Amaçlı Gelişmiş Java Mikroservis Projesi**  
> Domain-Driven Design (DDD), Hexagonal Architecture, CQRS, Event-Driven Architecture ve Cloud-Native prensiplerinin uygulandığı kapsamlı bir öğrenme projesidir.

---

## 📚 İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Mimari Yaklaşım](#-mimari-yaklaşım)
- [Servisler ve Rolleri](#-servisler-ve-rolleri)
- [Teknoloji Stack](#-teknoloji-stack)
- [Ön Gereksinimler](#-ön-gereksinimler)
- [Hızlı Başlangıç](#-hızlı-başlangıç)
- [Backend Servisleri Çalıştırma](#-backend-servisleri-çalıştırma)
- [Kubernetes Deployment](#-kubernetes-deployment)
- [Kubernetes Konfigürasyonları](#-kubernetes-konfigürasyonları)
- [API Endpoint'leri](#-api-endpointleri)
- [Keycloak Entegrasyonu](#-keycloak-entegrasyonu)
- [Kafka Event Streaming](#-kafka-event-streaming)
- [Database Yapısı](#-database-yapısı)
- [Monitoring ve Observability](#-monitoring-ve-observability)
- [Troubleshooting](#-troubleshooting)

---

## 🎯 Proje Hakkında

**SmartOrder**, modern mikroservis mimarisi prensiplerini uygulayan, gerçek dünya senaryolarını simüle eden kapsamlı bir e-ticaret platformudur. Bu proje, aşağıdaki konuları öğrenmek ve uygulamak için tasarlanmıştır:

### Temel Amaçlar

✅ **Domain-Driven Design (DDD)**: İş mantığını domain katmanında izole ederek, temiz ve sürdürülebilir kod yazımı  
✅ **Hexagonal Architecture (Ports & Adapters)**: Bağımlılıkları tersine çevirerek test edilebilir ve esnek yapı  
✅ **CQRS Pattern**: Command ve Query sorumluluklarının ayrılması ile performans optimizasyonu  
✅ **Event-Driven Architecture**: Asenkron iletişim ve gevşek bağlılık (loose coupling)  
✅ **Cloud-Native Patterns**: Config Server, Service Discovery, API Gateway, Circuit Breaker  
✅ **Security**: OAuth 2.0 / OIDC ile Keycloak entegrasyonu, JWT token yönetimi  
✅ **Containerization**: Docker ve Kubernetes ile deployment  
✅ **Observability**: Actuator, Health Checks, Prometheus metrikleri  

### İş Akışı

1. **Kullanıcı Keycloak üzerinden kimlik doğrulama yapar**
2. **BFF (Backend for Frontend)** üzerinden Angular frontend'e token sağlanır
3. **API Gateway** istekleri ilgili mikroservislere yönlendirir
4. **Catalog Service**: Ürün bilgilerini yönetir
5. **Order Service**: Sipariş oluşturur ve Kafka'ya event yayınlar
6. **Payment Service**: Ödeme işlemlerini gerçekleştirir (idempotency kontrolü ile)
7. **Notification Service**: Kullanıcılara bildirim gönderir
8. **Event-Driven Communication**: Servisler arası asenkron mesajlaşma

---

## 🏛️ Mimari Yaklaşım

### Domain-Driven Design (DDD)

Proje, Eric Evans'ın DDD prensiplerini takip eder:

- **Aggregate Root**: `Order`, `Product`, `PaymentRequest` gibi iş nesneleri
- **Value Objects**: `Money`, `OrderId`, `ProductId` gibi immutable record'lar
- **Domain Events**: `OrderCreated`, `PaymentSucceeded`, `PaymentFailed`
- **Ubiquitous Language**: İş terminolojisinin kod içinde tutarlı kullanımı

### Hexagonal Architecture (Ports & Adapters)

Her servis 4 ana katmandan oluşur:

```
├── domain/           → Pure Java, framework bağımsız iş mantığı
│   ├── model/        → Aggregates, Entities, Value Objects
│   ├── event/        → Domain Events
│   └── port/         → Interface'ler (Repository, EventPublisher)
│
├── application/      → Use-case orchestration
│   ├── command/      → Write operations (Create, Update, Delete)
│   ├── query/        → Read operations (Get, List, Search)
│   └── service/      → Application services
│
├── api/              → Dış dünyaya açılan HTTP endpoints
│   └── rest/         → REST Controllers (DTO kullanır)
│
└── infrastructure/   → Framework ve teknoloji detayları
    ├── adapter/      → Port implementasyonları
    │   ├── persistence/  → JdbcOrderRepositoryAdapter
    │   └── messaging/    → KafkaEventPublisherAdapter
    └── config/       → Spring Configuration
```

**Temel Prensip**: Domain katmanı hiçbir dış framework'e bağımlı değildir. Infrastructure katmanı domain'i bilir, ancak domain infrastructure'ı bilmez (Dependency Inversion).

### CQRS (Command Query Responsibility Segregation)

- **Command**: Sistem state'ini değiştirir (POST, PUT, DELETE)
- **Query**: Sadece veri okur (GET), state değiştirmez

```java
// Command örneği
application/command/CreateOrderCommand
application/command/CreateOrderHandler

// Query örneği  
application/query/GetOrderQuery
application/query/GetOrderHandler
```

### Event-Driven Architecture

Servisler arası iletişim Kafka üzerinden:

```
Order Service  →  [order.created]  →  Payment Service
Payment Service  →  [payment.succeeded]  →  Notification Service
Payment Service  →  [payment.failed]  →  Notification Service
```

**Outbox Pattern**: Domain event'ler önce veritabanına yazılır, sonra Kafka'ya publish edilir (eventual consistency garantisi).

---

## 🔧 Servisler ve Rolleri

### Infrastructure Services

| Servis | Port | Açıklama |
|--------|------|----------|
| **Config Server** | 8888 | Merkezi konfigürasyon yönetimi. Tüm servislerin config dosyalarını `configs/` klasöründen veya Git repository'sinden okur. |
| **Discovery Server** | 8761 | Netflix Eureka - Service Registry. Servislerin birbirini bulması için service discovery sağlar. |
| **API Gateway** | 8080 | Spring Cloud Gateway - Tek giriş noktası. Request routing, load balancing, security filtreleri. |
| **BFF (Backend for Frontend)** | 8070 | OAuth2 Login/Logout, token management, Angular frontend için proxy. |

### Business Services

| Servis | Port | Veritabanı | Açıklama |
|--------|------|------------|----------|
| **Catalog Service** | 8084 | H2 (catalog.mv.db) | Ürün kataloğu yönetimi (CRUD operations). OAuth2 Resource Server. |
| **Order Service** | 8081 | H2 (order.mv.db) | Sipariş oluşturma ve yönetimi. Catalog'dan fiyat bilgisi alır, Kafka'ya event yayınlar. |
| **Payment Service** | 8082 | H2 (payment.mv.db) | Ödeme işlemleri. Idempotency kontrolü ile duplicate işlem önleme. |
| **Notification Service** | 8083 | H2 (notification.mv.db) | Kafka event'lerini dinler, kullanıcılara bildirim gönderir (simüle). |

### External Services (Docker Compose)

| Servis | Port | Açıklama |
|--------|------|----------|
| **Keycloak** | 8585 | Identity & Access Management. OAuth 2.0 / OIDC provider. |
| **Keycloak DB** | 5432 | PostgreSQL - Keycloak'ın veritabanı. |
| **Kafka** | 9092 (internal), 29092 (external) | Event streaming platform (KRaft mode). |
| **Kafka UI** | 8085 | Kafka yönetim arayüzü. Topic'leri, mesajları görüntüleme. |

---

## 🛠️ Teknoloji Stack

### Backend Core

- **Java 21**: Latest LTS version
- **Spring Boot 3.5.6**: Core framework
- **Spring Cloud 2025.0.0**: Mikroservis altyapısı
- **Maven**: Dependency management (multi-module project)

### Spring Cloud Components

```xml
├── Spring Cloud Config          → Centralized configuration
├── Spring Cloud Netflix Eureka  → Service discovery
├── Spring Cloud Gateway         → API Gateway
├── Spring Cloud Stream          → Event-driven messaging (Kafka binder)
├── Spring Cloud LoadBalancer    → Client-side load balancing
└── Spring Security OAuth2       → Resource Server & Client
```

### Persistence & Messaging

- **Spring Data JDBC**: Database access (DDD-friendly, JPA'ya göre daha lightweight)
- **H2 Database**: File-based embedded database (her servis kendi DB'sine sahip)
- **Apache Kafka**: Event streaming (Bitnami image, KRaft mode)
- **Schema.sql**: Manuel schema yönetimi (Flyway kullanılmıyor)

### Security & Identity

- **Keycloak 24.0.5**: OIDC/OAuth2 provider
- **Spring Security OAuth2 Resource Server**: JWT token validation
- **Spring Security OAuth2 Client**: BFF için Authorization Code + PKCE flow

### Observability

- **Spring Boot Actuator**: Health, metrics, info endpoints
- **Prometheus-compatible metrics**: `/actuator/prometheus`
- **Liveness, Readiness, Startup Probes**: Kubernetes health checks

### Code Quality & Mapping

- **Lombok**: Boilerplate code reduction (@Data, @Builder, @Slf4j)
- **MapStruct**: Type-safe bean mapping (Domain ↔ DTO)

### Frontend

- **Angular 19+**: TypeScript-based SPA
- **Tailwind CSS**: Utility-first CSS framework

---

## 📋 Ön Gereksinimler

### Geliştirme Ortamı

- **Java Development Kit (JDK) 21**: [Oracle](https://www.oracle.com/java/technologies/downloads/) veya [OpenJDK](https://adoptium.net/)
- **Apache Maven 3.9+**: [Download](https://maven.apache.org/download.cgi)
- **Docker Desktop**: [Windows](https://docs.docker.com/desktop/install/windows-install/) | [Mac](https://docs.docker.com/desktop/install/mac-install/)
- **Git**: [Download](https://git-scm.com/)
- **Node.js 20+ & npm**: Angular frontend için (opsiyonel)

### Kubernetes (Opsiyonel)

- **kubectl**: [Install](https://kubernetes.io/docs/tasks/tools/)
- **Minikube** / **Docker Desktop Kubernetes** / **Kind**: Yerel K8s cluster

### IDE (Önerilen)

- **IntelliJ IDEA Ultimate** (Spring Boot desteği ile)
- **VS Code** + Java Extension Pack

---

## 🚀 Hızlı Başlangıç

### 1. Repository'yi Klonlayın

```bash
git clone https://github.com/halitkalayci/java-advanced-project3.git
cd java-advanced-project3
```

### 2. External Services'i Başlatın (Docker Compose)

```bash
# Keycloak, Kafka, Kafka UI ve PostgreSQL başlatılır
docker-compose up -d

# Logları takip edin
docker-compose logs -f
```

**Bekleme süresi**: Keycloak'ın başlaması ~30 saniye, Kafka ~10 saniye sürebilir.

### 3. Servislerin Hazır Olduğunu Kontrol Edin

```bash
# Kafka sağlık kontrolü
docker-compose exec kafka /opt/bitnami/kafka/bin/kafka-topics.sh \
  --bootstrap-server kafka:9092 --list

# Keycloak UI (tarayıcıda açın)
http://localhost:8585
# Admin credentials: admin / admin
```

### 4. Keycloak Konfigürasyonu

**Realm ve Client oluşturma için**: [`KEYCLOAK-SETUP.md`](./KEYCLOAK-SETUP.md) dosyasını takip edin.

Özet:
- Realm: `smartorder`
- Client: `smartorder-bff` (Authorization Code + PKCE)
- Client: `smartorder-angular` (Public client)
- Kullanıcı oluşturun: `testuser` / şifre belirleyin

---

## 🏃 Backend Servisleri Çalıştırma

### Maven ile Build

```bash
# Tüm modülleri build edin (root dizinde)
cd backend
mvn clean install -DskipTests

# Tek bir modül build etmek için
cd catalog-service
mvn clean package
```

### Servisleri Başlatma Sırası

**ÖNEMLİ**: Servisler arasında bağımlılık olduğu için sırayla başlatın:

#### 1️⃣ Config Server

```bash
cd backend/config-server
mvn spring-boot:run
```

**Doğrulama**: `http://localhost:8888/actuator/health`

#### 2️⃣ Discovery Server

```bash
cd backend/discovery-server
mvn spring-boot:run
```

**Doğrulama**: `http://localhost:8761` → Eureka Dashboard

#### 3️⃣ Business Services (paralel çalıştırılabilir)

```bash
# Terminal 1
cd backend/catalog-service
mvn spring-boot:run

# Terminal 2
cd backend/order-service
mvn spring-boot:run

# Terminal 3
cd backend/payment-service
mvn spring-boot:run

# Terminal 4
cd backend/notification-service
mvn spring-boot:run
```

#### 4️⃣ Gateway & BFF

```bash
# Terminal 5
cd backend/gateway
mvn spring-boot:run

# Terminal 6
cd backend/bff
mvn spring-boot:run
```

### Tüm Servislerin Durumunu Kontrol Etme

```bash
# Eureka Dashboard'da tüm servislerin UP olduğunu görmelisiniz
http://localhost:8761

# Her servisin health endpoint'i
curl http://localhost:8888/actuator/health  # config-server
curl http://localhost:8761/actuator/health  # discovery-server
curl http://localhost:8084/actuator/health  # catalog-service
curl http://localhost:8081/actuator/health  # order-service
curl http://localhost:8082/actuator/health  # payment-service
curl http://localhost:8083/actuator/health  # notification-service
curl http://localhost:8080/actuator/health  # gateway
curl http://localhost:8070/actuator/health  # bff
```

---

## ☸️ Kubernetes Deployment

### Ön Hazırlık: Docker Images Build

Config Server ve Discovery Server için Docker image'ları build edilmiş olmalı:

```bash
# Config Server image build
cd backend
docker build -f Dockerfile.config-server -t halitkalayci/smartorder-config-server:latest .

# Discovery Server image build
docker build -f Dockerfile.discovery-server -t halitkalayci/smartorder-discovery-server:latest .

# (Opsiyonel) DockerHub'a push
docker push halitkalayci/smartorder-config-server:latest
docker push halitkalayci/smartorder-discovery-server:latest
```

### Kubernetes Cluster Başlatma

**Minikube kullanıyorsanız**:

```bash
minikube start --cpus=4 --memory=8192
minikube status
```

**Docker Desktop Kubernetes**:

Settings → Kubernetes → Enable Kubernetes → Apply

### Config Server Deployment

```bash
kubectl apply -f k8s/config-server.yml
```

**Bu komut şunları oluşturur**:

1. **ConfigMap**: `config-server-config`
   - Environment variables (Git URI, Eureka URI, JVM options)

2. **Deployment**: `config-server`
   - 1 replica
   - Image: `halitkalayci/smartorder-config-server:latest`
   - Liveness/Readiness/Startup probes
   - Resource limits: 256Mi-512Mi RAM, 200m-500m CPU

3. **Service (NodePort)**: `config-server`
   - ClusterIP: 8888
   - NodePort: 30888 (dışarıdan erişim)

4. **Service (ClusterIP)**: `config-server-internal`
   - Internal cluster communication için

**Deployment'ı izleyin**:

```bash
# Pod'ların durumu
kubectl get pods -l app=config-server -w

# Detaylı bilgi
kubectl describe pod -l app=config-server

# Logları görüntüleme
kubectl logs -f deployment/config-server
```

**Config Server'a erişim**:

```bash
# Cluster dışından (NodePort)
curl http://localhost:30888/actuator/health

# Minikube kullanıyorsanız
minikube service config-server --url
```

### Discovery Server Deployment

Config Server çalışır hale geldikten sonra:

```bash
kubectl apply -f k8s/discovery-server.yml
```

**Bu komut şunları oluşturur**:

1. **ConfigMap**: `discovery-server-config`
   - Config Server URI
   - Spring Profile: `dev`

2. **Deployment**: `discovery-server`
   - 1 replica
   - Image: `halitkalayci/smartorder-discovery-server:latest`
   - Resource limits: 384Mi-768Mi RAM

3. **Service (NodePort)**: `discovery-server`
   - NodePort: 30761

4. **Service (ClusterIP)**: `discovery-server-internal`

**Eureka Dashboard'a erişim**:

```bash
# NodePort üzerinden
http://localhost:30761

# Minikube
minikube service discovery-server --url
```

### Tüm Kaynakları Görüntüleme

```bash
# Tüm pod'lar
kubectl get pods

# Tüm servisler
kubectl get services

# Tüm deployments
kubectl get deployments

# ConfigMaps
kubectl get configmaps

# Tüm kaynaklar
kubectl get all
```

### Temizleme (Cleanup)

```bash
# Config Server'ı silme
kubectl delete -f k8s/config-server.yml

# Discovery Server'ı silme
kubectl delete -f k8s/discovery-server.yml

# Veya tüm kaynakları silme
kubectl delete all --all
```

---

## ⚙️ Kubernetes Konfigürasyonları

### Config Server Override'ları

**Dosya**: `k8s/config-server.yml`

| ConfigMap Key | Değer | Açıklama | Override Nedeni |
|---------------|-------|----------|-----------------|
| `SPRING_PROFILES_ACTIVE` | `git` | Git backend kullanılır | **Yerel geliştirmede** `native` (dosya sistemi) kullanılırken, **K8s'de** Git repository tercih edilir. Config'ler merkezi bir repo'dan çekilir. |
| `SO_CONFIG_GIT_URI` | `https://github.com/halitkalayci/java-advanced-project3.git` | Config dosyalarının bulunduğu Git repository | **Production ortamında** config'ler Git'te versiyonlanır, audit log tutulur. Yerel ortamda ise `configs/` klasörü kullanılır. |
| `SO_CONFIG_GIT_BRANCH` | `main` | Config branch | **Farklı ortamlar** için farklı branch'ler kullanılabilir (dev, staging, prod). |
| `SO_EUREKA_URI` | `http://discovery-server-internal:8761/eureka` | Eureka Server adresi | **K8s cluster içinde** internal Service name (`discovery-server-internal`) kullanılır. Yerel ortamda `localhost:8761` kullanılır. DNS-based service discovery. |
| `JAVA_OPTS` | `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 ...` | JVM parametreleri | **Container ortamında** memory limitleri farklı hesaplanır. Heap size otomatik ayarlanır, container'dan çıkış hızlandırılır. |

**Liveness Probe**:
```yaml
httpGet:
  path: /actuator/health/liveness
  port: 8888
initialDelaySeconds: 90
periodSeconds: 10
failureThreshold: 3
```
- **Amaç**: Pod'un yaşayıp yaşamadığını kontrol eder. Fail olursa Kubernetes pod'u restart eder.
- **initialDelaySeconds**: Uygulama başlamadan önce 90 saniye bekle (Spring Boot'un başlaması için).

**Readiness Probe**:
```yaml
httpGet:
  path: /actuator/health/readiness
  port: 8888
initialDelaySeconds: 60
```
- **Amaç**: Pod'un trafik almaya hazır olup olmadığını kontrol eder. Ready olmadan Service'e eklenmez.
- **Fark**: Liveness failed → restart, Readiness failed → trafikten çıkar (restart etmez).

**Startup Probe**:
```yaml
httpGet:
  path: /actuator/health
  port: 8888
failureThreshold: 12
periodSeconds: 10
```
- **Amaç**: İlk başlangıç için ekstra süre verir. Başarılı olana kadar diğer probe'lar bekler.
- **failureThreshold × periodSeconds**: 12 × 10 = 120 saniye başlangıç toleransı.

**Resource Requests & Limits**:
```yaml
resources:
  requests:
    memory: "256Mi"
    cpu: "200m"
  limits:
    memory: "512Mi"
    cpu: "500m"
```
- **requests**: Kubernetes bu kaynakları garanti eder (scheduling için).
- **limits**: Pod bu limitin üzerine çıkamaz (OOMKilled riski).

**Security Context**:
```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1000
  fsGroup: 1000
```
- **Amaç**: Container'ı root kullanıcı olarak çalıştırmaz (güvenlik best practice).

---

### Discovery Server Override'ları

**Dosya**: `k8s/discovery-server.yml`

| ConfigMap Key | Değer | Açıklama | Override Nedeni |
|---------------|-------|----------|-----------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Aktif profil | Discovery Server için ortam bazlı konfigürasyon. |
| `SO_CONFIG_URI` | `http://config-server-internal:8888` | Config Server adresi | **K8s cluster içinde** internal service name kullanılır. DNS resolution ile `config-server-internal` Service'e route eder. |
| `JAVA_OPTS` | Container-optimized JVM flags | JVM parametreleri | Eureka memory-intensive olabilir, bu yüzden heap ayarları optimize edilir. |

**Resource Limits**:
```yaml
resources:
  requests:
    memory: "384Mi"  # Config Server'dan daha fazla
    cpu: "250m"
  limits:
    memory: "768Mi"
    cpu: "500m"
```
- **Neden daha fazla?**: Eureka tüm servislerin metadata'sını in-memory tutar. Servis sayısı arttıkça memory ihtiyacı artar.

**Probe Süreleri**:
```yaml
livenessProbe:
  initialDelaySeconds: 120  # Config Server'dan uzun
readinessProbe:
  initialDelaySeconds: 90
startupProbe:
  failureThreshold: 15     # 15 × 10 = 150 saniye
```
- **Neden daha uzun?**: Discovery Server, Config Server'dan config çeker. Config Server hazır olmadan başlayamaz. Cascade dependency.

---

### NodePort vs ClusterIP

**Her servis için 2 Service tanımı var**:

1. **NodePort Service** (Örn: `config-server`):
   - **Port**: 30888
   - **Amaç**: Cluster dışından erişim (developer, ops ekipleri)
   - **Kullanım**: `http://localhost:30888` veya `minikube service config-server`

2. **ClusterIP Service** (Örn: `config-server-internal`):
   - **Port**: 8888
   - **Amaç**: Cluster içi servisler arası iletişim
   - **Kullanım**: Diğer pod'lar `http://config-server-internal:8888` ile erişir

**Best Practice**: Production'da NodePort yerine Ingress kullanılır. NodePort sadece dev/test için.

---

## 📡 API Endpoint'leri

### Gateway Routing

Tüm istekler API Gateway üzerinden yönlendirilir:

```
http://localhost:8080/{service-path}
```

### Catalog Service (Product Management)

| Method | Endpoint | Açıklama | Request Body |
|--------|----------|----------|--------------|
| GET | `/products` | Tüm ürünleri listele | - |
| GET | `/products/{id}` | Ürün detayı | - |
| POST | `/products` | Yeni ürün oluştur | `{"name": "...", "unitCents": 1000, "currency": "USD"}` |
| PUT | `/products/{id}` | Ürün güncelle | `{"name": "...", "unitCents": 1500, ...}` |

**Örnek Request**:

```bash
# Ürün listesi
curl http://localhost:8080/products

# Yeni ürün oluştur (JWT token gerekli)
curl -X POST http://localhost:8080/products \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "unitCents": 150000,
    "currency": "USD",
    "active": true
  }'
```

### Order Service (Order Management)

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/orders` | Yeni sipariş oluştur |
| GET | `/orders/{id}` | Sipariş detayı |

**Order Creation Flow**:

1. Client → Order Service'e POST `/orders`
2. Order Service → Catalog Service'den ürün fiyatlarını çeker (WebClient)
3. Order aggregate oluşturulur, fiyatlar snapshot olarak kaydedilir
4. Domain event: `OrderCreated` → Outbox table'a yazılır
5. Scheduled job → Outbox'tan event'leri Kafka'ya publish eder
6. Payment Service → `order.created` topic'ini dinler

**Örnek**:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "user-123",
    "items": [
      {"productId": "prod-1", "quantity": 2},
      {"productId": "prod-2", "quantity": 1}
    ]
  }'
```

### BFF Endpoints (Frontend Auth)

| Endpoint | Açıklama |
|----------|----------|
| `GET /oauth2/authorization/keycloak` | Keycloak login'e yönlendirir |
| `GET /login/oauth2/code/keycloak` | OAuth callback (PKCE code exchange) |
| `GET /api/**` | Angular'dan gelen istekleri proxy'ler (token relay) |
| `POST /logout` | Session'ı sonlandırır, Keycloak'tan logout |

---

## 🔐 Keycloak Entegrasyonu

### OAuth 2.0 Flow (BFF)

1. **Angular** → BFF `/oauth2/authorization/keycloak`
2. **BFF** → Keycloak login page'e redirect (Authorization Code + PKCE)
3. **User** → Credentials girer
4. **Keycloak** → BFF callback endpoint'e `code` gönderir
5. **BFF** → `code` ile access token alır, **HttpOnly cookie**'de saklar
6. **Angular** → `/api/**` isteklerinde cookie otomatik gönderilir
7. **BFF** → Token'ı `Authorization: Bearer` header'ına ekler, backend'e proxy'ler

**Güvenlik Avantajları**:
- ✅ Token JavaScript'e asla expose olmaz (XSS koruması)
- ✅ HttpOnly cookie → CSRF token gerektirir
- ✅ PKCE → Authorization code interception koruması

### Resource Server (Business Services)

Her business service (catalog, order, payment, notification) şu config'e sahiptir:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${SO_KEYCLOAK_ISSUER:http://localhost:8585/realms/smartorder}
```

**İşleyiş**:
1. Client → `Authorization: Bearer <JWT>` header'ı gönderir
2. Resource Server → JWT'yi validate eder (Keycloak public key ile)
3. Token geçerliyse → endpoint'e erişim verilir
4. Token geçersiz/expired ise → `401 Unauthorized`

---

## 📨 Kafka Event Streaming

### Topic'ler

| Topic | Producer | Consumer | Event Type |
|-------|----------|----------|------------|
| `order.created` | Order Service | Payment Service | Yeni sipariş oluşturuldu |
| `payment.succeeded` | Payment Service | Notification Service | Ödeme başarılı |
| `payment.failed` | Payment Service | Notification Service | Ödeme başarısız |
| `*.DLT` (Dead Letter Topic) | Tüm consumer'lar | Manual recovery | İşlenememiş mesajlar |

### Outbox Pattern

**Problem**: Database transaction commit edildikten sonra Kafka publish başarısız olursa ne olur?

**Çözüm**: Outbox Table

```sql
CREATE TABLE outbox_events (
  id UUID PRIMARY KEY,
  aggregate_type VARCHAR(64),
  aggregate_id UUID,
  type VARCHAR(128),
  payload CLOB,
  created_at TIMESTAMP,
  published_at TIMESTAMP
);
```

**Flow**:

1. Business logic → Domain event oluştur
2. **Aynı transaction içinde** → Aggregate + Outbox record'unu kaydet
3. Scheduled job (`@Scheduled(fixedDelay = 5000)`) → `published_at IS NULL` record'ları seç
4. Kafka'ya publish et
5. Başarılıysa → `published_at = NOW()`
6. Başarısızsa → retry (exponential backoff)

**Avantajlar**:
- ✅ Eventual consistency garantisi
- ✅ At-least-once delivery
- ✅ Database transaction ile tutarlılık

### Idempotency (Payment Service)

**Problem**: Kafka consumer aynı mesajı birden fazla işlerse duplicate ödeme olur!

**Çözüm**: Idempotency Table

```sql
CREATE TABLE payment_requests (
  order_id UUID PRIMARY KEY,  -- Unique constraint
  status VARCHAR(20),
  last_attempt_at TIMESTAMP
);
```

**Flow**:

1. `order.created` event gelir
2. `payment_requests` tablosuna `INSERT` dene
3. **Duplicate key error** → Bu order zaten işlenmiş, skip et
4. **Insert başarılı** → Ödemeyi işle

**Sonuç**: Aynı order_id için sadece 1 kez ödeme yapılır.

---

## 🗄️ Database Yapısı

### H2 File Mode

Her servis kendi embedded database'ine sahiptir:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:${SO_DB_DIR:./data}/catalog;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
    username: sa
    password: ""
```

**Dosya Konumları**:

```
backend/
├── catalog-service/data/catalog.mv.db
├── order-service/data/order.mv.db
├── payment-service/data/payment.mv.db
└── notification-service/data/notification.mv.db
```

**Avantajlar**:
- ✅ Servisler arası database izolasyonu (microservices best practice)
- ✅ Development kolaylığı (external DB kurulumu gereksiz)
- ✅ Migration kolaylığı (H2 → PostgreSQL)

**H2 Console**:

```bash
# Her servisin kendi console'u var
http://localhost:8084/h2-console  # catalog-service
http://localhost:8081/h2-console  # order-service
http://localhost:8082/h2-console  # payment-service
http://localhost:8083/h2-console  # notification-service

# JDBC URL: jdbc:h2:file:./data/catalog (örnek)
# User: sa
# Password: (boş)
```

### Schema Initialization

**Flyway kullanılmıyor**. Schema dosyaları:

```
src/main/resources/db/
└── schema.sql
```

Spring Boot otomatik olarak `schema.sql`'i execute eder (ilk başlangıçta).

---

## 📊 Monitoring ve Observability

### Spring Boot Actuator

Her serviste aşağıdaki endpoint'ler aktiftir:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    health:
      probes:
        enabled: true
      show-details: always
```

**Endpoint'ler**:

| Endpoint | Açıklama |
|----------|----------|
| `/actuator/health` | Genel sağlık durumu |
| `/actuator/health/liveness` | Liveness probe (K8s için) |
| `/actuator/health/readiness` | Readiness probe (K8s için) |
| `/actuator/info` | Uygulama bilgisi |
| `/actuator/prometheus` | Prometheus format metrics |
| `/actuator/metrics` | Tüm metrikler |

**Prometheus Annotations** (K8s):

```yaml
annotations:
  prometheus.io/scrape: "true"
  prometheus.io/port: "8888"
  prometheus.io/path: "/actuator/prometheus"
```

Bu sayede Prometheus otomatik olarak metrikleri scrape eder.

### Örnek Metrikler

```bash
# JVM memory
curl http://localhost:8888/actuator/metrics/jvm.memory.used

# HTTP istekleri
curl http://localhost:8888/actuator/metrics/http.server.requests

# Custom metric (örnek)
curl http://localhost:8081/actuator/metrics/order.created.count
```

---

## 🐛 Troubleshooting

### Servislerin Başlamaması

**Problem**: Config Server'a bağlanamıyor

```
ConfigServicePropertySourceLocator : Could not locate PropertySource
```

**Çözüm**:
1. Config Server'ın çalıştığını doğrulayın: `curl http://localhost:8888/actuator/health`
2. `application.yml`'de `spring.config.import` doğru mu?

   ```yaml
   spring:
     config:
       import: "configserver:${SO_CONFIG_URI:http://localhost:8888}"
   ```

3. Config dosyaları `configs/` klasöründe doğru yerde mi?

---

**Problem**: Eureka'ya kayıt olamıyor

```
DiscoveryClient : Failed to get registry from Eureka server
```

**Çözüm**:
1. Discovery Server çalışıyor mu? `http://localhost:8761`
2. Config dosyasında Eureka URI doğru mu?

   ```yaml
   eureka:
     client:
       service-url:
         defaultZone: ${SO_EUREKA_URI:http://localhost:8761/eureka}
   ```

---

### Kafka Bağlantı Sorunları

**Problem**:

```
AdminClientException: Timed out waiting for a node assignment
```

**Çözüm**:

```bash
# Kafka çalışıyor mu?
docker-compose ps kafka

# Kafka log'ları kontrol et
docker-compose logs kafka

# Kafka yeniden başlat
docker-compose restart kafka
```

---

### JWT Token Validation Hatası

**Problem**:

```
401 Unauthorized - Invalid JWT signature
```

**Çözüm**:
1. Keycloak'un issuer-uri'si doğru mu?
2. Realm name doğru mu? (smartorder)
3. Token expired olabilir → Yeni token al
4. Public key cache temizle → Servisi restart et

---

### Kubernetes Pod Sürekli Restart Oluyor

**Problem**: CrashLoopBackOff

```bash
kubectl get pods
# NAME                             READY   STATUS             RESTARTS
# config-server-xxx                0/1     CrashLoopBackOff   5
```

**Çözüm**:

```bash
# Log'ları kontrol et
kubectl logs config-server-xxx

# Previous container'ın log'u
kubectl logs config-server-xxx --previous

# Describe ile event'leri gör
kubectl describe pod config-server-xxx

# Common issues:
# - Liveness probe çok erken tetikleniyor → initialDelaySeconds artır
# - OOMKilled → Memory limit düşük, artır
# - Image pull hatası → imagePullPolicy: IfNotPresent yap
```

---

### H2 Database Lock

**Problem**:

```
Database may be already in use: "Locked by another process"
```

**Çözüm**:

```bash
# Servisi durdur
# Lock dosyasını sil
rm backend/catalog-service/data/catalog.lock.db

# Servisi yeniden başlat
```

Veya `AUTO_SERVER=TRUE` flag'i zaten set edilmiş, birden fazla connection'a izin verir.

---

## 📚 Daha Fazla Bilgi

- **Keycloak Kurulum**: [`KEYCLOAK-SETUP.md`](./KEYCLOAK-SETUP.md)
- **Troubleshooting Guide**: [`TROUBLESHOOTING.md`](./TROUBLESHOOTING.md)
- **DDD Concepts**: [Domain-Driven Design (Eric Evans)](https://domainlanguage.com/ddd/)
- **Hexagonal Architecture**: [Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- **Spring Cloud**: [Official Docs](https://spring.io/projects/spring-cloud)
- **Kubernetes Probes**: [K8s Health Checks](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/)

---

## 🤝 Katkıda Bulunma

Bu eğitimsel bir projedir. Şu alanlarda katkı yapabilirsiniz:

- Yeni servis ekleme (örn: Review Service)
- Kubernetes Ingress konfigürasyonu
- Monitoring stack (Prometheus + Grafana)
- Integration test'ler
- CI/CD pipeline (GitHub Actions, Jenkins)

---

## 📄 Lisans

Bu proje eğitim amaçlıdır. MIT License.

---

## 👨‍💻 Geliştirici

**Halit Kalaycı**  
📧 Email: [halit.kalayci@example.com](mailto:halit.kalayci@example.com)  
🔗 GitHub: [@halitkalayci](https://github.com/halitkalayci)

---

**Happy Coding! 🚀**

