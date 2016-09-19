package de.stackoverflo.simplewebserver.handler.http;
/*
 * Parts of the class below were derived from an example of Apache's httpcore under
 * https://hc.apache.org/httpcomponents-core-4.4.x/httpcore/examples/org/apache/http/examples/HttpFileServer.java
 *
 * It has been reduced to only do what's required for the task at hand and was adjusted for the sake of cleaner OOP
 * design.
 *
 */

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Locale;

import de.stackoverflo.simplewebserver.handler.response.*;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class HttpFileHandler implements HttpRequestHandler {

    public static final String KEY_ATTR_FILE = "file";

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
        if (!method.equals("GET") && !method.equals("HEAD")) {
            throw new MethodNotSupportedException(method + " method not supported");
        }
        String target = request.getRequestLine().getUri();

        final File file = new File(this.documentRoot, URLDecoder.decode(target, "UTF-8"));
        context.setAttribute(KEY_ATTR_FILE, file);

        if (!file.exists()) {
            new FileNotFoundHandler().handle(request, response, context);

        } else if (!file.canRead()) {
            new AccessDeniedHandler().handle(request, response, context);

        } else if (file.isDirectory()) {
            new DirectoryListingHandler(file).handle(request, response, context);

        } else {
            /*
             * Response handlers in the spirit of the Chain-of-Responsibility design pattern.
             *
             * Precedence according to RFC 7232 Section 6.
             */
            ResponseHandler rh =
                new IfMatchHandler(
                    new IfNoneMatchHandler(
                        new IfModifiedSinceHandler(
                            new ServeFileHandler(file)
                        )
                    )
                );

            rh.handle(request, response, context);
        }
    }
}