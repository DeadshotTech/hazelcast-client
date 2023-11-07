package com.thatninjaguyspeaks.hazelcast.service;

import com.thatninjaguyspeaks.hazelcast.dto.SqlRequest;
import org.springframework.stereotype.Service;

@Service
public interface HazelcastSqlService {
    Object executeSqlQuery(SqlRequest sqlRequest);

    void createSqlMapping(SqlRequest sqlRequest);
}
