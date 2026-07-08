# donaton-bff

Backend For Frontend del proyecto Donaton (DuocUC DSY1106).
Agrega y orquesta las respuestas de los microservicios upstream para simplificar el consumo desde el frontend.

## Requisitos

- Java 17
- Maven 3.8+
- `ms-donaciones` corriendo en `localhost:8084`
- `ms-auth` corriendo en `localhost:8083`

## Cómo correrlo

```bash
mvn spring-boot:run
```

Levanta en el puerto **8090**.

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/bff/portada` | Causas activas + top donadores + resumen general |
| GET | `/bff/dashboard` | Stats totales + top donadores + últimas donaciones + testimonios pendientes |
| GET | `/bff/transparencia` | Tabla pública de todas las donaciones auditadas |

## Circuit Breaker

Todos los métodos de `DonacionesClient` tienen circuit breaker (`donaciones-cb`).
Si `ms-donaciones` no responde, los endpoints retornan datos vacíos con un `mensajeError` explicativo en lugar de fallar.

| Parámetro | Valor |
|---|---|
| Ventana deslizante | 10 llamadas |
| Umbral de fallo | 50 % |
| Tiempo en OPEN | 10 s |
| Timeout por llamada | 3 s |
