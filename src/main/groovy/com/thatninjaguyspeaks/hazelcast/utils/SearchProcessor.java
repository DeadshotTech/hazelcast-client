package com.thatninjaguyspeaks.hazelcast.utils;

import com.hazelcast.function.PredicateEx;

import java.io.Serializable;
import java.util.Map;

public class SearchProcessor implements PredicateEx<Map.Entry<String, String>>, Serializable {

    private String searchString;
    public SearchProcessor(String searchString) {
        this.searchString = searchString;
    }
    @Override
    public boolean testEx(Map.Entry<String, String> entry) throws Exception {
        return entry.getValue().contains(searchString);
    }

}