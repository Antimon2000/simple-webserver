package de.stackoverflo.simplewebserver.handler.http;
/*
 * The class below was derived from an example of the Apache httpcore-nio packages under
 * http://hc.apache.org/httpcomponents-core-4.4.x/httpcore/examples/org/apache/http/examples/HttpFileServer.java
 *
 * It has been reduced to meet the requirements of the task at hand and was adjusted for the sake of cleaner OOP design.
 *
 * -- Florian Noack, 15.09.2016
 */

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;

import de.stackoverflo.simplewebserver.handler.response.DirectoryListingHandler;
import de.stackoverflo.simplewebserver.handler.response.FileServerHandler;
import de.stackoverflo.simplewebserver.handler.response.ResponseHandler;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

public class HttpFileHandler implements HttpRequestHandler {

    private final File documentRoot;

    public HttpFileHandler(final File documentRoot) {
        super();
        this.documentRoot = documentRoot;
    }

    public void handle(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }
        String target = request.getRequestLine().getUri();

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            byte[] entityContent = EntityUtils.toByteArray(entity);
            System.out.println("Incoming entity content (bytes): " + entityContent.length);
        }

        final File file = new File(this.documentRoot, URLDecoder.decode(target, "UTF-8"));
        if (!file.exists()) {

            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
            StringEntity entity = new StringEntity(
                    "<html><body><h1>File " + file.getPath() +
                            " not found</h1></body></html>",
                    ContentType.create("text/html", "UTF-8"));
            response.setEntity(entity);
            System.out.println("File " + file.getPath() + " not found");

        } else if (!file.canRead()) {

            response.setStatusCode(HttpStatus.SC_FORBIDDEN);
            StringEntity entity = new StringEntity(
                    "<html><body><h1>Access denied</h1></body></html>",
                    ContentType.create("text/html", "UTF-8"));
            response.setEntity(entity);
            System.out.println("Cannot read file " + file.getPath());

        } else if (file.isDirectory()) {
            ResponseHandler directoryListingHandler = new DirectoryListingHandler(file);
            directoryListingHandler.handle(request, response, context);

        } else {
            ResponseHandler fileServerHandler = new FileServerHandler(file);
            fileServerHandler.handle(request, response, context);

        }
    }

}