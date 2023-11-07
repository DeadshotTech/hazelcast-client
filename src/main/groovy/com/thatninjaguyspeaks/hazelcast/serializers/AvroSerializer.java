package com.thatninjaguyspeaks.hazelcast.serializers;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AvroSerializer<T> implements StreamSerializer<T> {

    private final Class<T> clazz;
    private final Schema schema;

    public AvroSerializer(Class<T> clazz) {
        this.clazz = clazz;
        this.schema = ReflectData.get().getSchema(clazz);
    }

    @Override
    public int getTypeId() {
        return 1;  // Ensure this is a unique ID within your project
    }

    @Override
    public void write(ObjectDataOutput out, T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DatumWriter<T> writer = new SpecificDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);
        writer.write(object, encoder);
        encoder.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        out.write(bytes);
    }

    @Override
    public T read(ObjectDataInput in) throws IOException {
        byte[] bytes = new byte[in.readByte()];
        in.readFully(bytes);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DatumReader<T> reader = new SpecificDatumReader<>(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(byteArrayInputStream, null);
        return reader.read(null, decoder);
    }
}

//import com.hazelcast.nio.serialization.ByteArraySerializer;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.apache.avro.Schema;
//import org.apache.avro.io.*;
//import org.apache.avro.reflect.ReflectData;
//import org.apache.avro.specific.SpecificDatumReader;
//import org.apache.avro.specific.SpecificDatumWriter;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AvroSerializer<T> implements ByteArraySerializer<T> {
//
//    @Override
//    public int getTypeId() {
//        return 1;
//    }
//
//    @Override
//    public byte[] write(T object) throws IOException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        DatumWriter<T> datumWriter = new SpecificDatumWriter<>();
//        Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
//        datumWriter.write(object, encoder);
//        encoder.flush();
//        return outputStream.toByteArray();
//    }
//
//    @Override
//    public T read(byte[] buffer) throws IOException {
//        DatumReader<T> datumReader = new SpecificDatumReader<>(getSchemaForType());
//        Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
//        return datumReader.read(null, decoder);
//    }
//
//    private static Schema getSchemaForType() {
//        try {
//            return ReflectData.get().getSchema();
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("Failed to find class with error: {} " e);
//        }
//    }
//
//}
//
