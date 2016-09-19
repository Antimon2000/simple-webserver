package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;

abstract class AResponseHandler implements ResponseHandler {

    ResponseHandler responseHandler;

    AResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public final void handle(
            HttpRequest request,
            HttpResponse response,
            HttpContext context) throws HttpException, IOException {

        if (isApplicable(request, context)) {
            performHandling(request, response, context);
        } else {
            if (responseHandler != null) {
                responseHandler.handle(request, response, context);
            }
        }
    }

    abstract boolean isApplicable(HttpRequest request, HttpContext context);

    protected abstract void performHandling(
            HttpRequest request,
            HttpResponse response,
            HttpContext context) throws HttpException, IOException;

    File getResource(HttpContext context) {
        return (File) context.getAttribute(HttpFileHandler.KEY_ATTR_FILE);
    }
}
