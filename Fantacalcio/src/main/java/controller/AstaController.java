package controller; // Package controller per rispettare il pattern BCE[cite: 4].

import dao.CalciatoreDAO; // Importa le interfacce DAO[cite: 4].
import dao.FantaSquadraDAO;
import dao.TransazioneDAO;
import dao.impl.CalciatoreDAOImpl; // Importa le implementazioni concrete DAO[cite: 4].
import dao.impl.FantaSquadraDAOImpl;
import dao.impl.TransazioneDAOImpl;
import model.*; // Importa tutte le Entity del Model[cite: 4].

import java.util.ArrayList; // Gestione liste.
import java.util.List; // Gestione collezioni.
import java.util.Map; // Gestione mappe.

public class AstaController { // Classe Control che coordina logica di business, DAO e GUI[cite: 2, 4].

    private final CalciatoreDAO calciatoreDAO = new CalciatoreDAOImpl(); // Istanzia il DAO Calciatore[cite: 4].
    private final FantaSquadraDAO squadraDAO = new FantaSquadraDAOImpl(); // Istanzia il DAO FantaSquadra[cite: 4].
    private final TransazioneDAO transazioneDAO = new TransazioneDAOImpl(); // Istanzia il DAO Transazione[cite: 4].

    private List<FantaSquadra> squadre = new ArrayList<>(); // Mantiene la lista delle squadre aggiunte alla lega.
    private Asta astaCorrente; // Memorizza l'oggetto Asta attivo.
    private Calciatore calciatoreCorrente; // Memorizza il calciatore attualmente chiamato sul banco.
    private OffertaAsta ultimaOfferta; // Memorizza l'offerta vincente provvisoria.
    private int prezzoAttuale = 0; // Prezzo corrente raggiunto dall'asta.

    public AstaController() { // Costruttore del Controller.
        this.squadre = squadraDAO.findAllByLega(1); // Carica le squadre eventualmente già presenti nel DB.
        this.astaCorrente = new Asta(); // Inizializza l'asta.
    }

    public String aggiungiSquadraAllaLega(String nomeSquadra, String usernameAllenatore) { // Metodo per aggiungere una nuova squadra e il suo allenatore.
        if (nomeSquadra == null || nomeSquadra.trim().isEmpty()) { // Controllo nome squadra vuoto.
            return "ERRORE: Inserisci un nome valido per la squadra!";
        }
        if (usernameAllenatore == null || usernameAllenatore.trim().isEmpty()) { // Controllo username allenatore vuoto.
            return "ERRORE: Inserisci un username valido per il fantallenatore!";
        }

        // Verifica se la squadra con questo nome è già presente nella lista
        for (FantaSquadra sq : squadre) {
            if (sq.getNomeSquadra().equalsIgnoreCase(nomeSquadra.trim())) {
                return "ERRORE: Esiste già una squadra con il nome '" + nomeSquadra + "'!";
            }
        }

        boolean ok = squadraDAO.inserisciNuovaSquadra(nomeSquadra.trim(), usernameAllenatore.trim(), 1); // Salva allenatore e squadra nel DB PostgreSQL[cite: 4].
        if (ok) {
            this.squadre = squadraDAO.findAllByLega(1); // Ricarica le squadre salvate nel DB.
            return "OK"; // Ritorna esito positivo.
        }
        return "ERRORE: Impossibile registrare la squadra e l'allenatore sul Database!";
    }

    public String chiamaCalciatore(String nome) { // Cerca e convalida un calciatore interrogando direttamente il DB.
        if (nome == null || nome.trim().isEmpty()) return "ERRORE: Inserisci un nome valido!"; // Controllo input[cite: 4].

        Calciatore c = calciatoreDAO.findByNome(nome); // Cerca il calciatore nella tabella Calciatore del DB[cite: 4].
        if (c == null) { // Se la query non restituisce righe.
            return "ERRORE: Calciatore non trovato nel database!"; // Blocco: Giocatore inesistente nel listone.
        }

        if (calciatoreDAO.isGiocatoreInRosa(c.getId())) { // Interroga il DB per verificare se è nella tabella Rosa[cite: 4].
            return "ERRORE: " + c.getNome() + " è già stato acquistato da una squadra!"; // Blocco anti-doppione nel DB[cite: 3].
        }

        this.calciatoreCorrente = c; // Imposta il calciatore come entità attiva dell'asta.
        this.prezzoAttuale = 0; // Azzera il prezzo base.
        this.ultimaOfferta = null; // Azzera le offerte precedenti per il nuovo turno.
        return "OK"; // Chiamata approvata.
    }

    public String gestisciRilancio(FantaSquadra offerente, int incremento) {
        if (calciatoreCorrente == null) return "Nessun calciatore attualmente chiamato!";

        // Verifica limiti di reparto lato Java prima di inviare al DB
        String ruolo = calciatoreCorrente.getRuolo();
        long attualiInRuolo = offerente.getRosa().stream()
                .filter(c -> c.getRuolo().equalsIgnoreCase(ruolo))
                .count();

        int limiteRuolo = switch (ruolo.toUpperCase()) {
            case "POR" -> 1;
            case "DIF" -> 2;
            case "CEN" -> 2;
            case "ATT" -> 1;
            default -> 0;
        };

        if (attualiInRuolo >= limiteRuolo) {
            return "Rilancio rifiutato: Reparto " + ruolo + " già al completo (" + limiteRuolo + " max)!";
        }

        int nuovaPuntata = prezzoAttuale + incremento;

        OffertaAsta offerta = new OffertaAsta();
        offerta.setImportoCrediti(nuovaPuntata);
        offerta.setVincente(true);
        offerta.setSquadra(offerente);
        offerta.setCalciatore(calciatoreCorrente);
        offerta.setAsta(astaCorrente);

        try {
            transazioneDAO.registraOfferta(offerta);
            this.prezzoAttuale = nuovaPuntata;
            this.ultimaOfferta = offerta;
            return "OK";
        } catch (Exception e) {
            return "Rilancio rifiutato dal DB: " + e.getMessage();
        }
    }

    public boolean assegnaCalciatoreAllaScadenza() { // Assegna definitivamente il calciatore alla fine del timer.
        if (ultimaOfferta != null && calciatoreCorrente != null) { // Controlla se c'è un'offerta vincente valida.
            try {
                // Inserisce nella tabella Rosa del DB (I Trigger 'Check_Crediti' e 'Max_Rosa' del DB convalidano l'assegnazione)[cite: 3, 4]
                squadraDAO.aggiungiAllaRosa(ultimaOfferta.getSquadra().getId(), calciatoreCorrente.getId());

                ultimaOfferta.applicaTransazione(); // Esegue il metodo polimorfo per aggiornare gli oggetti Java[cite: 4].

                int creditiAggiornati = squadraDAO.getCreditiResidui(ultimaOfferta.getSquadra().getId()); // Sincronizza i crediti dal DB.
                ultimaOfferta.getSquadra().setCreditiResidui(creditiAggiornati); // Assegna il valore sincronizzato.
                return true; // Assegnazione completata con successo.
            } catch (Exception e) { // Se un Trigger SQL blocca l'inserimento[cite: 3, 4].
                System.err.println("Errore assegnazione DB: " + e.getMessage()); // Registra il log[cite: 4].
                return false; // Assegnazione annullata.
            }
        }
        return false; // Nessuna offerta effettuata.
    }

    public boolean sonoTutteLeRoseComplete() { // Verifica se tutte le squadre della lega hanno completato la propria rosa.
        for (FantaSquadra sq : squadre) { // Scorre ciascuna squadra della lega.
            Map<Calciatore, Integer> rosa = squadraDAO.getRosaConPrezzi(sq.getId()); // Interroga il DB per contare i giocatori in rosa.
            if (rosa.size() < 6) { // Una rosa completa richiede 6 calciatori (1 POR + 2 DIF + 2 CEN + 1 ATT).
                return false; // Ritorna false se anche a una sola squadra manca almeno un calciatore.
            }
        }
        return true; // Ritorna true solo se tutte le squadre hanno raggiunto i 6 calciatori.
    }

    public Map<Calciatore, Integer> getRosaConPrezziSquadra(FantaSquadra squadra) { // Recupera i calciatori e i relativi prezzi spesi.
        return squadraDAO.getRosaConPrezzi(squadra.getId()); // Interroga il DAO[cite: 4].
    }

    public String svincolaCalciatore(FantaSquadra squadra, Calciatore calciatore, int prezzoSpeso) { // Gestisce lo svincolo e il riaccredito.
        boolean esito = squadraDAO.svincolaCalciatore(squadra.getId(), calciatore.getId(), prezzoSpeso); // Chiama il DAO per il DB[cite: 4].
        if (esito) {
            int creditiAggiornati = squadraDAO.getCreditiResidui(squadra.getId()); // Sincronizza i crediti dal DB.
            squadra.setCreditiResidui(creditiAggiornati); // Aggiorna l'oggetto squadra in memoria.
            squadra.getRosa().removeIf(c -> c.getId() == calciatore.getId()); // Rimuove dalla lista locale.
            return "OK";
        }
        return "ERRORE: Impossibile completare lo svincolo sul Database!";
    }

    public List<FantaSquadra> getSquadre() { return squadre; } // Getter Squadre della lega.
    public int getPrezzoAttuale() { return prezzoAttuale; } // Getter Prezzo.
    public Calciatore getCalciatoreCorrente() { return calciatoreCorrente; } // Getter Calciatore Corrente.
    public FantaSquadra getUltimoOfferente() { return ultimaOfferta != null ? ultimaOfferta.getSquadra() : null; } // Getter Vincitore Provvisorio.
}