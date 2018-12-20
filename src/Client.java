import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


class Reader implements Runnable{
	Socket cs;
	BufferedReader in;

	public Reader(Socket cs){
		this.cs = cs;
	}

	public void run(){

		String msg;

		try{
			this.in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			while((msg = this.in.readLine()) != null){
				System.out.println(msg);
			}
		}
		catch(IOException e){}
	}
}

class Writer implements Runnable{
	Socket cs;
	PrintWriter out;

	public Writer(Socket cs){
		this.cs = cs;
	}

	public void run(){

		try{
			this.out = new PrintWriter(cs.getOutputStream(), true);
			BufferedReader sys_in = new BufferedReader(new InputStreamReader(System.in));
			String msg;

			while((msg = sys_in.readLine()) != null){
				out.println(msg);
			}
			sys_in.close();
		}
		catch (IOException e){
		}

		out.close();

	}
}


public class Client {
    public static void main(String args[]) throws IOException {

        Socket cs = new Socket("127.0.0.1", 999);


        int active = 1;

		Thread t = new Thread(new Reader(cs));
		Thread t1 = new Thread(new Writer(cs));
		t.start();
		t1.start();

        while(true){
        }

        //cs.close();
    }
}
