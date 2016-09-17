package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.util.HashUtil;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public abstract class AMatchHandler implements ResponseHandler {

    private static Logger logger = LogManager.getLogger(AMatchHandler.class);

    public static final String ETAG_WILDCARD = "*";

    private ResponseHandler responseHandler;
    private File file;

    public AMatchHandler(File file, ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        this.file = file;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        boolean doHandle = true;

        try {
            String fileContentHash = HashUtil.calculateMD5Hash(file);

            Header[] headers = request.getHeaders(getHeaderName());
            for (Header header : headers) {
                doHandle = false;

                if (ETAG_WILDCARD.equals(header.getValue()) || fileContentHash.equals(header.getValue()) == getTriggerHook()) {
                    doHandle = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (doHandle) {
            responseHandler.handle(request, response, context);
        } else {
            response.setStatusCode(HttpStatus.SC_PRECONDITION_FAILED);
        }
    }

    abstract boolean getTriggerHook();

    /**
     * Returns the name of the header to be used, e.g. If-Non-Matching.
     *
     * @return Header name according to RFC 2616
     */
    abstract String getHeaderName();
}
