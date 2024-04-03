package com.thatninjaguyspeaks.hazelcast.service.impl;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.pipeline.SourceBuilder.SourceBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RandomEntriesSupplier implements FunctionEx<Processor.Context, RandomEntriesSupplier.FillBufferRunnable>, Serializable {

    private static final Logger logger = LogManager.getLogger(RandomEntriesSupplier.class);
    private final long limit;

    public RandomEntriesSupplier(long limit) {
        this.limit = limit;
    }

    @Override
    public FillBufferRunnable applyEx(Processor.Context context) throws Exception {
        return new FillBufferRunnable(limit);
    }

    public static class FillBufferRunnable implements Serializable {
        private static final Logger logger = LogManager.getLogger(FillBufferRunnable.class);
        private final long limit;
        private transient long count = 0;

        public FillBufferRunnable(long limit) {
            this.limit = limit;
        }

        public void fillBuffer(SourceBuffer<Map.Entry<Long, Long>> buffer) {
            logger.info("fillBuffer called with buffer: {} ",buffer);
//            while (count < limit) {
//                var value = ThreadLocalRandom.current().nextLong();
                var value = count;
                logger.info("fillBuffer executing with key:{}, value: {}", count, value);
                buffer.add(new SimpleEntry<>(count, value));
                count++;
                if (count == limit) {
                    buffer.close();
                }
//            }
            logger.info("fillBuffer completed with buffer: {}", buffer!=null?buffer:"EMPTY");
        }
    }
}

