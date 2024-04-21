package com.thatninjaguyspeaks.hazelcast.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.datamodel.Tuple2;
import com.hazelcast.jet.pipeline.*;
import com.hazelcast.map.IMap;
import com.thatninjaguyspeaks.hazelcast.config.HazelcastClientInitializer;
import com.thatninjaguyspeaks.hazelcast.service.HazelcastPipelineService;
import com.thatninjaguyspeaks.hazelcast.sources.ApiBatchSource;
import com.thatninjaguyspeaks.hazelcast.sources.SocketSource;
import com.thatninjaguyspeaks.hazelcast.utils.FilterProcessor;
import com.thatninjaguyspeaks.hazelcast.utils.LineProcessor;
import com.thatninjaguyspeaks.hazelcast.utils.SearchProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class HazelcastPipelineServiceImpl implements HazelcastPipelineService {

    private final Logger logger = LogManager.getLogger(HazelcastPipelineServiceImpl.class);
    @Autowired
    HazelcastClientInitializer hazelcastClientInitializer;

    @Override
    public void triggerPipeline() {
        writeExcelDataToSocket();
//        loadDataFromExcel();
//        loadFilteredDataFromExcel();
    }

    private void loadDataFromExcel() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        // Define the pipeline
        Pipeline p = Pipeline.create();
        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(LineProcessor.class);
        // Corrected file source setup

//        BatchSource<String> fileSource = Sources.files("/Users/deadshot/Desktop/Code/hazelcast-client-data-interface/hazelcast-client-data-interface/src/main/resources/test");
        BatchSource<String> fileSource = Sources.files("src/main/resources/test");
        p.readFrom(fileSource)
                .map(new LineProcessor())
                .writeTo(Sinks.map("csvMap"));
        try {
            hz.getJet().newJob(p, jobConfig).join();
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging
        }
//        hz.shutdown();
    }

    @EventListener(ApplicationReadyEvent.class)
    private void startSocketReadPipeline() {
        HazelcastInstance hz = hazelcastClientInitializer.getHazelcastInstance();
        Pipeline p = Pipeline.create();
        JobConfig jobConfig = new JobConfig()
                .setName("socketReadPipeline")
                .addClass(SocketSource.class)
                .addClass(LineProcessor.class)
                .addClass(FilterProcessor.class);

        try {
            p.readFrom(SocketSource.buildNetworkSource())
                    .withoutTimestamps()
                    .map(new LineProcessor())
                    .peek()
                    .filter(new FilterProcessor())
                    .writeTo(Sinks.map("csvFilteredMapSocket"));

            Job job = hz.getJet().newJobIfAbsent(p, jobConfig);
            job.join();
            logger.info("Socket read pipeline job submitted and running.");
        } catch (Exception e) {
            logger.error("Failed to start socket read pipeline", e);
        }
    }

    private void writeExcelDataToSocket() {
        String filePath = "src/main/resources/test/nasdaqlisted.csv";
        String host = "localhost";
        int port = 9097;
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Stream<String> lines = Files.lines(Paths.get(filePath))) {
            logger.info("Connected to the socket at {}:{}", host, port);
            lines.forEach(out::println);
        } catch (IOException e) {
            logger.error("Failed to write data to socket: " + e.getMessage(), e);
        }
    }

    @Override
    public void uploadCsvData(InputStream fileStream) {
        String host = "localhost";
        int port = 9097;
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
             Stream<String> lines = reader.lines();) {
            logger.info("Connected to the socket at {}:{}", host, port);
            lines.forEach(out::println);
        } catch (IOException e) {
            logger.error("Failed to write data to socket: " + e.getMessage(), e);
            throw new RuntimeException("Failed to send data over socket", e);
        }
    }

    private void loadFilteredDataFromExcel() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        // Define the pipeline
        Pipeline p = Pipeline.create();
        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(LineProcessor.class);
        jobConfig.addClass(FilterProcessor.class);
        // Corrected file source setup
        BatchSource<String> fileSource = Sources.files("/Users/deadshot/Desktop/Code/hazelcast-client-data-interface/hazelcast-client-data-interface/src/main/resources/test");
        p.readFrom(fileSource)
                .map(new LineProcessor())
                .filter(new FilterProcessor())
                .writeTo(Sinks.map("csvFilteredMap"));
        hz.getJet().newJob(p, jobConfig).join();
    }

    @Override
    public Flux<String> search(String searchString) {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        JobConfig jobConfig = new JobConfig();
        jobConfig.addClass(SearchProcessor.class);
        // Prepare a Flux to stream search results to the client.
        return Flux.create(fluxSink -> {
            Pipeline pipeline = buildSearchPipeline(searchString, fluxSink);
            // Execute the pipeline
            hz.getJet().newJob(pipeline, jobConfig).join();
        });
    }

    private Pipeline buildSearchPipeline(String searchString, FluxSink<String> fluxSink) {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        Pipeline pipeline = Pipeline.create();

        // Assuming the map contains String keys and values. Adjust according to your actual data type.
        IMap<String, String> map = hz.getMap("csvMap");

        SearchProcessor searchProcessor = new SearchProcessor(searchString);
        BatchStage<Map.Entry<String, String>> sourceStage = pipeline
                .readFrom(Sources.map(map))
                // Use a static method reference for filtering
                .filter(searchProcessor)
                .peek()
                .setName("searchFilter"+searchString);

        sourceStage.writeTo(Sinks.observable("searchResults"));

        // Listen to the observable sink and push results to the Flux
        hz.getJet().getObservable("searchResults")
                .addObserver(event -> fluxSink.next(event.toString()));

        return pipeline;
    }
    public class SearchServiceUtil {
        // Make sure this class is available in Hazelcast's classpath
        public static boolean filterBySearchString(Map.Entry<String, String> entry, String searchString) {
            return entry.getValue().contains(searchString);
        }
    }

    @Override
    public Flux<String> loadApiData() {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        JobConfig jobConfig = new JobConfig();
//        jobConfig.addClass(ApiSource.class);
        jobConfig.addJar("/Users/deadshot/Desktop/Code/hazelcast-client-data-interface/hazelcast-client-data-interface/build/libs/hazelcast-client-interface-fat-0.0.1-SNAPSHOT.jar");
        return Flux.create(fluxSink -> {
            Pipeline pipeline = buildApiDataLoadPipeline(fluxSink);
            hz.getJet().newJob(pipeline, jobConfig).join();
        });
    }
    private Pipeline buildApiDataLoadPipeline(FluxSink<String> fluxSink) {
        var hz = hazelcastClientInitializer.getHazelcastInstance();
        Pipeline pipeline = Pipeline.create();

        BatchStage<Tuple2<Integer, String>> transformedStage = pipeline
                .readFrom(ApiBatchSource.buildApiStreamSource())
                .map(item -> Tuple2.tuple2(item.hashCode(), item))  // Transform string to a key-value pair
                .setName("apiDataSource");

        // Stream only the value part (original string) to an observable
        transformedStage.map(Tuple2::getValue)
                .writeTo(Sinks.observable("apiDataResults"));
        // Listen to the observable sink and push results to the Flux
        hz.getJet().getObservable("apiDataResults")
                .addObserver(event -> fluxSink.next(event.toString()));

        // Write key-value pairs to the Hazelcast map
        transformedStage
                .writeTo(Sinks.map("apiDataMap"));

        return pipeline;
    }

}


