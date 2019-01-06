//package Servidor;

import java.util.concurrent.locks.*;
import java.time.*;

public class Servidor{
	private String id;
	private double price;
	private int status; // 0 livre 1 ocupado 2 em leilão
	private Utilizador actual_owner; // dono
	private LocalDateTime adquirido; // data de aquisição
	private final String type; // tipo
	private final Lock l;
	private Condition bid_steal;


	public Servidor(String id, String type, double price){
		this.l = new ReentrantLock();
		this.id = id;
		this.status = 0;
		this.type = type;
		this.price = price;
		this.actual_owner = null;
		this.adquirido = null;
		this.bid_steal = this.l.newCondition();
	}

	/**
		Método alterar o estado do servidor para livre.
		@return Valor a pagar
	*/
	public double freeServer(){
		double paying = 0;
		boolean valid = false;
		try{
			l.lock();

			if (this.status == 2)
				valid = true;

			this.status = 0;
			this.actual_owner = null;

			LocalDateTime libertado=LocalDateTime.now();
			Duration duration = Duration.between(adquirido,libertado);
    		double diff = duration.getSeconds();

    		paying = this.price*(diff/3600);

			if (valid)
				this.bid_steal.signal();

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
		@param owner Comprador.
	*/
	public void buyServer(double price, Utilizador owner){
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
		Método reservar o servidor por Licitacao
		@param price Valor do aluguer.
		@param owner Comprador.
	*/

	public void bidServer(double price, Utilizador owner){
		try{
			l.lock();
			this.status = 2;
			this.price = price;
			this.actual_owner = owner;
			this.adquirido = LocalDateTime.now();
		}
		finally{l.unlock();}
	}

	/**
		Método para requisitar um servidor vendido por leilão
		@param price Preço novo do servidor.
		@param owner Novo dono do servidor.
	*/

	public void buyBiddenServer(double price, Utilizador owner){
		try{
			l.lock();
			this.status = 1;
			this.actual_owner.removeServidor(this.id);

			LocalDateTime libertado=LocalDateTime.now();
			Duration duration = Duration.between(adquirido,libertado);
    		double diff = duration.getSeconds();
			double paying = this.price*(diff/3600);

			this.actual_owner.addDivida(paying);

			this.price = price;
			this.adquirido = libertado;
			this.actual_owner = owner;

			this.bid_steal.signal();
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

	/**
	 * Método que verifica se o valor do status mudou depois
	 * @return booleano caso o status do servidor tenha mudado por compra
	 */

	public boolean warningAfterBid() throws InterruptedException{
		boolean valid = false;
		try{
			this.l.lock();

			while(this.status == 2)
				this.bid_steal.await();

			if (this.status == 1)
				valid = true;
		}
		finally{this.l.unlock();}

		return valid;
	}

}
