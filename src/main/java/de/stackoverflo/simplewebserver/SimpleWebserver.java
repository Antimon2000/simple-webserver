package de.stackoverflo.simplewebserver;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

public class SimpleWebserver {

    private int listenPort;
    private String documentRoot;
    private boolean isAcceptingNewRequests;

    public SimpleWebserver(int port, String documentRoot) {
        if (port <= 0) {
            throw new IllegalArgumentException("numerals must be greater than zero");
        }

        this.listenPort = port;
        this.documentRoot = documentRoot;
    }

    public void acceptConnections() {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        final HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(listenPort)
                .setServerInfo("Test/1.1")
                .setSocketConfig(socketConfig)
                .registerHandler("*", new HttpFileHandler(new File(documentRoot)))
                .create();

        try {
            server.start();
            server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.shutdown(5, TimeUnit.SECONDS);
            }
        });
    }
}
