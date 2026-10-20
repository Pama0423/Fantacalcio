package model; // Package model (Entity)[cite: 4].

public class OffertaAsta extends TransazioneMercato { // Eredita da TransazioneMercato (Generalizzazione UML)[cite: 2, 4].
    private boolean isVincente; // Flag di vittoria offerta.
    private Asta asta; // Riferimento all'asta attiva.

    @Override
    public void applicaTransazione() { // Implementazione polimorfa per l'acquisto[cite: 4].
        if (isVincente && getSquadra() != null && getCalciatore() != null) { // Controlla se l'offerta è valida.
            getSquadra().sottraiCrediti(getImportoCrediti()); // Scala i crediti dalla squadra.
            getSquadra().getRosa().add(getCalciatore()); // Inserisce il calciatore acquistato nella rosa.
        }
    }

    public boolean isVincente() { return isVincente; } // Getter Vincente.
    public void setVincente(boolean vincente) { isVincente = vincente; } // Setter Vincente.
    public Asta getAsta() { return asta; } // Getter Asta.
    public void setAsta(Asta asta) { this.asta = asta; } // Setter Asta.
}