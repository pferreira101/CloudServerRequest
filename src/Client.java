import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String args[]) throws IOException {

        Socket cs = new Socket("127.0.0.1", 999);

        PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));

        BufferedReader sys_in = new BufferedReader(new InputStreamReader(System.in)); // para ler do teclado

        String welcome = in.readLine(); // foi um teste xD
        System.out.println(welcome);

        int active = 1;

        while(active == 1){ // ter uma resposta especifica para cancelar a ligacao
            String msg = in.readLine();
            System.out.println(msg); // recebo a mensagem do Handler e imprimo

            String reply = sys_in.readLine(); // leio a mensagem do teclado
            out.println(reply); // envio para o handler
        }

        in.close();
        out.close();
        sys_in.close();
        cs.close();
    }
}
