package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.util.HashUtil;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IfNoneMatchHandler extends AMatchHandler {

    private static Logger logger = LogManager.getLogger(IfNoneMatchHandler.class);

    public IfNoneMatchHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    String getHeaderName() {
        return "If-None-Match";
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        boolean doHandle = true;
        File file;
        String fileContentHash;

        if (request.containsHeader(getHeaderName())) {
            // As If-None-Match is present make sure If-Modified-Since will be ignored
            request.removeHeaders(IfModifiedSinceHandler.IF_MODIFIED_SINCE);

            doHandle = false;
            file = getTargetFile(context);
            try {
                fileContentHash = HashUtil.calculateMD5Hash(file);

                List<String> entityTags = readEntityTags(request);
                for (String entityTag : entityTags) {
                    if (!fileContentHash.equals(entityTag)) {
                        doHandle = true;
                        break;

                    } else if (isWildcard(entityTag)) {
                        if (file.exists()) {
                            doHandle = true;
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        if (doHandle) {
            responseHandler.handle(request, response, context);
        } else {
            // Legit because method can only be GET or HEAD
            response.setStatusCode(HttpStatus.SC_NOT_MODIFIED);
        }
    }
}