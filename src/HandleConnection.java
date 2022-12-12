import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class HandleConnection implements Runnable{

    public ConcurrentHashMap<String, Utente> utenti;

    public int port = 0;
    public String address;
    public int multicastport;
    public String multicastaddress;
    public File f;
    public Word w;

    public final ExecutorService exec;

    public HandleConnection(ConcurrentHashMap<String, Utente> utenti, int port , String address, int multicastport, String multicastaddress, ExecutorService exec, File f, Word h ){
        this.utenti = utenti;
        this.port = port;
        this.address = address;
        this.multicastport = multicastport;
        this.multicastaddress = multicastaddress;
        this.exec = exec;
        this.f = f;
        this.w = h;
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
        while (done){
            Socket cl = new Socket();
            try{
                cl = serverSocket.accept();

            } catch (IOException e) {
                e.printStackTrace();
                }

            try {
                if (cl.isBound()) {
                    System.out.println("SERVER CONNESSO: " + cl.getPort() + "\nword: " + w.getW());
                }
                exec.submit(new Game(cl,utenti,w.getW(),multicastaddress,multicastport, f));
            } catch (IOException e) {
                throw new RuntimeException(e);
                }

        }
    }
}
