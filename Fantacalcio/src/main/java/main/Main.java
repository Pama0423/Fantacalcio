package main; // Package main di avvio dell'applicazione.

import controller.AstaController; // Importa il Controller della logica di business.
import gui.SchermataPartecipazione; // Importa la schermata di partecipazione ed inserimento squadre.
import javax.swing.SwingUtilities; // Importa SwingUtilities per la gestione thread-safe della GUI.

public class Main { // Classe principale di avvio.
    public static void main(String[] args) { // Metodo main standard di Java.
        SwingUtilities.invokeLater(() -> { // Avvia l'interfaccia grafica sul thread EDT di Swing.
            AstaController controller = new AstaController(); // Istanzia il Controller che pulisce il DB per la nuova asta[cite: 4].
            new SchermataPartecipazione(controller); // Apre direttamente la schermata di inserimento squadre senza login.
        });
    }
}