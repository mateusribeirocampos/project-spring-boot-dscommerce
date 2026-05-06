# Certs                                                                                                                                           

Generate local keystore:                                                                                                                                         

```bash
keytool -genkeypair \ 
-alias dscommerce-jwt \ 
-keyalg RSA -keysize 2048 \ 
-storetype PKCS12 \
-keystore src/main/resources/certs/dscommerce-jwt.p12 \
-validity 3650 \
-storepass YOUR_PASSWORD
```
Then base64-encode and set JWT_KEYSTORE_BASE64 in your .env file.

```bash
base64 -w 0 src/main/resources/certs/dscommerce-jwt.p12
```

**11. Render Dashboard** — the same 3 env vars to `.env` added in Environment Variables
