package de.stackoverflo.simplewebserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleWebserver {

    private static Logger logger = LogManager.getLogger(SimpleWebserver.class);

    private int listenPort;
    private int threadsPerCore;
    private String documentRoot;
    private boolean isAcceptingNewRequests;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public SimpleWebserver(int port, String documentRoot, int threadsPerCore) {
        if (port <= 0 || threadsPerCore <= 0) {
            throw new IllegalArgumentException("numerals must be greater than zero");
        }

        this.listenPort = port;
        this.documentRoot = documentRoot;
        this.threadsPerCore = threadsPerCore;
    }

    public void startServer() {
        isAcceptingNewRequests = true;

        Socket socket;
        executor = Executors.newFixedThreadPool(getThreadPoolSize());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopServer();
            }
        });

        try {
            serverSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isAcceptingNewRequests) {
            try {
                socket = serverSocket.accept();
                executor.execute(new Thread(new HttpRequestListener(socket, new File(documentRoot))));
            } catch (IOException e) {
                if (isAcceptingNewRequests) {
                    logger.error(e.getStackTrace());
                }
            }
        }
    }


    public void stopServer() {
        isAcceptingNewRequests = false;
        executor.shutdownNow();
    }


    private int getThreadPoolSize() {
        return Runtime.getRuntime().availableProcessors() * threadsPerCore;
    }
}
