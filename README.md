# OgłosZone Backend

## Opis aplikacji

OgłosZone Backend to serwerowa część mobilnej tablicy ogłoszeń, implementowana w Spring Boot z użyciem Java.
Aplikacja dostarcza REST API do zarządzania ogłoszeniami (tworzenie, edytowanie, usuwanie, przeglądanie),
integruje się z bazą danych MongoDB oraz obsługuje wysyłanie powiadomień e-mail.

## Technologie

- **Język:** Java 21
- **Framework:** Spring Boot
- **Baza danych:** MongoDB
- **Konteneryzacja:** Docker
- **Inne:** Spring Data MongoDB, JavaMailSender.

## Konfiguracja

Aplikacja wymaga następujących zmiennych środowiskowych:

- `MONGODB_URI`: Łączność z MongoDB
- `MAIL`: Adres e-mail do wysyłania powiadomień
- `MAIL_PASSWORD`: Hasło appliakcji do konta e-mail

## Uruchamianie

### 1. Budowa obrazu Docker

1. Upewnij się, że plik `Dockerfile` znajduje się w katalogu głównym projektu.
2. Zbuduj obraz Docker za pomocą poniższej komendy:
   ```bash
   docker build -t ogloszone-backend:latest .
   ```
    - `-t ogloszone-backend:latest`: Nazwa i tag obrazu.
    - `.`: Katalog z `Dockerfile`.

### 2. Uruchomienie kontenera

Uruchom kontener z podanymi zmiennymi środowiskowymi. Możesz to zrobić na dwa sposoby:

```bash
docker run -d \
  -p 8080:8080 \
  -e MONGODB_URI=<UZUPEŁNIJ> \
  -e MAIL=<UZUPEŁNIJ>\
  -e MAIL_PASSWORD=<UZUPEŁNIJ> \
  ogloszone-backend:latest
```

- `-d`: Uruchom w tle.
- `-p 8080:8080`: Mapowanie portu hosta na port kontenera.
- `-e`: Definicja zmiennych środowiskowych.

### 3. Weryfikacja

- Po uruchomieniu otwórz przeglądarkę lub użyj `curl`, aby sprawdzić endpoint healthcheck:
  ```bash
  curl http://localhost:8080/api/health
  ```
  Oczekiwana odpowiedź (przy sukcesie):
  ```json
  {
    "status": "UP",
    "details": {
      "database": "MongoDB is running",
      "checkedAt": "2025-10-18T18:06:00"
    }
  }
  ```

### 4. Dokumentacja swagger jest dostępna pod adresem

  ```bash
    http://localhost:8080/swagger-ui/index.html
  ```

### 5. Zatrzymanie kontenera

- `docker stop <container_id>` (znajdź ID za pomocą `docker ps`).
