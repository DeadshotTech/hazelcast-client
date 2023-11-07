package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.sql.SqlStatement;
import com.thatninjaguyspeaks.hazelcast.avro.Person;
import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.dto.SqlRequest;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastSqlService;
import com.thatninjaguyspeaks.hazelcast.utils.HazelcastUtils;
import com.thatninjaguyspeaks.hazelcast.utils.KeyGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.ClassUtils.isPrimitiveOrWrapper;

@Service
public class HazelcastSqlServiceImpl implements HazelcastSqlService {

    private static final Logger logger = LogManager.getLogger(HazelcastSqlServiceImpl.class);

    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public Object executeSqlQuery(SqlRequest sqlRequest) {
        logger.info("Starting sql query execution");
        final var hz = hazelcastClientInitializer.getHazelcastInstance();
        final KeyGenerator keyGenerator = new KeyGenerator(sqlRequest.getMapName(), hz);
        var sqlService = hz.getSql();
        Person person = new Person();
        person.setAge(25);
        person.setName("Alice");

        SqlStatement statement = getSqlInsertStatement(person, sqlRequest.getMapName(),
                sqlRequest.getKeyType(), keyGenerator);
        logger.info("Statement: {}", statement);
        try(var sqlResult = sqlService.execute(statement)){
            logger.info("SqlResult: {}", sqlResult.updateCount());
        }
        return null;
    }



    private SqlStatement getSqlInsertStatement(Object person, String mapName, String keyType, KeyGenerator keyGenerator) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(mapName).append(" VALUES (?,");
        Method[] methods = getGetterMethodsForSqlInsert(person);
        for (Method method : methods) {
            sql.append(" ?,");
        }
        sql.setLength(sql.length() - 1);  // Remove trailing comma
        sql.append(");");
        logger.info("Sql query: {}", sql);
        SqlStatement statement = new SqlStatement(sql.toString());
        List<Object> params = new ArrayList<>();
        params.add(HazelcastUtils.getKey(keyType, keyGenerator));
        for (Method method : methods) {
            try {
                params.add(method.invoke(person));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();  // Handle exception
            }
        }
        logger.info("Parameters: {}", params);
        statement.setParameters(params);
        return statement;
    }

    private Method[] getGetterMethodsForSqlInsert(Object person) {
        return Arrays.stream(person.getClass().getMethods())
                .filter(method -> method.getName().startsWith("get"))
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> isPrimitiveOrWrapper(method.getReturnType()) || method.getReturnType().equals(String.class))
                .toArray(Method[]::new);
    }


    @Override
    public void createSqlMapping(SqlRequest sqlRequest){
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        var sqlService = hz.getSql();
        String statement = getSql(sqlRequest.getMapName());
        var sqlResult = sqlService.execute(statement);
        logger.info("SqlResult: {}", sqlResult);
    }

    private String getSql(String mapName) {
        if(mapName.toLowerCase().contains("person"))
            return "CREATE OR REPLACE EXTERNAL MAPPING \"hazelcast\".\"public\".\""+mapName+"\" EXTERNAL NAME \""+mapName+"\" ( \n" +
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
                    ");";
        else
            return "CREATE OR REPLACE EXTERNAL MAPPING \"hazelcast\".\"public\".\""+mapName+"\" EXTERNAL NAME \""+mapName+"\" ( \n" +
                    "  \"__key\" VARCHAR,\n" +
                    "  \"soeId\" INTEGER,\n" +
                    "  \"userName\" VARCHAR\n" +
                    ")\n" +
                    "TYPE \"IMap\"\n" +
                    "OPTIONS (\n" +
                    "  'keyFormat'='java',\n" +
                    "  'keyJavaClass'='java.lang.String',\n" +
                    "  'valueFormat'='java',\n" +
                    "  'valueJavaClass'='com.thatninjaguyspeaks.hazelcast.avro.Employee'\n" +
                    ");";
    }

    public String convertJavaTypesToHazelcastTypes(String className){
        String hazelcastType;
        switch (className.toLowerCase()) {
            case "string":
                hazelcastType="VARCHAR";
                break;
            default:
                hazelcastType = null;
        }
        return hazelcastType;
    }
}
