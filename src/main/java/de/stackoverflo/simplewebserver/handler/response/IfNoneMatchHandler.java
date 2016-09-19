package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class IfNoneMatchHandler extends AMatchHandler {

    public static final String HEADERNAME_IF_NONE_MATCH = "If-None-Match";

    public IfNoneMatchHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    String getHeaderName() {
        return HEADERNAME_IF_NONE_MATCH;
    }

    @Override
    int getErrorCode() {
        return HttpStatus.SC_NOT_MODIFIED;
    }

    @Override
    boolean shouldForward(boolean hasTagsMatching) {
        return !hasTagsMatching;
    }
}