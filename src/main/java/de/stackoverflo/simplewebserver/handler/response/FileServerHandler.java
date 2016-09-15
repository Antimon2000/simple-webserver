package de.stackoverflo.simplewebserver.handler.response;

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

        HttpCoreContext coreContext = HttpCoreContext.adapt(context);
        HttpConnection conn = coreContext.getConnection(HttpConnection.class);
        response.setStatusCode(HttpStatus.SC_OK);
        FileEntity body = new FileEntity(file, ContentType.create("text/html", (Charset) null));
        response.setEntity(body);

        logger.trace(conn + ": serving file " + file.getPath());
    }
}
