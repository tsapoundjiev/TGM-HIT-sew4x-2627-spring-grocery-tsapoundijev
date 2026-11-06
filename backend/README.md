# Grocery Backend

REST-API zur Verwaltung von Einkaufslisten (Spring Boot, Datenhaltung vorerst über `GroceryServiceMock`).

## Starten

```bash
./gradlew bootRun
```

Swagger UI: http://localhost:8080/swagger-ui/index.html

## Endpunkte

| Kürzel | Methode | Pfad                       | Beschreibung                    | Status           |
|--------|---------|----------------------------|---------------------------------|------------------|
| A      | POST    | `/api/grocery/item`        | Artikel hinzufügen              | 201, 400         |
| B      | PUT     | `/api/grocery/item`        | Artikel aktualisieren           | 200, 400, 404    |
| C      | PATCH   | `/api/grocery/item/{id}`   | `collected` ändern              | 200, 404         |
| D      | GET     | `/api/grocery/item`        | Alle Artikel                    | 200, 204         |
| E      | GET     | `/api/grocery/item/{id}`   | Einen Artikel abfragen          | 200, 404         |
| F      | DELETE  | `/api/grocery/item[/{id}]` | Einen oder alle Artikel löschen | 200, 404         |
| G      | GET     | `/api/grocery/list`        | Alle Einkaufslisten             | 200, 204         |

## Tests

```bash
./gradlew test --tests '*GroceryControllerTest*'
```
