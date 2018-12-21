public class Licitacao {

    private String user;
    private double price;

    // Ordena na priorityQueue por ordem decrescente do preço da licitação para que seja mais rapido pegar no licitador maior para que lhe seja atribuido o service
    public int compareTo(Object o){
        if(this.price < ((Licitacao)o).price) return 1;
        else return -1;
    }

    public Licitacao(String user, int price){
        this.user = user;
        this.price = price;
    }
}
