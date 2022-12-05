import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Runnable{
    private final ConcurrentHashMap<String, Utente> utenti;
    private Utente utente;
    private Socket client;

    private BufferedReader in;
    private OutputStreamWriter out;
    private String SW;

    private final String multicastaddress;
    private final int multicastport ;
    private File f;
    public Game(Socket s, ConcurrentHashMap<String, Utente> utenti, String w, String multicastaddress, int multicastport, File f ) throws IOException {
        client = s;
        this.multicastaddress = multicastaddress;
        this.multicastport = multicastport;
        this.f = f;

        //in = client.getInputStream();

        in= new BufferedReader( new InputStreamReader( client.getInputStream()) );
        out = new OutputStreamWriter(client.getOutputStream());
        this.utenti = utenti;
        SW = w;
    }
    void register(String[] cmd) throws IOException {
        System.out.println(Arrays.toString(cmd));
        Utente u = new Utente(cmd[1], cmd[2]);
        if (!this.utenti.containsKey(u.getUsername())){
            this.utenti.put(u.getUsername(),u);
            out.write("UTENTE CREATO CORRETTAMENTE\n");

            System.out.println("UTENTE CREATO CORRETTAMENTE");

        }
        else {
            out.write("UTENTE GIÀ ASSOCIATO A QUESTO USERNAME\n");
            System.err.println("UTENTE GIÀ ASSOCIATO A QUESTO USERNAME");
        }
        out.flush();
        System.out.println("out flush");

    }

    void login(String[] cmd) throws IOException {
        Utente check = this.utenti.get(cmd[1]);
        if (check != null && check.getPassword().compareTo(cmd[2]) == 0  ){
            utente = check;
            out.write("LOGIN CORRETTO\n");
        }
        else {
            if (!this.utenti.containsKey(cmd[1])){
                out.write("UTENTE NON TROVATO\n");
            }
            else{
                System.out.println( "utente " + cmd[1] + " pw " + cmd[2]);
                out.write("PASSWORD ERRATA\n");
            }
        }
        out.flush();

    }

    public void share() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(multicastaddress);
        String cp = utente.getResults();
        String[] res = cp.split("!");
        int i = 0;
        while (res.length > 0){
            byte[] msg =  Arrays.asList(res).get(0).getBytes();
            System.out.println("CLIENT: " + Arrays.toString(msg));
            DatagramPacket packet = new DatagramPacket(msg, msg.length,
                    group, multicastport);
            socket.send(packet);
            res = Arrays.copyOfRange(res, 1, res.length);
        }
        socket.close();

    }
    public void sendMeStatistics() throws IOException {
        out.write(utente.toString() + "\n");
        out.flush();
    }

    public int game() throws IOException, InterruptedException {
        if (utente == null){
            return 1;
        }
        if (utente.isGame()){
            return 2;
        }

        Play p = new Play(SW,utente,in,out);
        utente.numberPlayed++;
        utente.setGame(true);
        Thread p_t = new Thread(p);
        p_t.start();

        return 0;
    }
    public void send(String[] cmd) {
        System.out.println("send .... " + cmd [1]);
        try {
            List<String> ls = Files.readAllLines(f.toPath());
            if (ls.contains(cmd[1])){
                Play.GW = cmd[1];
                Play.read = true;
            }
            else {
                out.write("LA PAROLA NON È INCLUSA NEL DIZIONARIO\n");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public void run() {
        try {
            out.write("INSERT COMMAND:\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //InputStreamReader in;
        //Scanner in;

        String command = "";
        while (command.compareTo("end") != 0){
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(command + "...");

            if (command.contains("register")){
                String[] cmd = command.split(" ");
                if (cmd.length != 3) {
                    try {
                        out.write("NUMERO DI ARGOMENTI INCORRETTO");
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    try {
                        register(cmd);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else if (command.contains("login")){
                String[] cmd = command.split(" ");
                if (cmd.length != 3) {
                    try {
                        out.write("NUMERO DI ARGOMENTI INCORRETTO\n");
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    try {
                        login(cmd);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else if (command.contains("playWordle")){
                String[] cmd = command.split(" ");

                try {
                    int i = game();
                    if (i == 1){
                        out.write("NESSUN UTENTE LOGGATO\n");
                        out.flush();
                    }
                    else if (i == 2){
                        out.write("L'UTENTE HA GIÀ GIOCATO\n");
                        out.flush();
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (command.contains("sendWord")){
                String[] cmd = command.split(" ");
                if (cmd.length != 2) {
                    try {
                        out.write("NUMERO DI ARGOMENTI INCORRETTO\n");
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    send(cmd);
                }

            }
            else if (command.contains("sendMeStatistics")){
                try {
                    sendMeStatistics();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            else if (command.contains("share")){
                try {
                    share();
                    out.write("condiviso\n");
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            else if (command.contains("showMeSharing")){
                try {
                    System.out.println("sharing");
                    out.write("stampa\n");
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (command.contains("logout")){
                String[] cmd = command.split(" ");
                if (cmd.length != 2) {
                    try {
                        out.write("NUMERO DI ARGOMENTI INCORRETTO\n");
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    if (cmd[1].compareTo(utente.getUsername()) == 0){
                        try {
                            utente.setGame(true);
                            out.write("USCITA\n");
                            out.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                }
            }
            else {
                try {
                    out.write("COMANDO ERRATO\n");
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

