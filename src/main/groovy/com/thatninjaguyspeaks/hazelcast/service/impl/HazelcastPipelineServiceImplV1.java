package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.*;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.thatninjaguyspeaks.hazelcast.avro.Employee;
import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastPipelineService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.AbstractMap;
import java.util.Map;

public class HazelcastPipelineServiceImplV1 implements HazelcastPipelineService {
    private final Logger logger = LogManager.getLogger(HazelcastPipelineServiceImplV1.class);
    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public void triggerPipeline() {
//        writeMap();
//        transferDataAcrossMapWithLogging();
    }

    private void transferDataAcrossMap() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        Pipeline p = Pipeline.create();
        p.readFrom(Sources.map("employee_map")) // Read from the source map
                .writeTo(Sinks.map("myMap")); // Write to the destination map

        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(Employee.class);
        hz.getJet().newJob(p, jobConfig);
    }

    private void transferDataAcrossMapWithLogging() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        Pipeline p = Pipeline.create();
        BatchStage<Map.Entry<String, Employee>> src = p.readFrom(Sources.map("myMap"));
        src.writeTo(Sinks.map("employee_map"));
        src.writeTo(Sinks.logger());
        hz.getJet().newJob(p);
        p.readFrom(Sources.map("myMap")) // Read from the source map
                .writeTo(Sinks.map("employee_map")); // Write to the destination map

        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(Employee.class);
        jobConfig.addClass(HazelcastPipelineServiceImpl.class);
        hz.getJet().newJob(p, jobConfig);
    }

//    Not working
    private void writeMap(){
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        var p = Pipeline.create();

        IMap<Long, Long> map = hz.getMap("myMap");
        logger.info("Map Size: {}", map.size());

        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(RandomEntriesSupplier.class);
        jobConfig.addClass(RandomEntriesSupplier.FillBufferRunnable.class);

//        p.readFrom(TestSources.itemStream(10))
//                .withoutTimestamps()
//                .filter(event -> event.sequence() % 2 == 0)
//                .setName("filter out odd numbers")
//                .writeTo(Sinks.logger());
        p.readFrom(SourceBuilder.batch("random-numbers-source", ctx -> new RandomEntriesSupplier(10_000_000).applyEx(ctx))
                        .fillBufferFn(RandomEntriesSupplier.FillBufferRunnable::fillBuffer)
                        .build())
                .writeTo(Sinks.mapWithUpdating("myMap",
                        Map.Entry::getKey,
                        (oldValue, entry) -> entry.getValue()));

//        p.readFrom(SourceBuilder.batch("random-numbers-source", ctx -> new RandomEntriesSupplier(10_000_000))
//                        .fillBufferFn((RandomEntriesSupplier.FillBufferRunnable supplier, SourceBuffer<Map.Entry<Long, Long>> buffer)
//                                -> supplier.fillBuffer(buffer))
//                        .build())
//                .writeTo(Sinks.mapWithUpdating("myMap",
//                        Map.Entry::getKey,
//                        (oldValue, entry) -> entry.getValue()));

        hz.getJet().newJob(p, jobConfig);
//        hz.getJet().newJob(p);
        logger.info("Map Size: {}", map.size());
        hz.shutdown();
        logger.info("Triggered pipeline");
    }
    static class IncrementEntryProcessor implements EntryProcessor<String, Employee, Employee> {
        @Override
        public Employee process(Map.Entry<String, Employee> entry) {
            return entry.setValue(Employee.newBuilder()
                    .setSoeId(entry.getValue().getSoeId())
                    .setUserName(entry.getValue().getUserName()+entry.getValue().getSoeId())
                    .build());
        }
    }
}
