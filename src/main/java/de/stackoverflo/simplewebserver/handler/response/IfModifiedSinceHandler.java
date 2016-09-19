package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import de.stackoverflo.simplewebserver.util.DateUtil;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class IfModifiedSinceHandler implements ResponseHandler {

    private static Logger logger = LogManager.getLogger(IfModifiedSinceHandler.class);

    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    private ResponseHandler responseHandler;

    public IfModifiedSinceHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header header = request.getLastHeader(IF_MODIFIED_SINCE);
        Date ifModifiedSinceDate;

        boolean dateParseable = true;
        boolean wasModified = false;

        if (header != null) {
            try {
                File file = (File) context.getAttribute(HttpFileHandler.KEY_ATTR_FILE);
                ifModifiedSinceDate = DateUtil.parseFromHttpDate(header.getValue());

                if (ifModifiedSinceDate.before(new Date(file.lastModified()))) {
                    wasModified = true;
                }
            } catch (java.text.ParseException e) {
                dateParseable = false;
                logger.warn(e.getMessage());
            }
        }

        if (!request.containsHeader(IF_MODIFIED_SINCE) || wasModified || !dateParseable) {
            responseHandler.handle(request, response, context);
        } else {
            logger.debug("File was not modified");
            response.setStatusCode(HttpStatus.SC_NOT_MODIFIED);
        }
    }
}
