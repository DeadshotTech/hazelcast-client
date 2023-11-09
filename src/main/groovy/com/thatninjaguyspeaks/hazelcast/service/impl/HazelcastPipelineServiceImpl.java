package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;

import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.SourceBuilder.SourceBuffer;
import com.hazelcast.jet.pipeline.test.TestSources;
import com.hazelcast.map.IMap;
import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastPipelineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Service
public class HazelcastPipelineServiceImpl implements HazelcastPipelineService {

    private final Logger logger = LogManager.getLogger(HazelcastPipelineServiceImpl.class);
    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public void triggerPipeline() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        var p = Pipeline.create();

        IMap<Long, Long> map = hz.getMap("myMap");
        logger.info("Map Size: {}", map.size());

//        JobConfig jobConfig = new JobConfig();
//        jobConfig.addClass(RandomEntriesSupplier.class);
//        jobConfig.addClass(RandomEntriesSupplier.FillBufferRunnable.class);

        p.readFrom(TestSources.itemStream(10))
                .withoutTimestamps()
                .filter(event -> event.sequence() % 2 == 0)
                .setName("filter out odd numbers")
                .writeTo(Sinks.logger());
//        p.readFrom(SourceBuilder.batch("random-numbers-source", ctx -> new RandomEntriesSupplier(10_000_000).applyEx(ctx))
//                        .fillBufferFn(RandomEntriesSupplier.FillBufferRunnable::fillBuffer)
//                        .build())
//                .writeTo(Sinks.mapWithUpdating("myMap",
//                        Map.Entry::getKey,
//                        (oldValue, entry) -> entry.getValue()));

//        p.readFrom(SourceBuilder.batch("random-numbers-source", ctx -> new RandomEntriesSupplier(10_000_000))
//                        .fillBufferFn((RandomEntriesSupplier.FillBufferRunnable supplier, SourceBuffer<Map.Entry<Long, Long>> buffer)
//                                -> supplier.fillBuffer(buffer))
//                        .build())
//                .writeTo(Sinks.mapWithUpdating("myMap",
//                        Map.Entry::getKey,
//                        (oldValue, entry) -> entry.getValue()));

//        hz.getJet().newJob(p, jobConfig);
        hz.getJet().newJob(p);
        logger.info("Map Size: {}", map.size());
        hz.shutdown();
        logger.info("Triggered pipeline");
    }
}

