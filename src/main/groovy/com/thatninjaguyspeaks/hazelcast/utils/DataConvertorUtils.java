package com.thatninjaguyspeaks.hazelcast.utils;

import com.thatninjaguyspeaks.hazelcast.models.MapConfiguration;
import com.thatninjaguyspeaks.hazelcast.serializers.AvroCustomSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataConvertorUtils {

    public static List<Object> convertGenericToMapData(MapConfiguration mapConfig, List<Map<String, Object>> data) throws ClassNotFoundException {
        if (mapConfig == null) {
            return new ArrayList<>();
        }

        Schema valueSchema = getSchemaForType(mapConfig.getValueType());
        Class clazz = Class.forName(mapConfig.getValueType());
        AvroCustomSerializer avroSerializer = AvroCustomSerializer.builder()
                .clazz(clazz)
                .schema(valueSchema).build();

        return data.parallelStream()
                .map(datum -> {
                    try {
                        // Serialize the map datum to a byte array
    //                    byte[] serializedData = avroSerializer.serialize(mapToSpecificRecord(datum, mapConfig.getValueType()));
                        byte[] serializedData = avroSerializer.write(convertMapToGenericRecord(datum, valueSchema));
                        // Deserialize the data back to a SpecificRecord for further processing
                        Object deserializedRecord = avroSerializer.read(serializedData);

                        return deserializedRecord;
                    } catch (IOException e) {
                        // Handle serialization or deserialization failure
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private static Schema getSchemaForType(String valueType) {
        try {
            Class<?> clazz = Class.forName(valueType);
            return ReflectData.get().getSchema(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to find class for valueType: " + valueType, e);
        }
    }
    public static GenericRecord convertMapToGenericRecord(Map<String, Object> map, Schema schema) {
        GenericRecord record = new GenericData.Record(schema);
        for (Schema.Field field : schema.getFields()) {
            Object value = map.get(field.name());
            record.put(field.name(), value);
        }
        return record;
    }

    private static SpecificRecordBase mapToSpecificRecord(Map<String, Object> map, String valueType) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends SpecificRecordBase> clazz = (Class<? extends SpecificRecordBase>) Class.forName(valueType);
        Constructor<? extends SpecificRecordBase> constructor = clazz.getConstructor();
        SpecificRecordBase record = constructor.newInstance();
        Schema schema = ReflectData.get().getSchema(clazz);

        for (Schema.Field field : schema.getFields()) {
            Object value = map.get(field.name());
            record.put(field.pos(), value);
        }

        return record;
    }


    public static Class<?> getClassForName(String name) throws ClassNotFoundException {
        switch (name.toLowerCase()) {
            case "string": return String.class;
            case "integer": return Integer.class;
            case "long": return Long.class;
            case "double": return Double.class;
            case "boolean": return Boolean.class;
            case "bigint": return BigInteger.class;
            default:
                return Class.forName(name);
        }
    }

}

