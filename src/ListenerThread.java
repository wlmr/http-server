import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Listens for new connections. If a new connection is established 
 * then the class spawns a new WorkerThread from the ThreadPool.
 */
public class ListenerThread implements Runnable {
    private String root;
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public ListenerThread(int port, String root) throws IOException{
        this.root = root;
        this.serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void run() {
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                executorService.execute(new WorkerThread(socket, root));
            }
        } catch (IOException e) {
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
