package de.stackoverflo.simplewebserver;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import org.apache.http.*;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.protocol.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class HttpRequestListener implements Runnable {

    private static Logger logger = LogManager.getLogger(HttpRequestListener.class);

    public static final int BUFFER_SIZE = 8 * 1024;

    private Socket socket;
    private File documentRoot;

    public HttpRequestListener(Socket socket, File documentRoot) {
        this.socket = socket;
        this.documentRoot = documentRoot;
    }

    @Override
    public void run() {
        DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(HttpRequestListener.BUFFER_SIZE);

        try {
            conn.bind(socket);

            UriHttpRequestHandlerMapper requestHandlerMapper = new UriHttpRequestHandlerMapper();
            requestHandlerMapper.register("*", new HttpFileHandler(documentRoot));

            HttpProcessor httpProcessor = HttpProcessorBuilder.create()
                .add(new ResponseDate())
                .add(new ResponseServer("SimpleWebserver/1.1"))
                .add(new ResponseContent())
                .add(new ResponseConnControl())
                .build();

            HttpService httpService = new HttpService(httpProcessor, requestHandlerMapper);

            int reuseCount = 1;
            while (conn.isOpen()) {
                httpService.handleRequest(conn, HttpCoreContext.create());
                logger.debug("Handled request #" + reuseCount++ + " on connection");
            }
        } catch (IOException | HttpException e) {
            logger.error(e.getMessage());
        }
    }
}
