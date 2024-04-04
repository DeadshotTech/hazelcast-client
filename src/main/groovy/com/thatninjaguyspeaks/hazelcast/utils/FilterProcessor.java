package com.thatninjaguyspeaks.hazelcast.utils;

import com.hazelcast.function.PredicateEx;

import java.io.Serializable;
import java.util.Map;

public class FilterProcessor implements PredicateEx<Map.Entry<String, String>>, Serializable {

        @Override
        public boolean testEx(Map.Entry<String, String> entry) throws Exception {
            return entry.getValue().split(",")[0].contains("A");
        }

}
