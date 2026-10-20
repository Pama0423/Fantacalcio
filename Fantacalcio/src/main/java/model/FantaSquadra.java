package model; // Dichiara il package model per la struttura BCE[cite: 4].

import java.util.ArrayList; // Importa ArrayList per gestire la rosa locale.
import java.util.List; // Interfaccia List.

public class FantaSquadra { // Entity che rappresenta una fantasquadra della lega[cite: 4].
    private int id; // Identificativo della squadra nel DB.
    private String nomeSquadra; // Nome della fantasquadra.
    private int creditiResidui = 500; // Crediti iniziali di default (500).
    private List<Calciatore> rosa = new ArrayList<>(); // Lista dei calciatori in rosa.

    public void sottraiCrediti(int importo) { // Metodo per aggiornare i crediti in memoria RAM.
        this.creditiResidui -= importo; // Sottrae la spesa effettuata.
    }

    public int getId() { return id; } // Getter ID.
    public void setId(int id) { this.id = id; } // Setter ID.
    public String getNomeSquadra() { return nomeSquadra; } // Getter Nome.
    public void setNomeSquadra(String nomeSquadra) { this.nomeSquadra = nomeSquadra; } // Setter Nome.
    public int getCreditiResidui() { return creditiResidui; } // Getter Crediti.
    public void setCreditiResidui(int creditiResidui) { this.creditiResidui = creditiResidui; } // Setter Crediti.
    public List<Calciatore> getRosa() { return rosa; } // Getter Rosa.
}