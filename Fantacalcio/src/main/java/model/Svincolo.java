package model; // Package model (Entity)[cite: 4].

public class Svincolo extends TransazioneMercato { // Sottoclasse per la gestione dello svincolo (Generalizzazione UML)[cite: 2, 4].
    private int trattenutaPenale; // Penale dello svincolo.

    @Override
    public void applicaTransazione() { // Implementazione polimorfa per il recupero crediti[cite: 4].
        int recupero = getImportoCrediti() - trattenutaPenale; // Calcola i crediti restituiti.
        getSquadra().setCreditiResidui(getSquadra().getCreditiResidui() + recupero); // Riaccredita i crediti alla squadra.
    }

    public int getTrattenutaPenale() { return trattenutaPenale; } // Getter Penale.
    public void setTrattenutaPenale(int trattenutaPenale) { this.trattenutaPenale = trattenutaPenale; } // Setter Penale.
}