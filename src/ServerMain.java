import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServerMain {
    public static void main(String[] args) throws IOException, InterruptedException {

        int port = 0;
        String address = null;
        int multicastport = 0;
        String multicastaddress = null;
        int wordtimer = 10000;
        Word w = new Word("");
        ConcurrentHashMap<String, Utente> utenti;

        File fileconfig = new File("config.txt");
        BufferedReader config = new BufferedReader(new FileReader(fileconfig));
        String s = config.readLine();
        while (s != null) {
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
                case "wordtimer":
                    wordtimer = Integer.parseInt(set[1]);
                    break;
                default:
                    break;
            }
            s = config.readLine();
        }

            File file = new File("words.txt");

            ExecutorService exec = Executors.newFixedThreadPool(5);

            Gson gson = new Gson();
            File jsonfile = new File("Utenti.json");
            JsonElement json = JsonParser.parseReader(new JsonReader(new FileReader("Utenti.json")));
            JsonReader jsonReader = new JsonReader(new FileReader(jsonfile));
            Type maputenti_t = new TypeToken<HashMap<String, Utente>>() {
            }.getType();
            HashMap<String, Utente> prova = gson.fromJson(jsonReader, maputenti_t);
            if (prova == null) {
                utenti = new ConcurrentHashMap<>();
            } else {
                utenti = new ConcurrentHashMap<>(prova);
            }


            Thread nextword = new Thread(new HandleWord(wordtimer, file, utenti, w));
            nextword.start();

            System.out.println(port);
            HandleConnection hc = new HandleConnection(utenti,port,address,multicastport,multicastaddress,exec, file,w);
            Thread handle = new Thread(hc);
            handle.start();

            Thread handleJson = new Thread(new HandleJson(utenti));
            handleJson.start();

        }
    }

