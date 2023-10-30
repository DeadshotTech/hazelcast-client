package com.thatninjaguyspeaks.hazelcast.service;

import org.springframework.stereotype.Service;

@Service
public interface HazelcastSqlService {
    Object executeSqlQuery();
}
