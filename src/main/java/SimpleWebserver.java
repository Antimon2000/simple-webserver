import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleWebserver {

    private int threadsPerCore;
    private String documentRoot;
    private boolean isAcceptingNewRequests;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public SimpleWebserver(int port, String documentRoot, int threadsPerCore) {
        if (port <= 0 || threadsPerCore <= 0) {
            throw new IllegalArgumentException("numerals must be greater than zero");
        }

        this.documentRoot = documentRoot;
        this.threadsPerCore = threadsPerCore;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnections() {
        isAcceptingNewRequests = true;

        Socket socket;
        executor = Executors.newFixedThreadPool(getThreadPoolSize());

        while (isAcceptingNewRequests) {
            try {
                socket = serverSocket.accept();
                executor.execute(new RequestDispatcher(socket, documentRoot));
            } catch (IOException e) {
                if (isAcceptingNewRequests) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopServer() {
        isAcceptingNewRequests = false;
        executor.shutdown();
    }

    private int getThreadPoolSize() {
        return Runtime.getRuntime().availableProcessors() * threadsPerCore;
    }
}
