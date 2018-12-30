import java.util.*;
import java.util.concurrent.locks.*;
import java.net.Socket;
import java.io.*;


class SortQueueLicitacao implements Comparator<Licitacao>{

	public int compare(Licitacao a, Licitacao b)
    {
		if(a.getOffer() < b.getOffer()) return 1;
        else return -1;
    }

}

public class ServerTypeManager{

	private Condition livre;
	private Lock l;
	private String type;
	private int n_servidores_livres;
	private int n_servidores_leiloes;
	private Map<String, Servidor> servers;
	private Queue<Licitacao> queue;
	private Queue<String> servidores_livres;

	public ServerTypeManager(Map<String, Servidor> servers, String type, int size){
		this.l = new ReentrantLock();
		this.livre = this.l.newCondition();
		this.n_servidores_livres = size;
		this.n_servidores_leiloes = 0;
		this.servers = servers;
		this.type = type;
		this.queue = new PriorityQueue<>(new SortQueueLicitacao());
		this.servidores_livres = new LinkedList<>();

		for(String s : servers.keySet())
			this.servidores_livres.add(s);
	}

	public double libertar(String n_server){
		Servidor server_to_free;
		double price = 0;
		try{
			l.lock();
			server_to_free = this.servers.get(n_server);
			if (server_to_free.getStatus() == 2)
				this.n_servidores_leiloes--;
			price = server_to_free.freeServer();
			this.n_servidores_livres++;
			this.servidores_livres.add(n_server);
			livre.signalAll();
		}
		finally{l.unlock();}
		System.out.println(price);
		return price;
	}

	public String adquirir(double price, Utilizador owner){
		String server_id = "";
		Servidor server = null;
		try{
			l.lock();
			while(n_servidores_livres == 0 && n_servidores_leiloes == 0)
				livre.await();

			if (n_servidores_livres > 0){
				n_servidores_livres--;
				server = this.servers.get(this.servidores_livres.remove());
				server.buyServer(price,owner);
			}
			else{
				for(Servidor s : this.servers.values()){
					if (s.getStatus() == 2){
						server = s;
						break;
					}
				}
				server.buyBiddenServer(price,owner);
				n_servidores_leiloes--;
			}

			server_id = server.getID();
			owner.addServidor(server_id);
		}
		catch(InterruptedException e){System.out.println(e.getMessage());}
		finally{
			l.unlock();
		}

		return server_id;
	}

	public String licitar(double price, Utilizador user, PrintWriter out){
		Licitacao proposta;
		String active_user = user.getUsername(),server_id = "";
		Servidor s;

		try{
			l.lock();
			proposta = new Licitacao(active_user,price);
			this.queue.add(proposta);

			while(n_servidores_livres == 0 || this.queue.peek().getUser() != active_user)
				livre.await();

			this.n_servidores_leiloes++;
			this.n_servidores_livres--;

			this.queue.remove();
			s = this.servers.get(this.servidores_livres.remove());
			s.bidServer(price,user); // comprar o server
			server_id = s.getID();
			user.addServidor(server_id); // adicionar o servidor ao utilizador
			livre.signalAll();

			Thread t = new Thread(new Bidcheck(s,out));
			t.start();

		}
		catch(InterruptedException e){System.out.println(e.getMessage());}
		finally{l.unlock();}

		return server_id;
	}


    /**
     * Método que retorna o tipo do
     * @return
     */
	public String getType(){
		return this.type;
	}


    /**
     * Método que retorna todos os servidores ligados ao SMT
     * @return lista dos servidores
     */
	public Collection<Servidor> getServers(){
		return this.servers.values();
	}
}
