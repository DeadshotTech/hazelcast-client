package com.thatninjaguyspeaks.hazelcast.utils;

import com.hazelcast.function.FunctionEx;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;

public class LineProcessor implements FunctionEx<String, Map.Entry<String, String>>, Serializable {
    @Override
    public Map.Entry<String, String> applyEx(String line) throws Exception {
        String[] tokens = line.split(",");
        return new AbstractMap.SimpleEntry<>(tokens[0], String.join(",", tokens));
    }

}
