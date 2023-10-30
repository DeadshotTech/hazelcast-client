package com.thatninjaguyspeaks.hazelcast.serializers;
import com.hazelcast.nio.serialization.ByteArraySerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvroCustomSerializer<T> implements ByteArraySerializer<T> {

    private Class<T> clazz;
    private Schema schema;

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public byte[] write(T object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<T> datumWriter = new SpecificDatumWriter<>(clazz);
        Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        datumWriter.write(object, encoder);
        encoder.flush();
        return outputStream.toByteArray();
    }

    @Override
    public T read(byte[] buffer) throws IOException {
        DatumReader<T> datumReader = new SpecificDatumReader<>(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
        return datumReader.read(null, decoder);
    }

}
