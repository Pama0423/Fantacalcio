package model; // Package model (Entity)[cite: 4].

public abstract class TransazioneMercato { // Classe astratta che applica il polimorfismo[cite: 3, 4].
    private int id; // ID transazione.
    private int importoCrediti; // Crediti impegnati.
    private FantaSquadra squadra; // Squadra coinvolta.
    private Calciatore calciatore; // Calciatore oggetto della transazione.

    public abstract void applicaTransazione(); // Metodo astratto sovrascritto nelle sottoclassi.

    public int getId() { return id; } // Getter ID.
    public void setId(int id) { this.id = id; } // Setter ID.
    public int getImportoCrediti() { return importoCrediti; } // Getter Importo.
    public void setImportoCrediti(int importoCrediti) { this.importoCrediti = importoCrediti; } // Setter Importo.
    public FantaSquadra getSquadra() { return squadra; } // Getter Squadra.
    public void setSquadra(FantaSquadra squadra) { this.squadra = squadra; } // Setter Squadra.
    public Calciatore getCalciatore() { return calciatore; } // Getter Calciatore.
    public void setCalciatore(Calciatore calciatore) { this.calciatore = calciatore; } // Setter Calciatore.
}