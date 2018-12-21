import java.util.concurrent.locks.*;
import java.time.*;

public class Servidor{
	private String id;
	private double price;
	private int status; // 0 livre 1 ocupado 2 em leilão 3 leiloado
	private String actual_owner; // dono
	private LocalDateTime adquirido; // data de aquisição
	private final String type; // tipo
	private final Lock l;


	public Servidor(String id, String type, double price){
		this.l = new ReentrantLock();
		this.id = id;
		this.status = 0;
		this.type = type;
		this.price = price;
		this.actual_owner = "";
		this.adquirido = null;
	}

	/**
		Método alterar o estado do servidor para livre.
		@return Valor a pagar
	*/
	public double freeServer(){
		double paying = 0;
		try{
			l.lock();
			this.status = 0;
			this.actual_owner = "";

			LocalDateTime libertado=LocalDateTime.now();
			Duration duration = Duration.between(adquirido,libertado);
    		double diff = duration.toSeconds();

    		paying = this.price*(diff/120);
		}
		finally{
			l.unlock();
		}
		return paying;
	}

	/**
		Método get para o valor do status.
		@return Valor do status.
	*/
	public int getStatus(){
		int status = -1;
		try{
			l.lock();
			status  = this.status;
		}
		finally{
			l.unlock();
		}

		return status;
	}

	/**
		Método reservar o servidor por um preço.
		@param price Valor do aluguer.
	*/
	public void buyServer(double price, String owner){
		try{
			l.lock();
			this.status = 1;
			this.price = price;
			this.actual_owner=owner;
			this.adquirido = LocalDateTime.now();
		}
		finally{l.unlock();}
	}

	/**
		Método get para o id do servidor.
		@return Id do servidor.
	*/
	public String getID(){
		return this.id;
	}


	/**
	 * Getter do preço do servidor
	 * @return preço do servior
	 */
	public double getPrice(){
		return this.price;
	}

}
