# Ecommerce Application

# Usage

1. To run application from docker, use following commands


```text
./mvnw clean package -DskipTests 
docker-compose down -v
docker-compose up --build
```

1.2 In case To run application in standalone update application-dev.yml or application-prod.yml accordingly.



3. Available api list (curl)

```text
curl --location 'http://localhost:8080/api/v1/customers/wishlist' \
--header 'Content-Type: application/json' \
--data '{
    "customerId": "cust-001"
}'

```

```text
curl --location 'http://localhost:8080/api/v1/sales/today/total'

```

```text
curl --location 'http://localhost:8080/api/v1/sales/max-sale-day?startDate=2023-07-03&endDate=2025-09-01'

```

```text
curl --location 'http://localhost:8080/api/v1/sales/top-selling-items'

```

```text
curl --location 'http://localhost:8080/api/v1/sales/top-selling-items/last-month-by-quantity'

```