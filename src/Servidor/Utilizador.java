package Servidor;

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
		finally{
			l.unlock();
		}

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
		finally{
		    l.unlock();
		}
	}

	/**
		Método para adicionar um serdidor ao utilizador
		@param server Nome do servidor a inserir.
	*/
	public void addServidor(String server){
		try{
			l.lock();
			this.servidores.add(server);
		}
		finally{
		    l.unlock();
		}
	}

	/**
		Método para remover um serdidor ao utilizador
		@param server_id Nome do servidor a remover.
	*/
	public void removeServidor(String server_id){
		try{
			l.lock();
			this.servidores.remove(server_id);

		}
		finally{
			l.unlock();
		}
	}

	/**
		Método que verifica se o utilizador é dono de um servidor.
		@param nome Nome do servidor a remover.
        @return true se for o dono do servidor
	*/
	public boolean donoServidor(String nome){
		boolean b = false;
		try{
			l.lock();
			b = this.servidores.contains(nome);
		}
		finally{
		    l.unlock();
		}

		return b;
	}

	/**
	 * Método para obter a lista de Servidores que um utilizador tem.
	 * @return String com os servidores.
	 */


	public String getOwnedServers(){
		StringBuilder r = new StringBuilder();
		try{
			l.lock();
			for(String s : this.servidores){
				r.append(s + " | ");
			}
		}
		finally {
			l.unlock();
		}
		return r.toString();
	}
}
