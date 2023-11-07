package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;

import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.SourceBuilder.SourceBuffer;
import com.hazelcast.map.IMap;
import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastPipelineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class HazelcastPipelineServiceImpl implements HazelcastPipelineService {

    private final Logger logger = LogManager.getLogger(HazelcastPipelineServiceImpl.class);
    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public void triggerPipeline() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        var p = Pipeline.create();

        IMap<Long, Integer> map = hz.getMap("myMap");
        logger.info("Map Size: {}", map.size());

        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(RandomEntriesSupplier.class);

        p.readFrom(SourceBuilder.batch("random-numbers-source", RandomEntriesSupplier::new)
                        .fillBufferFn(RandomEntriesSupplier::fillBuffer)
                        .build())
                .writeTo(Sinks.mapWithUpdating("myMap",
                        Map.Entry::getKey,
                        (oldValue, entry) -> entry.getValue()));

        hz.getJet().newJob(p, jobConfig).join();

        hz.getJet().newJob(p, jobConfig).join();

        logger.info("Map Size: {}", map.size());
        hz.shutdown();
        logger.info("Triggered pipeline");
    }
}

