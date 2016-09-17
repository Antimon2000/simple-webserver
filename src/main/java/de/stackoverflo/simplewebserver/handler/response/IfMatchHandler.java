package de.stackoverflo.simplewebserver.handler.response;

import java.io.File;

public class IfMatchHandler extends AMatchHandler {

    public IfMatchHandler(File file, ResponseHandler responseHandler) {
        super(file, responseHandler);
    }

    @Override
    boolean getTriggerHook() {
        return true;
    }

    @Override
    String getHeaderName() {
        return "If-Match";
    }
}