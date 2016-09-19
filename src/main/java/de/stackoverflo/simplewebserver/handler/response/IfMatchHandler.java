package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.HttpStatus;

public class IfMatchHandler extends AMatchHandler {

    public IfMatchHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    String getHeaderName() {
        return "If-Match";
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