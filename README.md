# Parking Service Usage Guide

## Overview
The Parking Service provides a REST API for managing a parking lot, supporting different types of vehicles and parking spots. The service handles car entry/exit operations, spot management, and reporting.

### Requirements

1. **Java 17+**
2. **Docker and Docker-compose**

### Setup via terminal

**1. Clone the repository:**

```bash
git clone https://gitverse.ru/test_cakes/Java-middle-new1.5-vadim_23.git
cd Java-middle-new1.5-vadim_23
```

**2. Copy the .env file:**

```bash
cp .env.sample .env
```

**3. Start the application and PostgreSQL using Docker:**

```bash
# Copy the docker-compose.yml file:
cp docker-compose.sample.yml docker-compose.yml  

# Make sure the ports specified in docker-compose are not in use on your system
# and start the build:
make up

# Watch app logs:
make logs
```

**4. After that, the API will be available at:**

```url
http://localhost:8081/swagger-ui.html
```

### Tests coverage

```bash
# Start tests:
make test

# Open JaCoCo coverage report in browser:
make coverage
```

## API Endpoints

### Car Entry
- **Endpoint**: `POST /api/v1/parking/entry`
- **Request Body**:
  ```json
  {
    "licensePlate": "ABC123",
    "type": "PASSENGER"
  }
  ```
- **Response**: 
  ```json
  {
    "licensePlate": "ABC123",
    "entryTime": "2025-06-06T10:00:00"
  }
  ```
- **Notes**:
  - License plate is required and must be unique
  - Car type must be one of: PASSENGER, TRUCK, MOTORCYCLE, SPECIAL
  - Returns 400 if car is already parked
  - Returns 404 if no suitable spot is available

### Car Exit
- **Endpoint**: `POST /api/v1/parking/exit`
- **Request Body**:
  ```json
  {
    "licensePlate": "ABC123"
  }
  ```
- **Response**:
  ```json
  {
    "licensePlate": "ABC123",
    "entryTime": "2025-06-06T10:00:00",
    "exitTime": "2025-06-06T11:30:00",
    "duration": "PT1H30M"
  }
  ```
- **Notes**:
  - Returns 400 if car is not currently parked
  - Duration is in ISO-8601 format

### Report Generation
- **Endpoint**: `GET /api/v1/parking/report`
- **Parameters**:
  - `start`: Start time (ISO-8601 format)
  - `end`: End time (ISO-8601 format)
- **Response**:
  ```json
  {
    "totalEntries": 100,
    "totalExits": 95,
    "averageParkingDuration": "PT2H15M",
    "entriesByType": {
      "PASSENGER": 20,
      "TRUCK": 10,
      "MOTORCYCLE": 5,
      "SPECIAL": 5
    }
  }
  ```
- **Notes**:
  - Start time must be before end time
  - Duration is in ISO-8601 format

## Business Rules

### Car Types
- **PASSENGER**: Regular passenger cars
- **TRUCK**: Large vehicles requiring special spots
- **MOTORCYCLE**: Motorcycles
- **SPECIAL**: Special vehicles

### Parking Rules
1. Each car type can only park in designated spots
2. A car cannot enter if it's already parked
3. A car cannot exit if it's not currently parked
4. Parking spots are automatically assigned based on car type
5. Each car's parking history is tracked

### Validation Rules
1. License plate is required and must be unique
2. Car type must be valid
3. Report dates must be valid
4. Start time must be before end time

## Error Handling
- **400 Bad Request**: Invalid input data
- **404 Not Found**: Resource not found (e.g., no available spots)
- **409 Conflict**: Business rule violation (e.g., car already parked)

## Best Practices
1. Always check response status codes
2. Handle errors appropriately
3. Use proper date/time formats
4. Validate input data before sending
5. Monitor parking spot availability
6. Keep track of car entry/exit times