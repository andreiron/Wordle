import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Objects;


public class Play implements Runnable{

    public String SW;
    public final Utente utente;
    public OutputStreamWriter out;
    public static String GW;
    public static boolean read;

    public Play(String sw, Utente u , BufferedReader in, OutputStreamWriter out){
        this.SW = sw;
        this.utente = u;
        this.out = out;
    }
    static int find(String[] sw, String gw, boolean[] check){
        for (int i = 0; i < sw.length; i++) {
            if (Objects.equals(sw[i], gw) && !check[i]){

                return i;
            }
        }
        return -1;
    }

    @Override
    public void run() {

        try {
            out.write("inizio\n");
            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] SWLetters = SW.split("");

        int rounds = 0;
        String results = "";

        while (rounds < 13) {
            String[] ret = new String[10];
            Arrays.fill(ret,"_");
            read = false;
            while (!read){
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //GW = new String(in.readAllBytes(),StandardCharsets.UTF_8);

            if (GW.length() > 10 ){
                try {
                    out.write("parola troppo lunga " + GW.length() + "\n");
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            System.out.println(GW);
            String[] GWLetters = GW.split("");
            System.out.println(Arrays.toString(GWLetters));
            boolean[] check = new boolean[10];
            Arrays.fill(check, false);


            if (GW.equals(SW)) {
                String[] r  = new String[10];
                Arrays.fill(r, "O" );
                String right = String.format("%0" + 10 + "d", 0).replace('0', 'O');
                results = results.concat( Arrays.deepToString(r));
                utente.win++;
                utente.winStreak++;
                utente.maxStreak = Math.max(utente.maxStreak, utente.winStreak);
                try {
                    out.write("YOU WON!!\n");
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }

            for (int i = 0; i < GWLetters.length; i++) {
                if ( SWLetters[i].compareTo(GWLetters[i]) == 0) {
                    ret[i] = "O";
                    check[i] = true;
                }
            }
            for (int i = 0; i < GWLetters.length; i++) {
                if (check[i]){
                    continue;
                }
                int h = (find(SWLetters, GWLetters[i], check));
                if ( h != -1){
                    check[i] = true;
                    ret[i] = "X";

                }
            }
            try {
                out.write(Arrays.deepToString(ret) + "\n");
                out.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            results = results.concat(Arrays.deepToString(ret)).concat("!");
            rounds++;
        }
        if (rounds <= 12){
            utente.guessDistribution = ((utente.guessDistribution*utente.win)+rounds) / utente.win;

        }
        utente.setResults(results);
        System.out.println(results);
    }
}
