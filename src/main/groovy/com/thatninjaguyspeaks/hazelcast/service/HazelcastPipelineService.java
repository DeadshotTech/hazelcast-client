package com.thatninjaguyspeaks.hazelcast.service;

import reactor.core.publisher.Flux;

import java.io.InputStream;

public interface HazelcastPipelineService {

    void triggerPipeline();
    void uploadCsvData(InputStream fileStream);
    Flux<String> search(String data);
    Flux<String> loadApiData();

}
