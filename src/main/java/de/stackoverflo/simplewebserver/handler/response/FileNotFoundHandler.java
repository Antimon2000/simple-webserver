package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class FileNotFoundHandler extends AResponseHandler {

    private static Logger logger = LogManager.getLogger(FileNotFoundHandler.class);

    public FileNotFoundHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    boolean isApplicable(HttpRequest request, HttpContext context) {
        boolean hasIfMatchWildcard = false;

        Header header = request.getFirstHeader(IfMatchHandler.HEADERNAME_IF_MATCH);
        if (header != null) {
            HeaderElement[] headerElements = header.getElements();
            for (HeaderElement headerElement : headerElements) {
                if (AMatchHandler.isWildcard(headerElement.getName())) {
                    hasIfMatchWildcard = true;
                    break;
                }
            }
        }

        return !getResource(context).exists() && !hasIfMatchWildcard;
    }

    @Override
    protected void performHandling(
            HttpRequest request,
            HttpResponse response,
            HttpContext context) throws HttpException, IOException {

        File file = getResource(context);

        response.setStatusCode(HttpStatus.SC_NOT_FOUND);
        StringEntity entity = new StringEntity(
                "<html><body><h1>File " + file.getPath() + " not found</h1></body></html>",
                ContentType.create("text/html", "UTF-8"));
        response.setEntity(entity);

        logger.debug("File " + file.getPath() + " not found");
    }
}
