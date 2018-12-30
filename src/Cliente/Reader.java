package Cliente;

import java.util.*;
import java.io.*;
import java.net.Socket;

public class Reader implements Runnable{
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
