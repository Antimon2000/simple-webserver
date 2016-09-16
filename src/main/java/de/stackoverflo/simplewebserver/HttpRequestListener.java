package de.stackoverflo.simplewebserver;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.*;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class HttpRequestListener implements Runnable {

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

            while (conn.isOpen()) {
                httpService.handleRequest(conn, HttpCoreContext.create());
            }

            /*
            HttpRequest request = conn.receiveRequestHeader();
            if (request instanceof HttpEntityEnclosingRequest) {
                conn.receiveRequestEntity((HttpEntityEnclosingRequest) request);
                HttpEntity entity = ((HttpEntityEnclosingRequest) request)
                        .getEntity();
                if (entity != null) {
                    // Do something useful with the entity and, when done, ensure all
                    // content has been consumed, so that the underlying connection
                    // could be re-used
                    EntityUtils.consume(entity);
                }
            }
            HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                    200, "OK");
            response.setEntity(new StringEntity("Got it"));
            conn.sendResponseHeader(response);
            conn.sendResponseEntity(response);
            */
        } catch (IOException | HttpException e) {

        }
    }
}
