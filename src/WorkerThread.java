import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Receives and crudely parses the HTTP GET request, finds the appropriate file
 * and copies it to the socket output stream with the help of a file input
 * stream.
 */
public class WorkerThread implements Runnable {

    private Socket socket;
    private String root;

    public WorkerThread(Socket socket, String root) {
        this.socket = socket;
        this.root = root;
    }

    private String getFilename(BufferedReader br) throws IOException {
        return br.readLine().split(" ")[1];
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        final String CRLF = "\r\n";
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String filename = getFilename(bufferedReader);
            Path path = FileSystems.getDefault().getPath(root, filename);
            File file = new File(path.toString());
            if (file.exists()){
                fileInputStream = new FileInputStream(file);
                System.out.println(file.length());
                String responseStart = "HTTP/1.1 200 OK" + CRLF + "Content-Length: " + file.length() + CRLF + CRLF;
                outputStream.write(responseStart.getBytes());
                copy(fileInputStream, outputStream);
                String responseEnd = CRLF + CRLF;
                outputStream.write(responseEnd.getBytes());
            } else {
                String errorMsg = "No such file...";
                System.out.println("Cannot find file " + path.toString() + "...");
                String response = "HTTP/1.1 200 OK" + CRLF + "Content-Length: " + errorMsg.getBytes().length
                        + CRLF + CRLF + errorMsg + CRLF + CRLF;
                outputStream.write(response.getBytes());
            }
        } catch (IOException e) {
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
