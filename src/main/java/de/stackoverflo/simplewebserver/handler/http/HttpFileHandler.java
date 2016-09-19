package de.stackoverflo.simplewebserver.handler.http;

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

        /*
         * Response handlers in the spirit of the Chain of Responsibility design pattern.
         *
         * Precedence of conditional handlers according to RFC 7232 Section 6.
         */
        ResponseHandler rh =
            new FileNotFoundHandler(
                new IfMatchHandler(
                    new IfNoneMatchHandler(
                        new IfModifiedSinceHandler(
                            new DirectoryListingHandler(
                                new ServeFileHandler()
                            )
                        )
                    )
                )
            );

            rh.handle(request, response, context);
    }
}