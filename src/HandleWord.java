import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class HandleWord implements Runnable{

    private final int wordtimer;
    private final File file;
    private final ConcurrentHashMap<String, Utente> utenti;
    private final Word w;

    public HandleWord(int wordtimer, File file, ConcurrentHashMap<String, Utente> utenti, Word w){
        this.wordtimer = wordtimer;
        this.file = file;
        this.utenti = utenti;
        this.w = w;
    }


    @Override
    public void run() {
        Random rand = new Random();
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(new File(file.toURI()), "r");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (true){
            try {
                f.seek(11 * rand.nextInt(30823));
                w.setW(f.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("cambio parola: " + w.getW());

            utenti.forEach( (key,utente) -> {
                utenti.get(key).setResults("");
                utenti.get(key).setGame(false);

            });

            try {
                Thread.sleep(wordtimer);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}

