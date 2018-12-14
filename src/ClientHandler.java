import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable{
    private Socket cs;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, String> clients;


    public ClientHandler(Socket cs, Map<String, String> clients) {
        this.cs = cs;
        this.clients = clients;
    }

    public void run(){
        String msg;
        int check;

        try{
            out = new PrintWriter(cs.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));

            //Cliente deve fazer LogIn (e registar-se se necessário)
            check = validateAccess();


            if(check == 1) {
                while ((msg = in.readLine()) != null) {
                    // processar a mensagem recebida
                    process(msg);
                }
            }

            in.close();
            out.close();
            cs.close();

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    /*
    returns
        1  se autenticação é validada
        -1 se cliente cancela operação de log in ou registo
     */
    private int validateAccess() throws IOException {
        String msg;
        int opt=-1, check=0;


        do{
            out.println("1 - Log In \n 2 - Registar \n 0 - Sair\n");
            msg=in.readLine();
            try{
                opt = Integer.parseInt(msg);

                switch(opt) {
                    case 1:
                        check = logIn();
                        if(check == 1) return 1;
                        break;
                    case 2:
                        regNewClient();
                        break;
                    case 0:
                        break;
                    default: out.println("Insira um dígito válido.\n");
                }

            }
            catch (NumberFormatException e){
                out.println("Input inválido. Insira um dígito.\n");
            }


        }while(opt != 0);

        return -1; // cliente cancela autenticação


    }


    // conforme a mensagem recebida fazer coisas (?)
    private void process(String msg) {
    }
}
