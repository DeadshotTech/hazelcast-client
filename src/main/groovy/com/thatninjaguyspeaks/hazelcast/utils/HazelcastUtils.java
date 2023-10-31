package com.thatninjaguyspeaks.hazelcast.utils;

import com.thatninjaguyspeaks.hazelcast.models.MapConfiguration;

public class HazelcastUtils {

    public static Object getKey(String keyType, KeyGenerator keyGenerator) {
        switch (keyType.toLowerCase()) {
            case "string": return keyGenerator.getStringKey();
            default:
                return keyGenerator.getKey();
        }
    }
}
