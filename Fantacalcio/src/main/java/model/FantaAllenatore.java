package model; // Package model (Entity).

public class FantaAllenatore { // Entity per l'utente/allenatore.
    private int id; // ID allenatore.
    private String username; // Nome utente/allenatore.
    private FantaSquadra fantaSquadra; // Squadra posseduta dall'allenatore (Relazione 1 a 1 dal diagramma UML).

    public int getId() { return id; } // Getter ID.
    public void setId(int id) { this.id = id; } // Setter ID.
    public String getUsername() { return username; } // Getter Username.
    public void setUsername(String username) { this.username = username; } // Setter Username.
    public FantaSquadra getFantaSquadra() { return fantaSquadra; } // Getter FantaSquadra.
    public void setFantaSquadra(FantaSquadra fantaSquadra) { this.fantaSquadra = fantaSquadra; } // Setter FantaSquadra.
}