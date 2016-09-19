package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.HttpStatus;

public class IfMatchHandler extends AMatchHandler {

    public static final String HEADERNAME_IF_MATCH = "If-Match";

    public IfMatchHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    String getHeaderName() {
        return HEADERNAME_IF_MATCH;
    }

    @Override
    int getErrorCode() {
        return HttpStatus.SC_PRECONDITION_FAILED;
    }

    @Override
    boolean shouldForward(boolean hasTagsMatching) {
        return hasTagsMatching;
    }
}