package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.*;

import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastPipelineService;
import com.thatninjaguyspeaks.hazelcast.utils.FilterProcessor;
import com.thatninjaguyspeaks.hazelcast.utils.LineProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HazelcastPipelineServiceImpl implements HazelcastPipelineService {

    private final Logger logger = LogManager.getLogger(HazelcastPipelineServiceImpl.class);
    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public void triggerPipeline() {
        loadDataFromExcel();
//        loadFilteredDataFromExcel();
    }

    private void loadDataFromExcel() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        // Define the pipeline
        Pipeline p = Pipeline.create();
        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(LineProcessor.class);
        // Corrected file source setup
        BatchSource<String> fileSource = Sources.files("/Users/deadshot/Desktop/Code/hazelcast-client-data-interface/hazelcast-client-data-interface/src/main/resources/test");
        p.readFrom(fileSource)
                .map(new LineProcessor())
                .writeTo(Sinks.map("csvMap"));
        hz.getJet().newJob(p, jobConfig).join();
//        hz.shutdown();
    }

    private void loadFilteredDataFromExcel() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        // Define the pipeline
        Pipeline p = Pipeline.create();
        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(LineProcessor.class);
        jobConfig.addClass(FilterProcessor.class);
        // Corrected file source setup
        BatchSource<String> fileSource = Sources.files("/Users/deadshot/Desktop/Code/hazelcast-client-data-interface/hazelcast-client-data-interface/src/main/resources/test");
        p.readFrom(fileSource)
                .map(new LineProcessor())
                .filter(new FilterProcessor())
                .writeTo(Sinks.map("csvFilteredMap"));
        hz.getJet().newJob(p, jobConfig).join();
//        hz.shutdown();
    }
}

