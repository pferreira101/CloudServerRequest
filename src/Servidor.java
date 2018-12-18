import java.util.concurrent.locks.*;

public class Servidor{
	private String id;
	private double price;
	private int status; // 0 livre 1 ocupado 2 em leilão 3 leiloado
	private String atual_owner; // dono
	private final String type; // tipo
	private final Lock l;


	public Servidor(String id,String type){
		this.l = new ReentrantLock();
		this.id = id;
		this.status = 0;
		this.type = type;
		this.price = 0;
		this.atual_owner = "";
	}

	/**
		Método alterar o estado do servidor para livre.

		@return Valor a pagar

	*/

	public double changeStatus(){
		double paying = 0;
		try{
			l.lock();
			paying = this.price;
			this.status = 0;
		}
		finally{l.unlock();}

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
		finally{l.unlock();}

		return status;
	}

	/**
		Método reservar o servidor.

		@param price Valor do aluguer.

	*/


	public void buyServer(double price){
		try{
			l.lock();
			this.status = 1;
			this.price = price;
		}
		finally{l.unlock();}
	}

	/**
		Método get para o id do servidor.

		@return Id do servidor.

	*/

	public String getId(){
		return this.id;
	}

}
