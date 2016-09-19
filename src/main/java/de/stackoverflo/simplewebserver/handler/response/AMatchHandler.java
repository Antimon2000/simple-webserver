package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.handler.http.HttpFileHandler;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AMatchHandler implements ResponseHandler {

    private static Logger logger = LogManager.getLogger(AMatchHandler.class);

    private static final String ETAG_WILDCARD = "*";
    private static final String PREFIX_WEAK_TAG = "W/";

    ResponseHandler responseHandler;

    AMatchHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }


    File getTargetFile(HttpContext context) {
        File file = (File) context.getAttribute(HttpFileHandler.KEY_ATTR_FILE);
        return file;
    }


    List<String> readEntityTags(HttpRequest request) {
        List<String> etags = new ArrayList<>();

        Header[] headers = request.getHeaders(getHeaderName());
        for (Header header : headers) {
            HeaderElement[] elements = header.getElements();
            for (HeaderElement headerElement : elements) {
                // Simplified parsing because server only sends strong tags anyway
                String tag = headerElement.getName().replace("\"", "");
                logger.debug("Found ETag " + tag);
                etags.add(tag);
            }
        }

        return etags;
    }


    boolean isWildcard(String etag) {
        return ETAG_WILDCARD.equals(etag);
    }

    boolean isWeakTag(String etag) {
        return etag.startsWith(PREFIX_WEAK_TAG);
    }

    String getEntityTagToken(String etag) {
        if (isWeakTag(etag)) {
            return etag.substring(PREFIX_WEAK_TAG.length());
        } else {
            return etag;
        }
    }

    /**
     * Returns the name of the header to be used, e.g. If-Non-Matching.
     *
     * @return Header name according to RFC 2616
     */
    abstract String getHeaderName();
}
