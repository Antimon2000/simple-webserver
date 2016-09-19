package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class IfNoneMatchHandler extends AMatchHandler {

    public IfNoneMatchHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    String getHeaderName() {
        return "If-None-Match";
    }

    @Override
    int getErrorCode() {
        return HttpStatus.SC_NOT_MODIFIED;
    }

    @Override
    boolean shouldForward(boolean hasTagsMatching) {
        return !hasTagsMatching;
    }

    @Override
    protected void performHandling(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // As the If-None-Match header is present, the header If-Modified-Since MUST be ignored (see RFC 7232)
        request.removeHeaders(IfModifiedSinceHandler.HEADERNAME_IF_MODIFIED_SINCE);

        super.performHandling(request, response, context);
    }
}