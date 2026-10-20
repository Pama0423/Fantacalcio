package dao; // Dichiara il package dao. Perché: Interfaccia per le transazioni di mercato[cite: 4].

import model.OffertaAsta; // Importa l'Entity OffertaAsta. Perché: Dati del rilancio da salvare.

public interface TransazioneDAO { // Interfaccia DAO per i rilanci/transazioni. Perché: Gestisce il salvataggio dei rilanci[cite: 4].
    boolean registraOfferta(OffertaAsta offerta); // Salva l'offerta nella tabella Transazione. Perché: Innesca i Trigger SQL[cite: 3].
}