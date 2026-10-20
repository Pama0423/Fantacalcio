package dao; // Dichiara il package dao. Perché: Interfaccia di persistenza astratta per le squadre[cite: 4].

import model.Calciatore; // Importa la classe Entity Calciatore[cite: 4].
import model.FantaSquadra; // Importa la classe Entity FantaSquadra[cite: 4].
import java.util.List; // Importa la struttura List.
import java.util.Map; // Importa la struttura Map.

public interface FantaSquadraDAO { // Interfaccia DAO per la gestione squadre[cite: 4].
    List<FantaSquadra> findAllByLega(int idLega); // Recupera le squadre di una lega.
    boolean aggiungiAllaRosa(int idSquadra, int idCalciatore); // Inserisce la coppia nella tabella Rosa.
    int getCreditiResidui(int idSquadra); // Legge il saldo crediti dal DB.

    // METODO PER INSERIRE DINAMICAMENTE UNA SQUADRA E IL SUO FANTALLENATORE NEL DB
    boolean inserisciNuovaSquadra(String nomeSquadra, String usernameAllenatore, int idLega); // Registra un nuovo allenatore e la sua squadra con 500 crediti nel DB.

    // METODI PER LA GESTIONE DELLA LEGA E DEGLI SVINCOLI
    Map<Calciatore, Integer> getRosaConPrezzi(int idSquadra); // Recupera i calciatori in rosa con i prezzi spesi.
    boolean svincolaCalciatore(int idSquadra, int idCalciatore, int importoRiaccredito); // Cancella dalla rosa e riaccredita i crediti nel DB.
}