package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import de.stackoverflo.simplewebserver.util.DateUtil;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class IfModifiedSinceHandler implements ResponseHandler {

    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    private ResponseHandler responseHandler;

    public IfModifiedSinceHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header header = request.getLastHeader(IF_MODIFIED_SINCE);
        Date lastModifiedDate;

        boolean wasModified = true;

        if (header != null) {
            try {
                File file = (File) context.getAttribute(HttpFileHandler.KEY_ATTR_FILE);
                lastModifiedDate = DateUtil.parseFromHttpDate(header.getValue());

                if (lastModifiedDate.before(new Date(file.lastModified()))) {
                    wasModified = true;
                }
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        if (wasModified) {
            responseHandler.handle(request, response, context);
        } else {
            response.setStatusCode(HttpStatus.SC_NOT_MODIFIED);
        }
    }
}
