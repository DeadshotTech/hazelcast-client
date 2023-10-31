package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.sql.SqlStatement;
import com.thatninjaguyspeaks.hazelcast.avro.Person;
import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.models.MapConfiguration;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastSqlService;
import com.thatninjaguyspeaks.hazelcast.utils.HazelcastUtils;
import com.thatninjaguyspeaks.hazelcast.utils.KeyGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HazelcastSqlServiceImpl implements HazelcastSqlService {

    private static final Logger logger = LogManager.getLogger(HazelcastSqlServiceImpl.class);

    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public Object executeSqlQuery() {
        logger.info("Starting sql query execution");
        final var hz = hazelcastClientInitializer.getHazelcastInstance();
        final var mapName = "person_map";
        final var keyType = "String";
        final KeyGenerator keyGenerator = new KeyGenerator(mapName, hz);
        var sqlService = hz.getSql();
        SqlStatement statement = new SqlStatement(
                "INSERT INTO person_map VALUES (?, ?, ?);"
        );

        Person person = new Person();
        person.setAge(25);
        person.setName("Alice");
        statement.setParameters(List.of(HazelcastUtils.getKey(keyType, keyGenerator), person.getAge(), person.getName()));
        try(var sqlResult = sqlService.execute(statement)){
            logger.info("SqlResult: {}", sqlResult.updateCount());
        }
        return null;
    }

    public void createSqlMapping(){
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
        logger.info("SqlResult: {}", sqlResult);
    }
}
