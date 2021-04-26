import java.io.IOException;

public class HttpServer {


    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ");
            System.out.println("java HttpServer PORT ROOTDIR");
            return;
        }
        int port = Integer.parseInt(args[0]);
        String root = args[1];
        try {
            ListenerThread listener = new ListenerThread(port, root);
            listener.run();
        } catch (IOException e) {
        } catch (NumberFormatException e) {
            System.out.println("");
        }
    }
}
