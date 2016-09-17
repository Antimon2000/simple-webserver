package de.stackoverflo.simplewebserver.handler.response;

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

public class FileServerHandler implements ResponseHandler {

    private static Logger logger = LogManager.getLogger(FileServerHandler.class);

    private File file;

    public FileServerHandler(File file) {
        this.file = file;
    }

    @Override
    public void handle(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        try {
            String etag = HashUtil.calculateMD5Hash(file);
            String quote = "\"";
            response.addHeader("etag", quote + etag + quote);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

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
