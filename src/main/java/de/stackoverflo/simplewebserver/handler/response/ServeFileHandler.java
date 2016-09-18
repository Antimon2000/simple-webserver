package de.stackoverflo.simplewebserver.handler.response;

import de.stackoverflo.simplewebserver.util.DateUtil;
import de.stackoverflo.simplewebserver.util.HashUtil;
import org.apache.http.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class ServeFileHandler implements ResponseHandler {

    private static Logger logger = LogManager.getLogger(ServeFileHandler.class);

    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";

    private File file;

    public ServeFileHandler(File file) {
        this.file = file;
    }

    @Override
    public void handle(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        // Set headers
        response.setHeader(HEADER_LAST_MODIFIED, DateUtil.getHttpDateFromTimestamp(file.lastModified()));

        try {
            String etag = HashUtil.calculateMD5Hash(file);
            String quote = "\"";
            response.addHeader(HEADER_ETAG, quote + etag + quote);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        // Read file and set body
        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        HttpConnection conn = coreContext.getConnection(HttpConnection.class);
        response.setStatusCode(HttpStatus.SC_OK);

        ContentType contentType
                = file.getName().endsWith(".html")
                ? ContentType.create("text/html", (Charset) null)
                : ContentType.create("text/plain", (Charset) null);

        FileEntity body = new FileEntity(file, contentType);
        response.setEntity(body);

        logger.info(conn + ": serving file /" + file.getName());
    }
}