package Cliente;

import java.util.concurrent.locks.*;


public class Logs {
	private boolean login;
	private boolean logout;
	private boolean waiting_response;

	public Logs(){
		this.login = false;
		this.logout = false;
		this.waiting_response = false;
	}

	public synchronized void login(){
			this.login = true;
	}

	public synchronized void loginoff(){
			this.login = false;
	}

	public void logout(){
			this.logout = true;
	}

	public synchronized  boolean getLogIn_status(){
		return this.login;
	}

	public boolean getLogOut_status(){
		return this.logout;
	}

	public synchronized boolean getWaiting(){
		return this.waiting_response;
	}

	public synchronized  void offwaiting(){
			this.waiting_response = false;
			notifyAll();
	}

	public synchronized  void waitresponse() throws InterruptedException{
			this.waiting_response = true;
			while(waiting_response)
				wait();
	}
}
