package model; // Dichiara il package model per rispettare il pattern BCE (Entity)[cite: 4].

public class Calciatore { // Classe Entity per rappresentare il calciatore[cite: 4].
    private int id; // Identificativo univoco della tabella Calciatore.
    private String nome; // Nome completo del calciatore.
    private String ruolo; // Ruolo (POR, DIF, CEN, ATT).

    public int getId() { return id; } // Getter ID.
    public void setId(int id) { this.id = id; } // Setter ID.
    public String getNome() { return nome; } // Getter Nome.
    public void setNome(String nome) { this.nome = nome; } // Setter Nome.
    public String getRuolo() { return ruolo; } // Getter Ruolo.
    public void setRuolo(String ruolo) { this.ruolo = ruolo; } // Setter Ruolo.
}