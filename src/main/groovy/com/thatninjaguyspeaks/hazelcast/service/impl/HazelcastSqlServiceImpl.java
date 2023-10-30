package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastSqlService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HazelcastSqlServiceImpl implements HazelcastSqlService {

    private static final Logger logger = LogManager.getLogger(HazelcastSqlServiceImpl.class);

    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public Object executeSqlQuery() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        var sqlService = hz.getSql();
        var sqlResult = sqlService.execute("CREATE OR REPLACE EXTERNAL MAPPING \"hazelcast\".\"public\".\"person_map\" EXTERNAL NAME \"person_map\" ( \n" +
                "  \"__key\" VARCHAR,\n" +
                "  \"age\" INTEGER,\n" +
                "  \"name\" VARCHAR\n" +
                ")\n" +
                "TYPE \"IMap\"\n" +
                "OPTIONS (\n" +
                "  'keyFormat'='java',\n" +
                "  'keyJavaClass'='java.lang.String',\n" +
                "  'valueFormat'='java',\n" +
                "  'valueJavaClass'='com.thatninjaguyspeaks.hazelcast.avro.Person'\n" +
                ");");
//        var sqlResult = sqlService.execute("Select * from person_map");
        logger.info("SqlResult: {}", sqlResult);
        return null;
    }
}
