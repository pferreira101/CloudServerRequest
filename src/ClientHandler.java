import java.io.*;
import java.net.Socket;
import java.util.Map;

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
        int check;

        try{
            out = new PrintWriter(cs.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));

            out.println("Bem-Vindo!");
            // Cliente deve fazer LogIn (e registar-se se necessário)
            check = validateAccess();


            if(check == 1) {
                int menu = 0;
				System.out.println("Login feito como: " + this.active_user);
				this.after_authentication();
            }

			System.out.println("Disconnected -> " + this.active_user); // no cliente as threads continuam a correr.
            in.close();
            out.close();
            cs.close();

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /*
    returns
        1  se autenticação é validada
        -1 se cliente cancela operação de log in ou registo
     */
    private int validateAccess() throws IOException {
        String msg;
        int opt=-1, check=-1;


        do{
            out.println("1 - Log In | 2 - Registar | 0 - Sair"); // tirei \n porque conta como linha nova. so se puser em ciclo a ler cada linha?
            msg = in.readLine();
            try{
                opt = Integer.parseInt(msg);
                String user;
                String pw;

                switch(opt) {
                    case 1:
                        do{
                            out.println("User: ");
                            user = in.readLine();
                            out.println("Password: ");
                            pw = in.readLine();

                            check = logIn(user, pw);
                            if(check == 1) return 1;
                        } while(check != 1);

                        break;
                    case 2:
                        do{
                            if(check == 0) out.println("Utilizador já existente.");
                            out.println("User: ");
                            user = in.readLine();
                            out.println("Password: ");
                            pw = in.readLine();

                            check = registerClient(user, pw);
                        } while(check != 1);

                        break;
                    case 0:
                        break;
                    default: out.println("Insira um dígito válido.\n");
                }

            }
            catch (NumberFormatException e){
                out.println("Input inválido. Insira um dígito.\n");
            }


        }while(opt != 0);

        return -1; // cliente cancela autenticação


    }




    // conforme a mensagem recebida fazer coisas (?)
    private void process(String msg) {
        System.out.println("Logged In -> " + active_user);
    }

	private void after_authentication() throws IOException {
		showOps(0);
		int opt = -1, v_leitura = -1;
		String msg;

		while(opt == -1){
			try{
				msg = in.readLine();
				v_leitura = Integer.parseInt(msg);

				switch(v_leitura){
					case 1: {
						showOps(1);
						msg = in.readLine();
						int x = Integer.parseInt(msg);
						if(x != 0) processaReserva(x);
						break;
					}

					case 2: {
                        if(getUser(this.active_user).getOwnedServers() != "") { // FIXME: 12/20/2018 não está a funcionar
					        showOps(2);
                            String server_id = in.readLine();
                            this.libertarServidor(server_id);
                        }
                        else out.println("Não possui nenhum servidor.");
						break;
					}

					case 3: {
						out.println("Valor da dívida = " + this.getDividaUser());
						break;
					}

					case 4:{
						break;
					}

					default:{
						out.println("Insira um dígito válido.\n");
						v_leitura = -1;
						break;
					}
				}
			}
			catch (NumberFormatException e){
                out.println("Input inválido. Insira um dígito.\n");
            }

			opt = v_leitura;
		}

		if (opt != 4){
			this.after_authentication();
		}
	}

// é preciso lock no log in? lock no user?
    private int logIn(String user, String pw){
		Utilizador util;
        synchronized (clients){
            if(this.clients.containsKey(user) && this.clients.get(user).authenticate(pw)){
                this.active_user = user;
				return 1;
			}
        }
		out.println("Credenciais incorretas.");
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

	private Utilizador getUser(String str){
		Utilizador user;
		synchronized (this.clients){
			user = clients.get(str);
		}
		return user;
	}

	private void adquirirServidor(double price, String type){
		Utilizador user = this.getUser(this.active_user);
		ServerTypeManager s = this.stm.get(type);
		String server_id = s.adquirir(price,user.getUsername());

		user.addServidor(server_id); // tem lock dentro da class

		out.println("Servidor adquirido: " + server_id);
	}

	private void libertarServidor(String server_id){
		Utilizador user = this.getUser(this.active_user);

        if (user.donoServidor(server_id)){
            double price = getServerSMT(server_id).libertar(server_id);

            user.removeServidor(server_id);
            user.addDivida(price);

            out.println("Servidor libertado. Aumento da dívida em: " + price);
        }
        else {
            out.println("Não és dono do servidor: " + server_id);
        }
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
     * Função que processa a reserva de um servidor
     * @param x opção escolhida pelo utilizador para o tipo de servidor
     * @throws IOException
     */
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
