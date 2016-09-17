package de.stackoverflo.simplewebserver.handler.response;

import java.io.File;

public class IfNonMatchHandler extends AMatchHandler {

    public IfNonMatchHandler(File file, ResponseHandler responseHandler) {
        super(file, responseHandler);
    }

    @Override
    boolean getTriggerHook() {
        return false;
    }

    @Override
    String getHeaderName() {
        return "If-Non-Match";
    }
}