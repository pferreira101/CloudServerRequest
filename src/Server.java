import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server{

    public static void main(String[] args) throws IOException {
        Map<String, Utilizador> clients = new HashMap<>();
        ServerSocket ss = new ServerSocket(999);
        Socket cs;

		Servidor s1 = new Servidor("a","1");
		Servidor s2 = new Servidor("b","1");
		Servidor s3 = new Servidor("c","1");
		Map<String,Servidor> server = new HashMap<>();
		server.put(s1.getId(),s1);
		server.put(s2.getId(),s2);
		server.put(s3.getId(),s3);

		ServerManagement servers1 = new ServerManagement(server,3);

        while(true){
            cs = ss.accept();

            Thread t = new Thread(new ClientHandler(cs, clients,servers1));
            t.start();
        }

    }
}
