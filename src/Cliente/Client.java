package Cliente;

import java.io.*;
import java.net.Socket;

/**
 * Classe responsável por ligar um cliente ao servidor.
 */

public class Client {
    public static void main(String args[]) throws IOException {

        Socket cs = new Socket("127.0.0.1", 999);

		Logs l = new Logs();
		Thread t = new Thread(new Drawer(cs,l));
		Thread t1 = new Thread(new Reader(cs,l));
		t.start();
		t1.start();

    }
}
