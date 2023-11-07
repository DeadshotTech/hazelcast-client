package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.jet.pipeline.SourceBuilder;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class FillBufferRunnable {
    private final long limit;
    private long count = 0;

    public FillBufferRunnable(long limit) {
        this.limit = limit;
    }

    public void run(SourceBuilder.SourceBuffer<Map.Entry<Long, Integer>> buffer) {
        while (count < limit) {
            buffer.add(new AbstractMap.SimpleEntry<>(count, ThreadLocalRandom.current().nextInt()));
            count++;
            if (count >= limit) {
                buffer.close();
            }
        }
    }
}
