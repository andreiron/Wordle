import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientMain {


    public static void main(String[] args) throws IOException {
        System.out.println("thread: " + Thread.currentThread().getName());
        InetAddress addr;
        int port = 0;
        String address = null;
        int multicastport = 0;
        String multicastaddress = null;
        File fileconfig = new File("config.txt");
        BufferedReader config = new BufferedReader( new FileReader(fileconfig));
        String s = config.readLine();
        while (s != null){
            String[] set = s.trim().split(":");

            switch (set[0]) {
                case "port":
                    port = Integer.parseInt(set[1]);
                    break;
                case "address":
                    address = set[1];
                    break;
                case "multicast_port":
                    multicastport = Integer.parseInt(set[1]);
                    break;
                case "multicast_address":
                    multicastaddress = set[1];
                    break;
                default:
                    break;
            }
            s = config.readLine();
        }

        try {
            addr= InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        Socket sk;
        try {
            sk = new Socket(addr,port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("CONNESSO: " + sk.getInetAddress().toString());

        ClientMulticast clm = new ClientMulticast(multicastaddress, multicastport);
        Thread m = new Thread(clm);
        m.start();

        OutputStream out;
        BufferedReader in = null;
        try {
            in= new BufferedReader( new InputStreamReader( sk.getInputStream()) );
            out = sk.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Scanner keyboard = new Scanner(System.in);
        while (true){
            try {
                Thread.sleep(500);
                String ans = in.readLine();
                System.out.println(ans);
                if (ans.compareTo("USCITA") == 0){
                    sk.close();
                    clm.done = true;
                    break;
                }
                else if (ans.compareTo("stampa") == 0){
                    clm.notifiche.forEach( e -> {
                        System.out.println("RICEVUTO: " + e);
                    });
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            String command = keyboard.nextLine() + "\n";
            System.out.println(command);
            try {
                out.write(command.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        System.out.println("esco");
        clm.ms.close();
    }
}
