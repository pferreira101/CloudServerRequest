import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable{
    private Socket cs;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, String> clients;

    private String active_user; // Coloquei isto porque quem trata do utilizador tem que saber qual ele é e isso só acontece apos o login


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

            out.println("Bem-Vindo!");
            // Cliente deve fazer LogIn (e registar-se se necessário)
            check = validateAccess();


            if(check == 1) {
                int menu = 0;
                showOps(menu);
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
        int opt=-1, check=-1;


        do{
            out.println("1 - Log In | 2 - Registar | 0 - Sair"); // tirei \n porque conta como linha nova. so se puser em ciclo a ler cada linha?
            msg = in.readLine();
            try{
                opt = Integer.parseInt(msg);
                String user;
                String pw; // declarei estes dois aqui porque dentro do switch nao dava idkw

                switch(opt) {
                    case 1:
                        do{
                            out.println("User: ");
                            user = in.readLine();
                            out.println("Password: ");
                            pw = in.readLine();

                            check = logIn(user, pw);
                            if(check == 1) return 1;
                        } while(check != 1);

                        break;
                    case 2:
                        do{
                            if(check == 0) out.println("Utilizador já existente.");
                            out.println("User: ");
                            user = in.readLine();
                            out.println("Password: ");
                            pw = in.readLine();

                            check = registerClient(user, pw);
                        } while(check != 1);

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
        System.out.println("Login feito como: " + active_user);
    }


    private int logIn(String user, String pw){
        synchronized (clients){
            if(this.clients.containsKey(user) && this.clients.get(user).equals(pw)){
                this.active_user = user;
                return 1;
            }
        }
        return 0;
    }

    private int registerClient(String user, String pw) {
        synchronized (clients){
            if(!this.clients.containsKey(user)){
                this.clients.put(user, pw);
                return 1;
            }
        }
        return 0;
    }

    void showOps(int menu){
        switch (menu){
            case 0:
                out.println("1 - Comprar servidor | 2 - Libertar servidor | 3 - Consultar divida");
        }
    }
}
