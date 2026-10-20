package dao; // Dichiara il package dao. Perché: Interfaccia di persistenza astratta secondo la specifica[cite: 4].

import model.Calciatore; // Importa l'Entity Calciatore. Perché: Definisce il tipo di dato gestito.

public interface CalciatoreDAO { // Interfaccia per l'accesso ai calciatori nel DB. Perché: Separa le operazioni concettuali dalle query SQL[cite: 4].
    Calciatore findByNome(String nome); // Trova un calciatore per nome. Perché: Usato per chiamare i giocatori dal DB.
    boolean isGiocatoreInRosa(int idCalciatore); // Verifica se è già in rosa. Perché: Controllo anti-doppione nel DB[cite: 3].
}