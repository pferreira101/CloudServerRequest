package Cliente;


import java.util.*;
import java.io.*;
import java.net.Socket;

public class Drawer implements Runnable{
	private int menu_status;
	private Socket cs;
	private Logs log;
	private PrintWriter out;

	public Drawer (Socket cs,Logs l){
		this.menu_status = 0;
		this.cs = cs;
		this.log = l;
	}

	/**
	 * Método usado para desenhar os menus.
	 */

	public void menu_draw(){
		switch (this.menu_status){
			case 0:{
				System.out.println("1 - Log In | 2 - Registar | 0 - Sair");
				break;
			}
			case 1:{
				System.out.println("1 - Reservar servidor | 2 - Libertar servidor | 3 - Consultar divida | 4 - Sair");
				break;
			}
			case 2:{
				System.out.println("Tipo: 1 - Fast | 2 - Medium | 3 - Large | 4 - Voltar");
				break;
			}
		}
	}

	/**
	 * Método para ler a decisão do cliente em cada menu.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void read_menu_output() throws IOException,InterruptedException{
		switch (this.menu_status){
			case 0:
				menu_one_output();
				break;
			case 1:
				menu_two_output();
				break;
			case 2:
				menu_three_output();
				break;
			default:
				break;
		}
	}

	/**
	 * Método para ler um inteiro digitado pelo utilizador.
	 * @return Valor inteiro introduzido pelo utilizador.
	 */

	public int readOpt(){
		int option = -1;
		boolean valid = false;
		String msg;
		Scanner is = new Scanner(System.in);

		while(!valid){
				try{
					msg = is.nextLine();
					option = Integer.parseInt(msg);
					valid = true;
				}
				catch (NumberFormatException e){
					System.out.println("Input inválido. Insira um dígito.\n");
				}
		}

		return option;
	}

	/**
	 * Método utilizado para ler um double introduzido pelo utilizador.
	 * @return Valor double introduzido pelo utilizador.
	 */

	public double readDouble(){
		double number = -1;
		boolean valid = false;
		Scanner is = new Scanner(System.in);
		String msg;

		while(!valid){
				try{
					msg = is.nextLine();
					number = Double.parseDouble(msg);
					if (number > 0)
						valid = true;
					else{
						System.out.println("Por favor insira valores positivos");
					}
				}
				catch (NumberFormatException e){
					System.out.println("Input inválido. Insira um dígito.\n");
				}
		}

		return number;
	}

	/**
	 * Método para interpretar a decisão do cliente no menu 3.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void menu_three_output() throws IOException,InterruptedException{
		int option = this.readOpt();

		switch(option){
			case 1: {this.server_rent(1);break;}
			case 2: {this.server_rent(2);break;}
			case 3: {this.server_rent(3);break;}
			case 4:{this.menu_status--; break;}
			default:{
				System.out.println("Por favor insira um número das opeções dadas");
				menu_three_output();
			}
		}
	}

	/**
	 * Método reponsável pelo pedido ao servidor de uma compra ou licitação de um servidor.
	 * @param option Tipo do servidor pretendido.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void server_rent(int option) throws IOException,InterruptedException{
		String server_type;
		double server_price;

		if(option == 1){server_type = "fast"; server_price = 10.0;}
        else if (option == 2){server_type = "medium"; server_price = 7.5;}
        else{server_type = "large"; server_price = 5;}

		System.out.println("Deseja reservar: 1 - Preço base (" + server_price + ") | 2 - Preço para leilão");

		int op = -1;

		while(op == -1) {
			op = readOpt();
			if (!(op == 1 || op == 2))
				op = -1;
		}

		String result = "";
		if (op == 1){
			result = String.join(";","RENT",server_type,String.valueOf(server_price));

		}
		else if (op == 2){
			System.out.println("Preço para o servidor:");
			server_price = readDouble();
			result = String.join(";","BID",server_type,String.valueOf(server_price));
			System.out.println(String.valueOf(server_price));
		}
		this.server_request(result);
		this.menu_status--;
	}

	/**
	 * Método para interpretar a decisão do cliente no menu 2.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void menu_two_output() throws IOException,InterruptedException{
		int option = this.readOpt();

		switch(option){
			case 1:{ this.menu_status++; break;}
			case 2:{ this.menu_two_freeserver(); break;}
			case 3:{ this.menu_two_money(); break;}
			case 4:{ server_request("LOGOUT");;break;}
			default:{
				System.out.println("Por favor insira um número das opeções dadas");
				menu_two_output();
			}
		}
	}

	/**
	 * Método que emite um pedido ao servidor e espera pela resposta.
	 * @param msg Pedido a ser enviador.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void server_request(String msg) throws IOException,InterruptedException{
		this.out.println(msg);
		this.log.waitresponse();
	}

	/**
	 * Método responsável por emitir o pedido para libertar um servidor no menu 2.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void menu_two_freeserver() throws IOException,InterruptedException{
		String result = "OSERVER",msg;
		this.server_request(result);

		Scanner is = new Scanner(System.in);
		msg = is.nextLine();

		result = String.join(";","FREE",msg);
		this.server_request(result);

	}

	/**
	 * Método responsável por emitir o pedido para consultar a dívida do cliente no menu 2.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void menu_two_money() throws IOException,InterruptedException{
		String result = "MONEY";
		this.server_request(result);
	}


	/**
	 * Método responsável por interpretar a decisão do cliente no menu 1.
	 * @throws IOException
	 * @throws InterruptedException
	 */


	public void menu_one_output() throws IOException,InterruptedException{
		int option = this.readOpt();

		switch(option){
			case 0:{
					server_request("LOGOUT");
				break;
			}
			case 1:
				menu_one_login();
				break;
			case 2:
				menu_one_signup();
				break;
			default:{
				System.out.println("Por favor insira um número das opeções dadas");
				menu_one_output();
				break;
			}
		}

	}

	/**
	 * Método para fazer log in no menu 1.
	 * @throws IOException
	 * @throws InterruptedException
	 */


	public void menu_one_login() throws IOException,InterruptedException{
		String username, password;
		Scanner is = new Scanner(System.in);

		System.out.println("Username:");
		username = is.nextLine();
		System.out.println("Password:");
		password = is.nextLine();

		String result = String.join(";","LOGIN",username,password);
		this.server_request(result);

		if(this.log.getLogIn_status()){
			this.menu_status++;
		}

	}

	/**
	 * Método para registar no menu 1.
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public void menu_one_signup() throws IOException,InterruptedException{
		String username, password;
		Scanner is = new Scanner(System.in);

		System.out.println("Username:");
		username = is.nextLine();
		System.out.println("Password:");
		password = is.nextLine();

		String result = String.join(";","SIGN",username,password);
		this.server_request(result);

	}

	/**
	 * Método run que é executado pela thread.
	 */

	public void run(){
		try{
			this.out = new PrintWriter(cs.getOutputStream(), true);

			while(!this.log.getLogOut_status()){
				menu_draw();
				read_menu_output();
			}
			this.cs.close();
		}
		catch(IOException e){}
		catch(InterruptedException a){}
	}
}
