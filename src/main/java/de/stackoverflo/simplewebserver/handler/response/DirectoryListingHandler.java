package de.stackoverflo.simplewebserver.handler.response;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryListingHandler extends AResponseHandler {

    public DirectoryListingHandler(ResponseHandler responseHandler) {
        super(responseHandler);
    }

    @Override
    boolean isApplicable(HttpRequest request, HttpContext context) {
        return getResource(context).isDirectory();
    }

    @Override
    protected void performHandling(
            final HttpRequest request,
            final HttpResponse response,
            final HttpContext context) throws HttpException, IOException {

        List<File> directories = new ArrayList<>();
        List<File> files = new ArrayList<>();

        File file = getResource(context);
        for (File currentFile : file.listFiles()) {
            if (currentFile.isDirectory()) {
                directories.add(currentFile);
            } else {
                files.add(currentFile);
            }
        }

        response.setStatusCode(HttpStatus.SC_OK);

        StringBuilder builder = new StringBuilder();
        builder
                .append("<html><body><h1>Index of /").append(file.getName()).append("</h1>")
                .append("<pre><ul style=\"list-style: none\">");

        for (File f : directories) {
            builder.append("<li>D ").append(f.getName()).append("/</li>");
        }

        for (File f : files) {
            builder.append("<li>F ").append(f.getName()).append("</li>");
        }

        builder
                .append("</ul></pre>")
                .append("</body></html>");

        StringEntity entity = new StringEntity(
                builder.toString(),
                ContentType.create("text/html", "UTF-8"));

        response.setEntity(entity);
    }

}
