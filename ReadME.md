---

# Hazelcast Client Data

This repository contains the implementation of a Hazelcast client application.

## Controller Endpoints

### Map Controller

The [MapController](https://github.com/DeadshotTech/hazelcast-client-data/blob/master/src/main/groovy/com/thatninjaguyspeaks/hazelcast/controller/MapController.java) provides the following endpoints:

- `GET /com/thatninjaguyspeaks/hazelcast/map/{key}`: Retrieve data from the map using the provided key.
- `PUT /com/thatninjaguyspeaks/hazelcast/map/put`: Insert data into the map.
- `PUT /com/thatninjaguyspeaks/hazelcast/map/put-default`: Insert default data into the map.
- `DELETE /com/thatninjaguyspeaks/hazelcast/map/destroy-map`: Delete the entire map.
- `POST /com/thatninjaguyspeaks/hazelcast/map/sql`: Execute an SQL query on the map.

## Utility Files

- **ConfigUtils**: Utility functions related to configuration.
- **DataConvertorUtils**: Functions for converting data between different formats.
- **KeyGenerator**: Utility for generating unique keys.

## Serializers

#### AvroCustomSerializer

The [AvroCustomSerializer](https://github.com/DeadshotTech/hazelcast-client-data/blob/master/src/main/groovy/com/thatninjaguyspeaks/hazelcast/serializers/AvroCustomSerializer.java) class provides custom serialization and deserialization using the Avro framework. This serializer is designed to work with Avro schemas and provides methods to write and read data in Avro format.

### Config Classes

#### ConfigurationLoader

The [ConfigurationLoader](https://github.com/DeadshotTech/hazelcast-client-data/blob/master/src/main/groovy/com/thatninjaguyspeaks/hazelcast/config/ConfigurationLoader.java) class is responsible for loading configurations for maps from a YAML file (`map-configurations.yaml`). It reads the configurations during the application's startup and provides a method to retrieve the list of map configurations.

## Swagger Documentation

The application provides a Swagger UI for API documentation and testing. You can access the Swagger UI at the `/swagger-ui/` endpoint of your deployed application.

---
