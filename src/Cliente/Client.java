import java.io.*;
import java.net.Socket;


public class Client {
    public static void main(String args[]) throws IOException {

        //Henrique:
        //Socket cs = new Socket("127.0.0.1", 1025);
        Socket cs = new Socket("127.0.0.1", 999);

		Logs l = new Logs();
		Thread t = new Thread(new Drawer(cs,l));
		Thread t1 = new Thread(new Reader(cs,l));
		t.start();
		t1.start();

        //while(true){
        //}

        //cs.close();
    }
}
