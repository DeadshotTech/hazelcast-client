package com.thatninjaguyspeaks.hazelcast.service;

import reactor.core.publisher.Flux;

public interface HazelcastPipelineService {

    void triggerPipeline();

    Flux<String> search(String data);
}
