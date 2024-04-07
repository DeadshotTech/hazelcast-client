package com.thatninjaguyspeaks.hazelcast.sources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class ApiSource implements Serializable {
    public static StreamSource<String> buildApiStreamSource() {
        return SourceBuilder
                .stream("api-source", procCtx -> new ApiStreamReader())
                .<String>fillBufferFn(ApiStreamReader::fillBuffer)
                .destroyFn(ApiStreamReader::close)
                .build();
    }

    private static class ApiStreamReader implements Serializable {
        private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

        public void fillBuffer(SourceBuilder.SourceBuffer<String> buffer) {
            try {
                String response = Request.Get(API_URL).execute().returnContent().asString();
                ObjectMapper mapper = new ObjectMapper();
                List<Post> posts = mapper.readValue(response, new TypeReference<List<Post>>(){});
                posts.forEach(post -> buffer.add(post.toString()));
                // Sleep to prevent too frequent polling, adjust as needed
//                TimeUnit.SECONDS.sleep(10);
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to fetch data from API", e);
            }
        }

        public void close() {
            // Any cleanup code when source is destroyed
        }
    }

    private static class Post implements Serializable{
        public int userId;
        public int id;
        public String title;
        public String body;

        @Override
        public String toString() {
            return "{" +
                    "userId=" + userId +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", body='" + body + '\'' +
                    '}';
        }
    }
}