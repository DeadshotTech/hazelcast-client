package com.thatninjaguyspeaks.hazelcast.sources;

import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketSource {
    public static StreamSource<String> buildNetworkSource() {
        return SourceBuilder
                .stream("network-source", procCtx -> {
                    int port = 9090;
                    ServerSocket serverSocket = new ServerSocket(port);
                    procCtx.logger().info("Server socket opened on port " + port);
                    return new NetworkContext(serverSocket);
                })
                .<String>fillBufferFn((context, buf) -> {
                    Socket socket = context.acceptClient();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buf.add(line);
                        }
                    } catch (IOException e) {
                        context.getLogger().error("Error reading from socket: " + e.getMessage(), e);
                    }finally {
                        socket.close();
                    }
                })
                .destroyFn(NetworkContext::close)
                .build();
    }

    private static class NetworkContext {
        private final ServerSocket serverSocket;
        private final Logger logger;

        NetworkContext(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
            this.logger = LoggerFactory.getLogger(getClass());
        }

        Socket acceptClient() throws IOException {
            return serverSocket.accept(); // Accepts a new client connection
        }

        void close() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("Failed to close server socket: " + e.getMessage(), e);
            }
        }

        Logger getLogger() {
            return logger;
        }
    }
}
