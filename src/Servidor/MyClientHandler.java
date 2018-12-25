import java.io.*;
import java.net.Socket;
import java.util.Map;

public class MyClientHandler implements Runnable{
    private Socket cs;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, Utilizador> clients;
	private Map<String, ServerTypeManager> stm;
    private String active_user; // Coloquei isto porque quem trata do utilizador tem que saber qual ele é e isso só acontece apos o login


    public MyClientHandler(Socket cs, Map<String, Utilizador> clients, Map<String, ServerTypeManager> stm) {
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

	private void command(String msg){
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
			default: {this.out.println("Errro"); break;}
		}
	}

	private void commandBServer(String msg){
		String args[] = msg.split(";");

		Utilizador user = this.getUser(this.active_user);
		ServerTypeManager s = this.stm.get(args[1]);
		String server_id = s.licitar(Double.parseDouble(args[2]),user,this.out);

		out.println("SERVER AQUIRED: " + server_id);

	}


	private void commandFServer(String msg){
		String[] args = msg.split(";");
		libertarServidor(args[1]);
	}

	private void commandOServer(){
		if (!this.active_user.equals("")){
			Utilizador u = this.getUser(this.active_user);
			out.println("OWNED SERVERS: " + u.getOwnedServers());
		}
		else{
			out.println("DENIED");
		}
	}

	private void commandLogout(){
		out.println("USER: " + this.active_user + " -> DISCONNECTED");
		this.active_user = "";
	}

	private void commandLogin(String msg){
		String[] args = msg.split(";");

		int i = logIn(args[1],args[2]);
		switch (i){
			case 1: {out.println("GRANTED"); this.active_user = args[1];break;}
			default: {out.println("DENIED"); break;}
		}
	}

	private void commandSign(String msg){
		String[] args = msg.split(";");

		int i = registerClient(args[1],args[2]);
		switch (i){
			case 1: {out.println("USER REGISTED"); break;}
			default: {out.println("USER ALREADY REGISTED"); break;}
		}
	}

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

/*
    void showOps(int menu){
        switch (menu){
            case 0:
                out.println("1 - Reservar servidor | 2 - Libertar servidor | 3 - Consultar divida | 4 - Sair");
                break;

            case 1:
                out.println("Tipo: 1 - Fast | 2 - Medium | 3 - Large");
                break;
            case 2:
                Utilizador u = getUser(this.active_user);
                out.println("Servidor a remover: " + u.getOwnedServers());
                break;
        }
    }
*/
	private Utilizador getUser(String str){
		Utilizador user;
		synchronized (this.clients){
			user = clients.get(str);
		}
		return user;
	}

	private void commandRServer(String msg){
		String args[] = msg.split(";");

		adquirirServidor(Double.parseDouble(args[2]),args[1]);
	}

	private void adquirirServidor(double price, String type){
		Utilizador user = this.getUser(this.active_user);
		ServerTypeManager s = this.stm.get(type);
		String server_id = s.adquirir(price,user);

		out.println("SERVER AQUIRED: " + server_id);
	}

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
     * Função que processa a reserva de um servidor
     * @param x opção escolhida pelo utilizador para o tipo de servidor
     * @throws IOException
     */
	 /*
	public void processaReserva(int x) throws IOException {
        String opt;
        int op;
        String server_type;
        double server_price;

        if(x == 1){server_type = "fast"; server_price = 10.0;}
        else if (x == 2){server_type = "medium"; server_price = 7.5;}
        else{server_type = "large"; server_price = 5;}


        out.println("Deseja reservar: 1 - Preço base (" + server_price + ") | 2 - Preço para leilão");

        opt = in.readLine();
        op = Integer.parseInt(opt);

        if(op == 2){
            out.println("Valor: ");
            opt = in.readLine();
            int value = Integer.parseInt(opt);

            adquirirServidor(value, server_type);
        }
        else adquirirServidor(server_price, server_type);

    }*/


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
