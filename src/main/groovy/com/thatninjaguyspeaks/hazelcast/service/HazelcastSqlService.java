package com.thatninjaguyspeaks.hazelcast.service;

import com.thatninjaguyspeaks.hazelcast.dto.SqlInputRequestDTO;

public interface HazelcastSqlService {
    Object executeSqlQuery(SqlInputRequestDTO sqlRequest);

    void createSqlMapping(SqlInputRequestDTO sqlRequest);
}
