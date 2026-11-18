package by.example.fooddelivery.monitoring;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MetricsServer implements AutoCloseable {
    private final HttpServer server;

    public MetricsServer(PrometheusMeterRegistry registry, int port) throws IOException {
        server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", port), 0);

        server.createContext("/metrics", exchange -> {
            byte[] response = registry.scrape()
                    .getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/plain; version=0.0.4; charset=utf-8");

            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream output = exchange.getResponseBody()) {
                output.write(response);
            }
        });
    }

    public void start() {
        server.start();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}