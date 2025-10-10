# Keycloak Kurulum Adımları - SmartOrder

## 📋 Keycloak'ta Yapılması Gerekenler

### 1️⃣ Realm Oluşturma
- **Adım**: Keycloak Admin Console'a giriş yap (http://localhost:8585)
- **Realm Adı**: `smartorder-bff`
- **Enabled**: ✅ Aktif olmalı

---

### 2️⃣ Client Oluşturma (BFF için)
**Client ID**: `bff-web`

#### General Settings:
- **Client type**: `OpenID Connect`
- **Client ID**: `bff-web`
- **Name**: `SmartOrder BFF Web Client`
- **Description**: `Backend for Frontend client for SmartOrder`
- **Always Display in Console**: ✅ (opsiyonel)

#### Capability config:
- **Client authentication**: ✅ **ON** (Confidential client)
- **Authorization**: ❌ OFF
- **Standard Flow**: ✅ **ON** (Authorization Code Flow)
- **Direct Access Grants**: ❌ OFF
- **Implicit Flow**: ❌ OFF
- **Service Accounts**: ❌ OFF

#### Login settings:
- **Root URL**: `http://localhost:8070`
- **Home URL**: `http://localhost:4200`
- **Valid Redirect URIs**: 
  - `http://localhost:8070/login/oauth2/code/keycloak`
  - `http://localhost:8070/login/oauth2/code/*`
- **Valid Post Logout Redirect URIs**: 
  - `http://localhost:4200/*`
  - `http://localhost:8070/*`
- **Web Origins**: 
  - `http://localhost:4200`
  - `http://localhost:8070`

#### Advanced Settings:
- **PKCE Code Challenge Method**: `S256` ✅ (PKCE zorunlu)
- **Access Token Lifespan**: `5 Minutes` (varsayılan)
- **Client Session Idle**: `30 Minutes`
- **Client Session Max**: `10 Hours`

---

### 3️⃣ Client Credentials (Client Secret)

**Client oluşturduktan sonra:**
1. `bff-web` client'ına tıkla
2. **Credentials** tab'ına git
3. **Client Secret** değerini kopyala
4. Bu değeri `backend/configs/bff/bff-dev.yml` dosyasına yapıştır:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            client-secret: [BURAYA_CLIENT_SECRET_YAPIŞTIR]
```

**Not**: Şu anki config'de client-secret: `2RTvc4QRG2P7GJf7q0lnPchOqhx9Bq19` yazıyor. Bu değer Keycloak'tan aldığınız gerçek secret ile eşleşmelidir.

---

### 4️⃣ Realm Roles Oluşturma (Opsiyonel)

**Roller**:
- `user` - Normal kullanıcı
- `admin` - Yönetici

**Adımlar**:
1. Realm Settings → Roles → Create Role
2. Her bir rol için:
   - Role Name: `user` / `admin`
   - Description: Açıklama ekle
   - Save

---

### 5️⃣ Test Kullanıcısı Oluşturma

#### Kullanıcı Bilgileri:
1. **Users** → **Add user**
   - Username: `testuser`
   - Email: `testuser@smartorder.com`
   - First Name: `Test`
   - Last Name: `User`
   - Email Verified: ✅ ON
   - Enabled: ✅ ON
   - Save

2. **Credentials** tab'ına git:
   - Set password: `password123`
   - Temporary: ❌ OFF (şifre değiştirme zorunluluğu yok)
   - Save

3. **Role Mappings** tab'ına git:
   - Assign role → `user` rolünü ekle

---

### 6️⃣ Realm Settings - Genel Ayarlar

#### Login Tab:
- **User registration**: ✅ ON (kullanıcı kaydına izin ver - opsiyonel)
- **Forgot password**: ✅ ON (opsiyonel)
- **Remember me**: ✅ ON (opsiyonel)
- **Login with email**: ✅ ON

#### Tokens Tab:
- **SSO Session Idle**: `30 Minutes`
- **SSO Session Max**: `10 Hours`
- **Access Token Lifespan**: `5 Minutes`
- **Refresh Token Max Reuse**: `0` (one-time use)

---

### 7️⃣ Client Scopes (Varsayılan ayarlar yeterli)

**Otomatik atanan scopes**:
- `openid`
- `profile`
- `email`

Bu scope'lar BFF config'de zaten tanımlı:
```yaml
scope:
  - openid
  - profile
  - email
```

---

## ✅ Doğrulama Kontrolleri

### 1. Realm doğru mu?
```
Realm: smartorder-bff
URL: http://localhost:8585/realms/smartorder-bff
```

### 2. Client ayarları doğru mu?
```
Client ID: bff-web
Client Authentication: ON (Confidential)
Valid Redirect URIs: http://localhost:8070/login/oauth2/code/keycloak
PKCE: S256
```

### 3. Client Secret eşleşiyor mu?
```
Keycloak Credentials Tab'daki secret = bff-dev.yml içindeki client-secret
```

### 4. Test kullanıcısı var mı?
```
Username: testuser
Password: password123
Enabled: true
Email Verified: true
```

---

## 🚀 Akış Testi

### Login Flow:
1. Frontend → `http://localhost:4200` açılır
2. Login butonuna tıkla → BFF'ye redirect: `http://localhost:8070/oauth2/authorization/keycloak`
3. Keycloak login sayfası açılır → `testuser` / `password123` ile giriş yap
4. Keycloak → BFF'ye callback: `http://localhost:8070/login/oauth2/code/keycloak`
5. BFF → Frontend'e redirect: `http://localhost:4200` (authenticated)

### Logout Flow:
1. Logout butonuna tıkla → BFF: `http://localhost:8070/auth/logout`
2. BFF session siler
3. BFF → Frontend'e redirect: `http://localhost:4200/login`

---

## 🔧 Troubleshooting

### Hata: "Invalid redirect_uri"
**Çözüm**: Keycloak client'ındaki `Valid Redirect URIs` kontrol edin:
- `http://localhost:8070/login/oauth2/code/keycloak` ekli olmalı

### Hata: "Client authentication failed"
**Çözüm**: 
- Client Secret'in bff-dev.yml ile eşleştiğinden emin olun
- Client Authentication'ın ON olduğundan emin olun

### Hata: "PKCE verification failed"
**Çözüm**: 
- Client → Advanced Settings → PKCE Code Challenge Method = `S256` olmalı

### Hata: "Invalid issuer"
**Çözüm**: 
- bff-dev.yml'deki issuer-uri doğru olmalı:
  ```yaml
  issuer-uri: http://localhost:8585/realms/smartorder-bff
  ```

---

## 📝 Özet Checklist

- [ ] Realm oluşturuldu: `smartorder-bff`
- [ ] Client oluşturuldu: `bff-web` (Confidential, Standard Flow ON)
- [ ] Valid Redirect URIs: `http://localhost:8070/login/oauth2/code/keycloak`
- [ ] PKCE enabled: `S256`
- [ ] Client Secret kopyalandı ve bff-dev.yml'ye eklendi
- [ ] Test user oluşturuldu: `testuser` / `password123`
- [ ] Roles atandı (opsiyonel)
- [ ] Web Origins: `http://localhost:4200` eklendi

---

**Not**: Docker Compose ile Keycloak'u başlatırken port `8585` kullanıyoruz. Eğer farklı bir port kullanıyorsanız, tüm config dosyalarında bunu güncelleyin.

