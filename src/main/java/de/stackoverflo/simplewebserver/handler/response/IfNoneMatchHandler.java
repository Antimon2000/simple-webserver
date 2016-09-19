package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.HttpStatus;

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