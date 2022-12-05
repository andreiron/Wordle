public class Utente {
    public final String username;
    public final String password;
    public int winStreak;
    public int maxStreak;
    public int numberPlayed;
    public int win;
    public float guessDistribution;
    public String results;
    public boolean game;

    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.win = 0;
        this.maxStreak = 0;
        this.winStreak = 0;
        this.numberPlayed = 0;
        this.guessDistribution = 0;
        this.results = "";
        this.game = false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", win='" + win + '\'' +
                ", maxStreak='" + maxStreak + '\'' +
                ", winStreak='" + winStreak + '\'' +
                ", numberPlayed='" + numberPlayed + '\'' +
                ", guessDistribution='" + guessDistribution + '\'' +
                '}';
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public boolean isGame() {
        return game;
    }

    public void setGame(boolean game) {
        this.game = game;
    }
}
