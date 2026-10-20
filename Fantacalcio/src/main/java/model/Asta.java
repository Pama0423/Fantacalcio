package model; // Package model (Entity)[cite: 4].

public class Asta { // Entity per lo stato dell'asta in corso[cite: 4].
    private int id = 1; // ID dell'asta attiva nel DB.
    private int timerSecondi = 2; // Durata predefinita del conto alla rovescia (2s).
    private String statoAsta = "IN_CORSO"; // Stato dell'asta.

    public int getId() { return id; } // Getter ID.
    public void setId(int id) { this.id = id; } // Setter ID.
    public int getTimerSecondi() { return timerSecondi; } // Getter Timer.
    public void setTimerSecondi(int timerSecondi) { this.timerSecondi = timerSecondi; } // Setter Timer.
    public String getStatoAsta() { return statoAsta; } // Getter Stato.
    public void setStatoAsta(String statoAsta) { this.statoAsta = statoAsta; } // Setter Stato.
}