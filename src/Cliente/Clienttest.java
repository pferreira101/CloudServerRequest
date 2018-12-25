import java.util.*;
import java.io.*;
import java.net.Socket;


class Reader implements Runnable{
	Socket cs;
	BufferedReader in;
	Logs l;

	public Reader(Socket cs,Logs l){
		this.cs = cs;
		this.l = l;
	}

	public boolean warningCheck(String msg){
		String args [] = msg.split(":");

		boolean valid = args[0].equals("WARNING") ? true : false;

		return valid;
	}

	public void run(){

		String msg;

		try{
			this.in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			while((msg = this.in.readLine()) != null){
				System.out.println(msg);
				if (msg.equals("GRANTED"))
					l.login();
				if ((!this.warningCheck(msg)) && l.getWaiting()){
					l.offwaiting();
				}
			}
		}
		catch(IOException e){}
	}
}

public class Clienttest {
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
