package com.thatninjaguyspeaks.hazelcast.utils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

public class KeyGenerator {

    private final FlakeIdGenerator idGenerator;

    public KeyGenerator(String mapName, HazelcastInstance hazelcastInstance) {
        this.idGenerator = hazelcastInstance.getFlakeIdGenerator(mapName + "KeyGenerator");
    }

    public long getKey() {
        return idGenerator.newId();
    }

    public String getStringKey() {
        return "String" + getKey();
    }
}
