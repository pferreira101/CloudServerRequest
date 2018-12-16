import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server{

    public static void main(String[] args) throws IOException {
        Map<String, String> clients = new HashMap<>();
        ServerSocket ss = new ServerSocket(999);
        Socket cs;

        while(true){
            cs = ss.accept();

            Thread t = new Thread(new ClientHandler(cs, clients));
            t.start();
        }

    }
}
