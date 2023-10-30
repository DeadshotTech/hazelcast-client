package com.thatninjaguyspeaks.hazelcast.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thatninjaguyspeaks.hazelcast.models.MapConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ConfigurationLoader {


    private static Resource configFile;
    @Value("classpath:map-configurations.yaml")
    public void setConfigFile(Resource configFile) {
        ConfigurationLoader.configFile = configFile;
    }

    private static List<MapConfiguration> mapConfigurations;

    @PostConstruct
    public static void loadConfigurations() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        MapConfigurationContainer container = mapper.readValue(configFile.getInputStream(), MapConfigurationContainer.class);
        mapConfigurations = container.getMapConfigurations();
    }

    public synchronized List<MapConfiguration> getMapConfigurations() throws IOException {
        if(mapConfigurations.isEmpty())
            loadConfigurations();
        return mapConfigurations;
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class MapConfigurationContainer {
    private List<MapConfiguration> mapConfigurations;
}
