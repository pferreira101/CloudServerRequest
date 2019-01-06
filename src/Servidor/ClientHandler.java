package Servidor;

import java.io.*;
import java.net.Socket;
import java.util.Map;

/**
 * Classe responsável por intrepertar os pedidos feitos por um cliente ao servidor.
 */

public class ClientHandler implements Runnable{
    private Socket cs;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, Utilizador> clients;
	private Map<String, ServerTypeManager> stm;
    private String active_user; // Coloquei isto porque quem trata do utilizador tem que saber qual ele é e isso só acontece apos o login


    public ClientHandler(Socket cs, Map<String, Utilizador> clients, Map<String, ServerTypeManager> stm) {
        this.cs = cs;
        this.clients = clients;
		this.stm = stm;
    }

    public void run(){
        String msg;

        try{
            out = new PrintWriter(cs.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));

			while((msg = in.readLine()) != null){
				System.out.println(msg);
				command(msg);
			}


        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

	/**
	 * Método que recebe uma mensagem do cliente e reencaminha para o método correto.
	 * @param msg Pedido recebido.
	 * @return Valor booleano se a ligação ao servidor tem que ser cortada.
	 */

	private boolean command(String msg){
		String [] args = msg.split(";",2);

		switch(args[0]){
			case "LOGIN": {commandLogin(msg); break;}
			case "SIGN": {commandSign(msg); break;}
			case "LOGOUT": {commandLogout(); break;}
			case "MONEY": {commandMoney(); break;}
			case "OSERVER": {commandOServer(); break;}
			case "FREE": {commandFServer(msg); break;}
			case "RENT": {commandRServer(msg); break;}
			case "BID" : {commandBServer(msg); break;}
			default: {this.out.println("Erro"); break;}
			//falta o disconnect total e tratar das excpetions melhor
		}

		boolean result = args[0].equals("EXIT");

		return result;
	}

	/**
	 * Método responsável por comprar um servidor por leilão.
	 * @param msg Pedido recebido.
	 */

	private void commandBServer(String msg){
		String args[] = msg.split(";");

		Utilizador user = this.getUser(this.active_user);
		ServerTypeManager s = this.stm.get(args[1]);
		String server_id = s.licitar(Double.parseDouble(args[2]),user,this.out);

		out.println("SERVER AQUIRED: " + server_id);

	}

	/**
	 * Método responsável por libertar um servidor.
	 * @param msg Pedido recebido
	 */


	private void commandFServer(String msg){
		String[] args = msg.split(";");
		libertarServidor(args[1]);
	}

	/**
	 * Método responsável por obter a lista de servidores que o utilizador obtem.
	 */

	private void commandOServer(){
		if (!this.active_user.equals("")){
			Utilizador u = this.getUser(this.active_user);
			out.println("OWNED SERVERS: " + u.getOwnedServers());
		}
		else{
			out.println("DENIED");
		}
	}

	/**
	 * Método de log out de um utilizador do servidor.
	 */

	private void commandLogout(){
		out.println("USER: " + this.active_user + " -> DISCONNECTED");
		this.active_user = "";
	}

	/**
	 * Método reponsável por fazer login de um utilizador.
	 * @param msg Pedido ao servidor.
	 */

	private void commandLogin(String msg){
		String[] args = msg.split(";");

		int i = logIn(args[1],args[2]);
		switch (i){
			case 1: {out.println("GRANTED"); this.active_user = args[1];break;}
			default: {out.println("DENIED"); break;}
		}
	}

	/**
	 * Método reponsável por registar um utilizador.
	 * @param msg Pedido ao servidor.
	 */

	private void commandSign(String msg){
		String[] args = msg.split(";");

		int i = registerClient(args[1],args[2]);
		switch (i){
			case 1: {out.println("USER REGISTED"); break;}
			default: {out.println("USER ALREADY REGISTED"); break;}
		}
	}

	/**
	 * Método reponsável por verificar a dívida de um utilizador.
	 */

	private void commandMoney(){
		double money;
		if (this.active_user.equals("")){
			out.println("DENIED");
		}
		else{
			money = this.getDividaUser();
			out.println("USER: " + this.active_user + " -> " + money);
		}
	}

	/**
	 * Método que verifica se as credenciais inseridas são válidades.
	 * @param user String com o nome do utilizador.
	 * @param pw String com a password do utilizador.
	 * @return Permições obtidas.
	 */


    private int logIn(String user, String pw){
		Utilizador util;
        synchronized (clients){
            if(this.clients.containsKey(user) && this.clients.get(user).authenticate(pw)){
                this.active_user = user;
				return 1;
			}
        }
        return 0;
    }

	/**
	 * Método para registar um utilizador.
	 * @param user String com o nome do utilizador.
	 * @param pw String com a password do utilizador.
	 * @return
	 */

	private int registerClient(String user, String pw) {
        synchronized (clients){
            if(!this.clients.containsKey(user)){
                this.clients.put(user, new Utilizador(user, pw));
                return 1;
            }
        }
        return 0;
    }

	/**
	 * Método que retorna a dívida do utilizador
	 * @return dívida
	 */
	private double getDividaUser(){
		Utilizador u;
		double value = -1;

		synchronized (this.clients) {
			u = clients.get(this.active_user);
		}

		if (u != null){
			value = u.getDivida();
		}

		return value;
	}

	/**
	 * Método utilizado para obter o utilizador pretendido.
	 * @param str String com o nome do utilizador.
	 * @return Utilizador pretendido.
	 */

	private Utilizador getUser(String str){
		Utilizador user;
		synchronized (this.clients){
			user = clients.get(str);
		}
		return user;
	}

	/**
	 * Método responsável por intrepertar o comando para reservar um servidor.
	 * @param msg Pedido ao servidor.
	 */

	private void commandRServer(String msg){
		String args[] = msg.split(";");

		adquirirServidor(Double.parseDouble(args[2]),args[1]);
	}

	/**
	 * Método para adquirir um servidor.
	 * @param price Preço da aquisição.
	 * @param type Tipo de servidor.
	 */

	private void adquirirServidor(double price, String type){
		Utilizador user = this.getUser(this.active_user);
		ServerTypeManager s = this.stm.get(type);
		String server_id = s.adquirir(price,user);

		out.println("SERVER ACQUIRED: " + server_id);
	}

	/**
	 * Método para libertar um servidor
	 * @param server_id String com o identificador do servidor.
	 */

	private void libertarServidor(String server_id){
		Utilizador user = this.getUser(this.active_user);

        if (user.donoServidor(server_id)){
            double price = getServerSMT(server_id).libertar(server_id);

            user.removeServidor(server_id);
            user.addDivida(price);

            out.println("SERVER PAYMENT -> " + price);
        }
        else {
            out.println("SERVER NOT OWNED -> " + server_id);
        }
    }




    /**
     * Função que retorna o objeto da classe ServerTypeManager correspondente ao id de um servidor.
     * @param server_id id do servidor
     * @return ServerTypeManager correspondente
     */

    public ServerTypeManager getServerSMT(String server_id){
	    String server_type = (server_id.split("\\."))[0];

	    return this.stm.get(server_type);
    }
}
