package model; // Package model (Entity)[cite: 4].

import java.util.ArrayList; // Gestione lista squadre.
import java.util.List; // Interfaccia List.

public class Lega { // Entity che rappresenta la Lega Fantacalcio[cite: 4].
    private int id = 1; // ID Lega.
    private String nome = "Lega Serie A"; // Nome del campionato.
    private List<FantaSquadra> squadre = new ArrayList<>(); // Squadre appartenenti alla lega.

    public int getId() { return id; } // Getter ID.
    public void setId(int id) { this.id = id; } // Setter ID.
    public String getNome() { return nome; } // Getter Nome.
    public void setNome(String nome) { this.nome = nome; } // Setter Nome.
    public List<FantaSquadra> getSquadre() { return squadre; } // Getter Squadre.
}