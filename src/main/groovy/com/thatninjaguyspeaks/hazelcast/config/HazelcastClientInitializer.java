package com.thatninjaguyspeaks.hazelcast.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientUserCodeDeploymentConfig;
import com.hazelcast.core.HazelcastInstance;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HazelcastClientInitializer {

    private static final Logger logger = LogManager.getLogger(HazelcastClientInitializer.class);
    @Autowired
    HazelcastInstance hazelcastInstanceDefault;
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public synchronized HazelcastInstance getHazelcastInstance() {
        if(hazelcastInstance==null){
            hazelcastInstance = hazelcastInstanceDefault==null
                    ? HazelcastClient.newHazelcastClient()
                    :hazelcastInstanceDefault;
            logger.info("Hazelcast client instance initialized");
        }
        return hazelcastInstance;
    }

//    private ClientConfig createHazelcastConfig(){
//        ClientConfig clientConfig = new ClientConfig();
//        ClientUserCodeDeploymentConfig userCodeDeploymentConfig = new ClientUserCodeDeploymentConfig();
//        userCodeDeploymentConfig.setEnabled(true);
//        userCodeDeploymentConfig.addClass("com.thatninjaguyspeaks.hazelcast.avro.Person");
//        userCodeDeploymentConfig.addClass("com.thatninjaguyspeaks.hazelcast.avro.Employee");
//        clientConfig.setUserCodeDeploymentConfig(userCodeDeploymentConfig);
//        logger.info("Hazelcast Client Config: {}", clientConfig);
//        return clientConfig;
//    }

//    public void forceHazelcastClientCreation(){
//        try {
//            hazelcastInstance.shutdown();
//            hazelcastInstance = null;
//            hazelcastInstance = HazelcastClient.newHazelcastClient(createHazelcastConfig());
//            logger.info("Hazelcast instance is recreated");
//        }catch(Exception e){
//            logger.error("Hazelcast force creation failed with error: {}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
}