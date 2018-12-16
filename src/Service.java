import java.util.PriorityQueue;
import java.util.Queue;

public class Service { // servidor que é possivel comprar - Server vai ter um map disto?

    private int id;
    private String name;
    private double price;
    private String current_owner;
    private int fully_owned; // flag que indica se foi comprado por leilao ou não
    private Queue<Licitacao> queue = new PriorityQueue<>();
}
