import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandleConnection implements Runnable{

    public ConcurrentHashMap<String, Utente> utenti;

    public int port = 0;
    public String address;
    public int multicastport;
    public String multicastaddress;
    public File f;
    public String w;

    public final ExecutorService exec;

    public HandleConnection(ConcurrentHashMap<String, Utente> utenti, int port , String address, int multicastport, String multicastaddress, ExecutorService exec, File f, Word w ){
        this.utenti = utenti;
        this.port = port;
        this.address = address;
        this.multicastport = multicastport;
        this.multicastaddress = multicastaddress;
        this.exec = exec;
        this.f = f;
        this.w = w.getW();
    }
    @Override
    public void run() {


        boolean done = true;
        ServerSocket serverSocket = null;
        MulticastSocket ms = null;
        InetAddress addr;
        try {
            ms = new MulticastSocket(multicastport);
            addr = InetAddress.getByName(multicastaddress);
            ms.joinGroup(addr);
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HandleTime.s = serverSocket;
        while (done){
            Socket cl = new Socket();
            try{
                cl = serverSocket.accept();
                cl.setSoTimeout(20*1000);

            } catch (IOException e) {
                e.printStackTrace();
                }

            try {
                if (cl.isBound()) {
                    System.out.println("SERVER CONNESSO: " + cl.getPort());
                }
                exec.submit(new Game(cl,utenti,w,multicastaddress,multicastport, f));
            } catch (IOException e) {
                throw new RuntimeException(e);
                }

        }
    }
}
