public class Licitacao {

    private String user;
    private double price;

    public Licitacao(String user, double price){
        this.user = user;
        this.price = price;
    }
	public String getUser(){
		return this.user;
	}

	public double getOffer(){
		return this.price;
	}
}
