import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ClientMulticast implements Runnable{
    public final int multicastport;
    public final String address;
    public ArrayList<String> notifiche = new ArrayList<>();
    public MulticastSocket ms;
    public boolean done = false;

    public ClientMulticast(String multicastaddress,int multicastport){
        this.address = multicastaddress;
        this.multicastport = multicastport;
    }

    @Override
    public void run() {

        InetAddress multicastaddr;

        try {
            ms = new MulticastSocket(multicastport);
            multicastaddr = InetAddress.getByName(address);
            ms.joinGroup(multicastaddr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (!done){
            System.out.println("multicast");
            byte[] buffer = new byte[50];
            DatagramPacket pk = new DatagramPacket(buffer,50);
            try {
                ms.receive(pk);
                System.out.println("");
            } catch (IOException e) {
                System.out.println("multicast chiuso");
            }
            String s = new String(pk.getData(), StandardCharsets.UTF_8);
            String ret = s.substring(0,s.indexOf(s.charAt(49)));
            notifiche.add(ret);
        }

    }
}
