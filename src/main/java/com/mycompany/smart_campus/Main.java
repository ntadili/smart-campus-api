/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;

/**
 *
 * @author nassertadili
 */
public final class Main {
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_PORT = 8090;
    private static final String CONTEXT_PATH = "/api/v1/";

    private Main() {
    }
    
    public static void main(String[] args) throws Exception {
        int port = resolvePort();
        URI baseUri = URI.create("http://" + DEFAULT_HOST + ":" + port + CONTEXT_PATH);

        ResourceConfig config = ResourceConfig.forApplicationClass(SmartCampusApplication.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config, false);
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow, "smart-campus-shutdown"));
        server.start();

        String publicUri = "http://localhost:" + port + "/api/v1";
        System.out.println();
        System.out.println("Smart Campus API started");
        System.out.println("  Base URI : " + publicUri);
        System.out.println("  Discovery: " + publicUri + "/");
        System.out.println();

        Thread.currentThread().join();
    }

    private static int resolvePort() {
        String env = System.getenv("PORT");
        if (env != null && !env.isBlank()) {
            try {
                return Integer.parseInt(env.trim());
            } catch (NumberFormatException ignored) {
                System.err.println("Invalid PORT env var; falling back to " + DEFAULT_PORT);
            }
        }
        return DEFAULT_PORT;
    }
}
