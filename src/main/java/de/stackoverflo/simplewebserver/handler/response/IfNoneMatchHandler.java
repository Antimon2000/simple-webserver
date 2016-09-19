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
        boolean hasMatch = false;
        File file;
        String fileContentHash;

        if (request.containsHeader(getHeaderName())) {
            // As If-None-Match is present, If-Modified-Since must be ignored (see RFC 7232)
            request.removeHeaders(IfModifiedSinceHandler.IF_MODIFIED_SINCE);

            file = getTargetFile(context);
            try {
                fileContentHash = HashUtil.calculateMD5Hash(file);

                List<String> entityTags = readEntityTags(request);
                for (String entityTag : entityTags) {
                    if (fileContentHash.equals(entityTag) || isWildcard(entityTag)) {
                        hasMatch = true;
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        if (!request.containsHeader(getHeaderName()) || !hasMatch) {
            responseHandler.handle(request, response, context);
        } else {
            // Legit because method can only be GET or HEAD
            response.setStatusCode(HttpStatus.SC_NOT_MODIFIED);
        }
    }
}