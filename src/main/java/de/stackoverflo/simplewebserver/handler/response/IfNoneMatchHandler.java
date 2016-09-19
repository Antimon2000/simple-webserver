package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.HttpStatus;

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
}