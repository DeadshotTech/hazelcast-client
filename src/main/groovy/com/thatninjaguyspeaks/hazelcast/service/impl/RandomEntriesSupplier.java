package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.pipeline.SourceBuilder;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEntriesSupplier {
    private final long limit;
    private long count = 0;

    public RandomEntriesSupplier(Processor.Context context) {
        limit = 10_00_00_000;
    }

    public void fillBuffer(SourceBuilder.SourceBuffer<Map.Entry<Long, Integer>> buffer) {
        while (count < limit) {
            buffer.add(new AbstractMap.SimpleEntry<>(count, ThreadLocalRandom.current().nextInt()));
            count++;
            if (count == limit) {
                buffer.close();
            }
        }
    }
}
