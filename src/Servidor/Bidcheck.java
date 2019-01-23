

import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;
import java.net.Socket;

/**
 * Classe que é responsável por avisar um utilizador quando um dos seus servidores obtidos por leilão é adquirido por outro utilizador.
 */

public class Bidcheck implements Runnable{
	private Servidor s;
	private PrintWriter out;
	private Utilizador u;

	public Bidcheck(Servidor s,PrintWriter out, Utilizador u){
		this.s = s;
		this.out = out;
		this.u = u;
	}

	public void run(){

		try{
			boolean valid = s.warningAfterBid();

			if (valid){
				String id = s.getID();
				if(u.getStatus() == 1)
					this.out.println("WARNING: " + id + " WAS SOLD TO OTHER USER");
				else u.addMsg("WARNING: " + id + " WAS SOLD TO OTHER USER");
			}

		}
		catch(InterruptedException a){a.getMessage();}
	}

}
