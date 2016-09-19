package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.util.HashUtil;
import org.apache.http.*;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class AMatchHandler extends AResponseHandler {

    private static Logger logger = LogManager.getLogger(AMatchHandler.class);

    private static final String ETAG_WILDCARD   = "*";
    private static final String PREFIX_WEAK_TAG = "W/";

    AMatchHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    /**
     * Returns this matcher's header name, e.g. If-Non-Matching.
     *
     * @return Header name according to RFC 7232
     */
    abstract String getHeaderName();

    /**
     * HTTP status code which is returned when the condition is not fulfilled.
     *
     * @return HTTP status code as in RFC 2616
     */
    abstract int getErrorCode();

    /**
     * Returns if the response should be handled by the next handler with
     * regards to the existence or non-existence of matchings.
     *
     * @param hasTagsMatching Determines if at least one entity tag matched
     * @return true if the next handler shall be invoked, false if an error code shall be sent
     */
    abstract boolean shouldForward(boolean hasTagsMatching);

    @Override
    boolean isApplicable(HttpRequest request, HttpContext context) {
        return request.containsHeader(getHeaderName());
    }

    @Override
    protected void performHandling(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        File file = getResource(context);
        boolean hasTagsMatching = false;

        try {
            String fileContentHash = HashUtil.calculateMD5Hash(file);

            List<String> entityTags = readEntityTags(request);
            for (String entityTag : entityTags) {
                if (fileContentHash.equals(getEntityTagToken(entityTag)) || (isWildcard(entityTag) && file.exists())) {
                    hasTagsMatching = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (shouldForward(hasTagsMatching)) {
            responseHandler.handle(request, response, context);
        } else {
            response.setStatusCode(getErrorCode());
        }
    }


    private List<String> readEntityTags(HttpRequest request) {
        List<String> entityTags = new ArrayList<>();

        Header[] headers = request.getHeaders(getHeaderName());
        for (Header header : headers) {
            HeaderElement[] elements = header.getElements();
            for (HeaderElement headerElement : elements) {
                // Simplification of parsing because the server only produces strong entity tags.
                String entityTag = headerElement.getName().replace("\"", "");
                logger.debug("Found ETag " + entityTag);
                entityTags.add(entityTag);
            }
        }

        return entityTags;
    }


    private boolean isWildcard(String etag) {
        return ETAG_WILDCARD.equals(etag);
    }


    private boolean isWeakTag(String etag) {
        return etag.startsWith(PREFIX_WEAK_TAG);
    }

    private String getEntityTagToken(String etag) {
        if (isWeakTag(etag)) {
            return etag.substring(PREFIX_WEAK_TAG.length());
        } else {
            return etag;
        }
    }
}
