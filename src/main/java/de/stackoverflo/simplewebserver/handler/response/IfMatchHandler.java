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

public class IfMatchHandler extends AMatchHandler {

    private static Logger logger = LogManager.getLogger(IfMatchHandler.class);

    public IfMatchHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    String getHeaderName() {
        return "If-Match";
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        boolean doHandle = true;
        File file;
        String fileContentHash;

        if (request.containsHeader(getHeaderName())) {
            doHandle = false;
            file = getTargetFile(context);
            try {
                fileContentHash = HashUtil.calculateMD5Hash(file);

                List<String> entityTags = readEntityTags(request);
                for (String entityTag : entityTags) {
                    if (fileContentHash.equals(entityTag)) {
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
            response.setStatusCode(HttpStatus.SC_PRECONDITION_FAILED);
        }
    }
}