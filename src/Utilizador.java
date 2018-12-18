import java.util.concurrent.locks.*;
import java.util.*;

public class Utilizador {
	private final String username;
	private String password;
	private double divida;
	private Lock l;
	private List<String> servidores;

	/**
		Construtor da class Utilizador

		@param username String com o email do Utilizador.
		@param password String com a password do Utilizador.
	*/

	public Utilizador(String username,String password){
		this.username = username;
		this.password = password;
		this.divida = 0.0;
		this.servidores = new ArrayList<>();
		this.l = new ReentrantLock();
	}

	/**
		Método get para o Username

		@return String com o email do Utilizador.
	*/

	public String getUsername(){
		return this.username;
	}

	/**
		Método para verificar se a password coincidade com a do Utilizador.

		@param password String com a password a testar.

		@return Boolean se as passwords são iguais ou não.
	*/

	public boolean authenticate(String password){
		return this.password.equals(password);
	}

	/**
		Método get para ter o valor da dívida.

		@return Valor atual da dívida.
	*/

	public double getDivida(){
		double value;
		try{
			l.lock();
			value = this.divida;
		}
		finally{l.unlock();}

		return value;
	}

	/**
		Método para adicionar um valor à dívida atual.

		@param value Valor a adicionar à dívida atual.

	*/

	public void addDivida(double value){
		try{
			l.lock();
			this.divida += value;
		}
		finally{l.unlock();}
	}

	public void addServidor(String str){
		try{
			l.lock();
			this.servidores.add(str);
		}
		finally{l.unlock();}
	}

	public void removeServidor(String str){
		try{
			l.lock();
			this.servidores.remove(str);
		}
		finally{l.unlock();}
	}

	public boolean donoServidor(String str){
		boolean b = false;
		try{
			l.lock();
			b = this.servidores.contains(str);
		}
		finally{l.unlock();}

		return b;
	}
}
