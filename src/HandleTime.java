import java.net.ServerSocket;

public class HandleTime implements Runnable{
    public static ServerSocket s;

    @Override
    public void run() {

        try {
            Thread.sleep(1000*30);
            System.out.println("CAMBIO");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
