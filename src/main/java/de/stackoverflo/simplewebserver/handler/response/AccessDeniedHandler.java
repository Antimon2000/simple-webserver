package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class AccessDeniedHandler extends AResponseHandler {

    private static Logger logger = LogManager.getLogger(AccessDeniedHandler.class);

    public AccessDeniedHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    boolean isApplicable(HttpRequest request, HttpContext context) {
        return !getResource(context).canRead();
    }

    @Override
    public void performHandling(
            HttpRequest request,
            HttpResponse response,
            HttpContext context) throws HttpException, IOException {

        File file = getResource(context);

        response.setStatusCode(HttpStatus.SC_FORBIDDEN);
        StringEntity entity = new StringEntity(
                "<html><body><h1>Access denied</h1></body></html>",
                ContentType.create("text/html", "UTF-8"));
        response.setEntity(entity);

        logger.debug("Cannot read file " + file.getPath());
    }
}
