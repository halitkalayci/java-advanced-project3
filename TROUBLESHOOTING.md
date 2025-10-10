# Troubleshooting: 400 BAD_REQUEST from UNKNOWN

## Sorun
```
WebClientResponseException$BadRequest: 400 Bad Request from UNKNOWN
checkpoint ? 400 BAD_REQUEST from GET /api/catalog/products [DefaultWebClient]
```

**"UNKNOWN"** kelimesi çok kritik! Bu LoadBalancer'ın hedef servisi (`lb://gateway`) çözemediği anlamına gelir.

---

## Çözüm Adımları

### 1️⃣ Gateway Çalışıyor mu?

**Kontrol**:
- Gateway servisinin terminalini kontrol edin
- `Started GatewayApplication` yazısını görmeli siniz

**Test**:
```bash
curl http://localhost:8080/actuator/health
```

**Beklenen Yanıt**:
```json
{"status":"UP"}
```

❌ **Eğer yanıt gelmiyorsa**: Gateway çalışmıyor → Gateway'i başlatın

---

### 2️⃣ Gateway Eureka'da Kayıtlı mı?

**Kontrol**:
1. Tarayıcıda açın: http://localhost:8761
2. "Instances currently registered with Eureka" bölümüne bakın
3. **GATEWAY** servisini arayın

✅ **Görülmesi gereken**:
```
GATEWAY - 192.168.x.x:gateway:8080
```

❌ **Eğer yok ise**:
- Gateway Eureka client config'inde sorun var
- Gateway başlatılmamış
- Eureka server çalışmıyor

---

### 3️⃣ Service Name Uyuşuyor mu?

**BFF WebClient**:
```java
.baseUrl("lb://gateway")  // Küçük harf
```

**Gateway Application Name**:
```yaml
spring:
  application:
    name: gateway  # Küçük harf olmalı
```

**Eureka'da Görünen**:
- Genellikle BÜYÜK HARF: `GATEWAY`
- LoadBalancer bu büyük/küçük harf farkını otomatik halleder

---

### 4️⃣ LoadBalancer Dependency Var mı?

**BFF pom.xml kontrol**:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

❌ **Eğer yok ise**: Ekleyin ve `mvn clean install`

---

### 5️⃣ Geçici Test: Direkt URL Kullan

**BFF WebClientConfig.java**:
```java
@Bean
WebClient webClient(...) {
    return builder
            .baseUrl("http://localhost:8080")  // Geçici test
            .filter(oauth)
            .build();
}
```

✅ **Eğer bu çalışıyorsa**: Sorun LoadBalancer'da
❌ **Eğer yine hata veriyorsa**: Sorun Gateway'de

---

## Detaylı Kontrol Listesi

### A) Eureka Server
- [ ] Çalışıyor: http://localhost:8761
- [ ] Dashboard açılıyor
- [ ] En az 1 servis kayıtlı

### B) Gateway
- [ ] Çalışıyor
- [ ] Port 8080'de dinliyor
- [ ] Eureka'ya kaydolmuş (http://localhost:8761'de görünüyor)
- [ ] `spring.application.name: gateway`

### C) BFF
- [ ] Çalışıyor
- [ ] WebClient baseUrl: `lb://gateway`
- [ ] LoadBalancer dependency var
- [ ] Eureka client enabled

### D) Catalog Service
- [ ] Çalışıyor
- [ ] Port 8084'te dinliyor
- [ ] Eureka'ya kaydolmuş
- [ ] `spring.application.name: catalog-service`

---

## Hızlı Test

### 1. Servislerin Durumu
```bash
# Gateway health check
curl http://localhost:8080/actuator/health

# Catalog health check
curl http://localhost:8084/actuator/health

# BFF health check
curl http://localhost:8070/actuator/health
```

### 2. Gateway'den Catalog'a Direkt İstek
```bash
# Gateway üzerinden (manuel test)
curl -X GET "http://localhost:8080/api/catalog/products?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Catalog'a Direkt İstek
```bash
# Catalog service direkt (bypass gateway)
curl "http://localhost:8084/products?page=0&size=20"
```

---

## Muhtemel Sorunlar ve Çözümler

### Sorun: Gateway Başlamıyor
**Sebep**: Dependency hatası, port çakışması
**Çözüm**: 
- Gateway terminalindeki hata loglarını kontrol edin
- `mvn clean install` yapın
- Port 8080 kullanımda mı kontrol edin

### Sorun: Gateway Eureka'da Yok
**Sebep**: Eureka client config yanlış
**Çözüm**:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
    register-with-eureka: true
    fetch-registry: true
```

### Sorun: LoadBalancer Çalışmıyor
**Sebep**: spring-cloud-loadbalancer dependency eksik
**Çözüm**: BFF pom.xml'e ekleyin:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

### Sorun: 400 Bad Request (UNKNOWN değil)
**Sebep**: Gateway route'ları veya Catalog endpoint'leri yanlış
**Çözüm**: 
- Gateway route path: `/api/catalog/**`
- Catalog endpoint: `/products`
- stripPrefix(2) ile `/api/catalog` kaldırılıyor

---

## BFF Loglama Çıktısını Kontrol Edin

Şu logları arayın:
```
DEBUG - Attempting to resolve service: gateway
DEBUG - No instances available for gateway
```

Eğer bu logları görüyorsanız → Gateway Eureka'da kayıtlı değil!

---

## Önerilen İşlem Sırası

1. **Eureka Dashboard kontrol** → Gateway kayıtlı mı?
2. **Gateway logları kontrol** → Eureka'ya kaydoldu mu?
3. **BFF logları kontrol** → LoadBalancer ne diyor?
4. **Geçici direkt URL test** → Gateway çalışıyor mu?
5. **Maven clean install** → Dependency'ler güncel mi?

Lütfen bu adımları takip edin ve hangi aşamada problem olduğunu belirtin!

