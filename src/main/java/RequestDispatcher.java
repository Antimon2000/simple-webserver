import java.io.*;
import java.net.Socket;

public class RequestDispatcher implements Runnable {

    private Socket socket;
    private boolean keepAlive;
    private String documentRoot;

    public RequestDispatcher(Socket socket, String documentRoot) {
        this.socket = socket;
        this.documentRoot = documentRoot;
    }

    public void run() {

        String desc = Thread.currentThread().getName() + " total: " + Thread.getAllStackTraces().keySet().size();
        try {
            InputStream in = socket.getInputStream();
            int dump = in.read();

            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.write("HTTP/1.0 200 OK\r\n");
            out.write("Connection: Close\r\n");
            out.write("\r\n");
            out.write("<html><body><h1>" +desc+ "</h1></body></html>\r\n");
            out.flush();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isKeptAlive() {
        return keepAlive;
    }
}
