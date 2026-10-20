package gui; // Package gui (Boundary)[cite: 4].

import controller.AstaController; // Importa il Controller della logica di business[cite: 4].
import model.Calciatore; // Importa l'Entity Calciatore[cite: 4].
import model.FantaSquadra; // Importa l'Entity FantaSquadra[cite: 4].

import javax.swing.*; // Libreria Swing per l'interfaccia grafica.
import javax.swing.table.DefaultTableModel; // Tabella dinamica per le rose.
import java.awt.*; // Libreria AWT per i layout.
import java.util.Map; // Gestione mappa calciatore-prezzo.

public class SchermataLega extends JFrame { // Finestra di gestione della Lega con rose al completo e mercato svincoli[cite: 4].

    private final AstaController controller; // Riferimento al Controller[cite: 4].
    private final JTabbedPane tabbedPane = new JTabbedPane(); // Schede a pannelli per ogni fantasquadra.

    public SchermataLega(AstaController controller) { // Costruttore della schermata della Lega.
        this.controller = controller; // Salva il controller[cite: 4].

        setTitle("FantaLab - Pannello Lega e Mercato Svincoli"); // Titolo della finestra.
        setSize(850, 500); // Dimensioni finestra.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Chiusura dell'applicazione.
        setLocationRelativeTo(null); // Centra a schermo.
        setLayout(new BorderLayout(10, 10)); // Layout a zone.

        JLabel lblIntestazione = new JLabel("GESTIONE LEGA - ROSE FINALI E MERCATO SVINCOLI", SwingConstants.CENTER); // Intestazione.
        lblIntestazione.setFont(new Font("SansSerif", Font.BOLD, 18)); // Stile del titolo.
        add(lblIntestazione, BorderLayout.NORTH); // Posiziona in alto.

        costruisciSchedeSquadre(); // Popola le schede per tutte le squadre della lega.
        add(tabbedPane, BorderLayout.CENTER); // Inserisce il tabbed pane al centro.

        setVisible(true); // Rende visibile la finestra.
    }

    private void costruisciSchedeSquadre() { // Popola e aggiorna i pannelli con le rose e i pulsanti di svincolo.
        tabbedPane.removeAll(); // Svuota i pannelli per consentire il refresh dinamico.

        for (FantaSquadra sq : controller.getSquadre()) { // Cicla su tutte le squadre della lega caricate dal DB[cite: 4].
            JPanel panelSquadra = new JPanel(new BorderLayout(5, 5)); // Pannello contenitore per la singola squadra.

            // Riga crediti residui aggiornati dal DB
            JLabel lblCrediti = new JLabel("  Crediti Residui: " + sq.getCreditiResidui() + " cr", SwingConstants.LEFT);
            lblCrediti.setFont(new Font("SansSerif", Font.BOLD, 14));
            panelSquadra.add(lblCrediti, BorderLayout.NORTH);

            // Struttura tabella per la rosa
            String[] colonne = {"Calciatore", "Ruolo", "Prezzo d'Acquisto"};
            DefaultTableModel modelTable = new DefaultTableModel(colonne, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Blocca la modifica diretta delle celle della tabella.
                }
            };

            JTable tabellaRosa = new JTable(modelTable); // Istanzia la tabella grafica.
            Map<Calciatore, Integer> rosaConPrezzi = controller.getRosaConPrezziSquadra(sq); // Recupera i calciatori e i prezzi spesi dal DB[cite: 4].

            // Popola le righe della tabella con la rosa della squadra
            for (Map.Entry<Calciatore, Integer> entry : rosaConPrezzi.entrySet()) {
                Calciatore c = entry.getKey();
                int prezzo = entry.getValue();
                modelTable.addRow(new Object[]{c.getNome(), c.getRuolo(), prezzo + " cr"});
            }

            panelSquadra.add(new JScrollPane(tabellaRosa), BorderLayout.CENTER); // Inserisce la tabella con scroll.

            // Pannello inferiore per la gestione dello svincolo
            JPanel pnlSvincolo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSvincola = new JButton("Svincola Calciatore Selezionato"); // Pulsante per svincolare.


            btnSvincola.addActionListener(e -> { // Ascoltatore per l'azione di svincolo.
                int rigaSelezionata = tabellaRosa.getSelectedRow(); // Legge la riga evidenziata dall'utente.
                if (rigaSelezionata != -1) { // Se è stato selezionato un calciatore.
                    int index = 0;
                    Calciatore calciatoreDaSvincolare = null;
                    int prezzoSpeso = 0;

                    for (Map.Entry<Calciatore, Integer> entry : rosaConPrezzi.entrySet()) {
                        if (index == rigaSelezionata) {
                            calciatoreDaSvincolare = entry.getKey();
                            prezzoSpeso = entry.getValue();
                            break;
                        }
                        index++;
                    }

                    if (calciatoreDaSvincolare != null) {
                        int risposta = JOptionPane.showConfirmDialog(this,
                                "Vuoi svincolare " + calciatoreDaSvincolare.getNome() +
                                        "?\nVerranno riaccreditati " + prezzoSpeso + " cr a " + sq.getNomeSquadra() + ".",
                                "Conferma Svincolo", JOptionPane.YES_NO_OPTION);

                        if (risposta == JOptionPane.YES_OPTION) { // Se l'utente conferma lo svincolo.
                            String esito = controller.svincolaCalciatore(sq, calciatoreDaSvincolare, prezzoSpeso); // Esegue lo svincolo nel DB via Controller[cite: 4].
                            if (esito.equals("OK")) {
                                JOptionPane.showMessageDialog(this, calciatoreDaSvincolare.getNome() + " svincolato con successo! Riaccreditati " + prezzoSpeso + " cr.");
                                costruisciSchedeSquadre(); // Rinfresca istantaneamente le schede e i crediti dal DB.
                            } else {
                                JOptionPane.showMessageDialog(this, esito); // Mostra popup d'errore se la transazione fallisce[cite: 4].
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Seleziona prima un calciatore dalla tabella per poterlo svincolare!"); // Avviso per l'utente[cite: 4].
                }
            });

            pnlSvincolo.add(btnSvincola);
            panelSquadra.add(pnlSvincolo, BorderLayout.SOUTH);

            tabbedPane.addTab(sq.getNomeSquadra(), panelSquadra); // Aggiunge la scheda per la squadra.
        }
    }
}