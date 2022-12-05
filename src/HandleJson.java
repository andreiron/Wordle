import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HandleJson implements Runnable{

    private final ConcurrentHashMap<String,Utente> utenti;

    public HandleJson (ConcurrentHashMap<String,Utente> utenti){

        this.utenti = utenti;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Gson gson = new Gson();
            File jsonfile = new File("Utenti.json");

            String utentijson = gson.toJson(utenti);
            try {
                BufferedOutputStream bout = new BufferedOutputStream( new FileOutputStream( jsonfile));
                bout.write(utentijson.getBytes());
                bout.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
