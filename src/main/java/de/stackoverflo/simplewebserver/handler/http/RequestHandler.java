package de.stackoverflo.simplewebserver.handler.http;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private static Logger logger = LogManager.getLogger(RequestHandler.class);

    private static final int BUFFER_SIZE = 9192;

    private Socket socket;
    private String documentRoot;

    public RequestHandler(Socket socket, String documentRoot) {
        this.socket = socket;
        this.documentRoot = documentRoot;
    }

    public void run() {
        logger.trace("Starting new request handler");

        DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(RequestHandler.BUFFER_SIZE);

        try {
            conn.bind(socket);
            HttpRequest request = conn.receiveRequestHeader();
            logger.trace("Received request header");
            if (request instanceof HttpEntityEnclosingRequest) {
                logger.trace("Received entity enclosing request");

                conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
                HttpEntity entity = ((HttpEntityEnclosingRequest) request)
                        .getEntity();
                if (entity != null) {
                    // Do something useful with the entity and, when done, ensure all
                    // content has been consumed, so that the underlying connection
                    // could be re-used
                    System.out.println("Length: " + entity.getContentLength());

                    EntityUtils.consume(entity);
                }
            }

            logger.trace("Sending response");
            HttpResponse response = new BasicHttpResponse(
                    HttpVersion.HTTP_1_1, 200, "OK");
            response.setEntity(new StringEntity("Got it"));
            conn.sendResponseHeader(response);
            conn.sendResponseEntity(response);
            conn.flush();
        } catch (HttpException | IOException e) {
            logger.error(e.getStackTrace());
        }
    }
}