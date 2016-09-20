package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.util.DateUtil;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class IfModifiedSinceHandler extends AResponseHandler {

    private static Logger logger = LogManager.getLogger(IfModifiedSinceHandler.class);

    public static final String HEADERNAME_IF_MODIFIED_SINCE = "If-Modified-Since";

    public IfModifiedSinceHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    boolean isApplicable(HttpRequest request, HttpContext context) {
        return request.containsHeader(HEADERNAME_IF_MODIFIED_SINCE)
                && !request.containsHeader(IfNoneMatchHandler.HEADERNAME_IF_NONE_MATCH);
    }

    @Override
    protected void performHandling(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header header = request.getLastHeader(HEADERNAME_IF_MODIFIED_SINCE);
        Date ifModifiedSinceDate;

        boolean isDateParseable = true;
        boolean hasNewerVersion = false;

        try {
            File file = getResource(context);
            ifModifiedSinceDate = DateUtil.parseFromHttpDate(header.getValue());

            if (ifModifiedSinceDate.before(new Date(file.lastModified()))) {
                hasNewerVersion = true;
            }
        } catch (java.text.ParseException e) {
            isDateParseable = false;
            logger.warn(e.getMessage());
        }

        if (hasNewerVersion || !isDateParseable) {
            responseHandler.handle(request, response, context);
        } else {
            logger.debug("File was not modified");
            response.setStatusCode(HttpStatus.SC_NOT_MODIFIED);
        }
    }
}
