import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Servertest{



    public static void main(String[] args) throws IOException {
        Map<String, Utilizador> clients = new HashMap<>();
        Map<String, ServerTypeManager> server_type_manager = new HashMap<>();

        // Henrique:
        //ServerSocket ss = new ServerSocket(1025);
        ServerSocket ss = new ServerSocket(999);
        Socket cs;

        // Criação dos servidores do tipo "fast"
		Servidor s1 = new Servidor("fast.com", "fast", 10.0);
		Servidor s2 = new Servidor("fast.net", "fast", 10.0);
		Servidor s3 = new Servidor("fast.org", "fast", 10.0);

		Map<String, Servidor> m1 = new HashMap<>();
		m1.put(s1.getID(), s1);
		m1.put(s2.getID(), s2);
		m1.put(s3.getID(), s3);

		ServerTypeManager stm1 = new ServerTypeManager(m1, "fast", 3);
		server_type_manager.put(stm1.getType(), stm1);


		// Criação dos servidores do tipo "medium"
		Servidor s4 = new Servidor("medium.com", "medium", 7.50);
		Servidor s5 = new Servidor("medium.net", "medium", 7.50);
		Servidor s6 = new Servidor("medium.org", "medium", 7.50);

		Map<String, Servidor> m2 = new HashMap<>();
		m2.put(s4.getID(), s4);
		m2.put(s5.getID(), s5);
		m2.put(s6.getID(), s6);

		ServerTypeManager stm2 = new ServerTypeManager(m2, "medium", 3);
		server_type_manager.put(stm2.getType(), stm2);

		int i = 0;

        while(true){
            cs = ss.accept();

            Thread t = new Thread(new MyClientHandler(cs,clients,server_type_manager));
            t.start();
			i++;
        }

    }
}
