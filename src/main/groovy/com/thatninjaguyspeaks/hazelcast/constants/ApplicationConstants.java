package com.thatninjaguyspeaks.hazelcast.constants;

import com.hazelcast.core.HazelcastJsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationConstants {

    public static final List<Map<Integer, HazelcastJsonValue>> MAP_DEFAULT_DATA = new ArrayList<>();
    static {
        MAP_DEFAULT_DATA.add(new HashMap<>());
    }
}
