---

# Architecture Documentation

## Overview

The `hazelcast-client-data` application is a Spring Boot-based application designed to interact with Hazelcast data structures. The application provides RESTful endpoints to manipulate data in Hazelcast and uses Avro for serialization and deserialization.

## Application Layers

### Controller Layer

The controller layer, primarily represented by the `MapController`, exposes RESTful endpoints for CRUD operations on Hazelcast maps and executing SQL queries. This layer acts as the entry point for external requests and directs them to the appropriate service layer.

### Service Layer

The service layer contains the business logic of the application. It interacts with Hazelcast to perform operations on data structures. The main services include:

- `HazelcastMapService`: Provides operations related to Hazelcast maps.
- `HazelcastSqlService`: Allows the execution of SQL queries on Hazelcast data.

### Serializer Layer

The application uses Avro for serialization and deserialization. The `AvroCustomSerializer` class provides methods to serialize and deserialize data using Avro schemas.

### Configuration Layer

The configuration layer is responsible for loading and managing configurations required by the application. The primary class in this layer is:

- `ConfigurationLoader`: Loads map configurations from a YAML file and provides them to other parts of the application.

## Data Models

The application uses various data models to represent and manipulate data:

- `Employee`: Represents an employee entity.
- `Person`: Represents a person entity.
- `MapConfiguration`: Represents the configuration of a Hazelcast map.

## Utility Classes

Several utility classes assist in various functionalities:

- `ConfigUtils`: Provides utility functions related to configuration.
- `DataConvertorUtils`: Contains functions for converting data between different formats.
- `KeyGenerator`: Generates unique keys for data entries.

## External Integrations

- **Hazelcast**: The application integrates with Hazelcast to store and retrieve data.
- **Avro**: Used for data serialization and deserialization.
- **Swagger**: Provides an interactive API documentation and testing interface.

## Conclusion

The `hazelcast-client-data` application follows a layered architecture with clear separation of concerns. It leverages Hazelcast for data storage and retrieval, Avro for serialization, and provides a user-friendly interface through Swagger.

---