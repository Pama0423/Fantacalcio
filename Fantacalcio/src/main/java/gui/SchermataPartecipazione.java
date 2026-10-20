package gui; // Package gui (Boundary).

import controller.AstaController; // Importa il Controller per la logica.

import javax.swing.*; // Componenti Swing per l'interfaccia.
import java.awt.*; // Layout e grafica AWT.

public class SchermataPartecipazione extends JFrame { // Schermata per la registrazione delle squadre e dei fantallenatori della lega.

    private final AstaController controller; // Riferimento al Controller.
    private final JTextField txtNomeSquadra = new JTextField("", 15); // Campo di testo VUOTO per il nome della squadra.
    private final JTextField txtUsernameAllenatore = new JTextField("", 15); // Campo di testo VUOTO per il nome del fantallenatore.
    private final DefaultListModel<String> modelListaSquadre = new DefaultListModel<>(); // Modello grafico per mostrare le squadre aggiunte.
    private final JList<String> listSquadre = new JList<>(modelListaSquadre); // Lista a schermo delle squadre inserite.

    public SchermataPartecipazione(AstaController controller) { // Costruttore della schermata.
        this.controller = controller; // Salva il controller.

        setTitle("FantaLab - Creazione Lega e Squadre"); // Titolo della finestra.
        setSize(480, 560); // Dimensioni della finestra.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Chiusura processo Java.
        setLocationRelativeTo(null); // Centra la finestra a schermo.
        setLayout(new BorderLayout(15, 15)); // Layout a zone (NORTH, CENTER, SOUTH).

        // --- ZONA NORD (Input inserimento nuova squadra e fantallenatore) ---
        JPanel pnlNord = new JPanel(); // Pannello superiore.
        pnlNord.setLayout(new BoxLayout(pnlNord, BoxLayout.Y_AXIS)); // Layout verticale.
        pnlNord.setBorder(BorderFactory.createTitledBorder("Aggiungi Nuova Squadra e Allenatore"));

        JLabel lblNomeSquadra = new JLabel("Nome della squadra:"); // Etichetta per il nome squadra.
        lblNomeSquadra.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNomeSquadra.setMaximumSize(new Dimension(300, 30)); // Fissa dimensione campo.
        txtNomeSquadra.setHorizontalAlignment(JTextField.CENTER); // Cursore al centro.

        JLabel lblUsernameAllenatore = new JLabel("Username Fantallenatore:"); // Etichetta per il fantallenatore.
        lblUsernameAllenatore.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsernameAllenatore.setMaximumSize(new Dimension(300, 30)); // Fissa dimensione campo.
        txtUsernameAllenatore.setHorizontalAlignment(JTextField.CENTER); // Cursore al centro.

        JButton btnPartecipa = new JButton("Partecipa"); // Pulsante standard di sistema senza sfondi e senza simboli.
        btnPartecipa.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlNord.add(Box.createVerticalStrut(8));
        pnlNord.add(lblNomeSquadra);
        pnlNord.add(Box.createVerticalStrut(5));
        pnlNord.add(txtNomeSquadra); // Campo nome squadra completamente vuoto.
        pnlNord.add(Box.createVerticalStrut(10));
        pnlNord.add(lblUsernameAllenatore);
        pnlNord.add(Box.createVerticalStrut(5));
        pnlNord.add(txtUsernameAllenatore); // Campo username fantallenatore completamente vuoto.
        pnlNord.add(Box.createVerticalStrut(12));
        pnlNord.add(btnPartecipa); // Bottone Partecipa.
        pnlNord.add(Box.createVerticalStrut(8));

        add(pnlNord, BorderLayout.NORTH);

        // --- ZONA CENTRO (Elenco visivo squadre già inserite) ---
        JPanel pnlCentro = new JPanel(new BorderLayout());
        pnlCentro.setBorder(BorderFactory.createTitledBorder("Squadre Partecipanti nella Lega"));
        listSquadre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pnlCentro.add(new JScrollPane(listSquadre), BorderLayout.CENTER); // Mostra le squadre via via aggiunte.
        add(pnlCentro, BorderLayout.CENTER);

        // --- ZONA SUD (Pulsante Entra in Asta) ---
        JPanel pnlSud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnEntraAsta = new JButton("ENTRA IN ASTA"); // Pulsante standard con sfondo base di sistema.
        btnEntraAsta.setPreferredSize(new Dimension(350, 45));
        btnEntraAsta.setFont(new Font("Arial", Font.BOLD, 15));

        pnlSud.add(btnEntraAsta);
        add(pnlSud, BorderLayout.SOUTH);

        // --- AZIONE PULSANTE PARTECIPA (Registra squadra e fantallenatore nel DB) ---
        btnPartecipa.addActionListener(e -> { // Listener sul click del pulsante Partecipa.
            String nomeTeam = txtNomeSquadra.getText().trim(); // Legge il nome della squadra.
            String usernameAllenatore = txtUsernameAllenatore.getText().trim(); // Legge l'username del fantallenatore.

            String esito = controller.aggiungiSquadraAllaLega(nomeTeam, usernameAllenatore); // Inserisce allenatore e squadra nel DB tramite Controller.

            if (esito.equals("OK")) { // Se l'inserimento sul DB ha avuto successo.
                modelListaSquadre.addElement(nomeTeam + " (Allenatore: " + usernameAllenatore + ")"); // Aggiunge squadra e allenatore alla lista visibile.
                txtNomeSquadra.setText(""); // Svuota il campo squadra.
                txtUsernameAllenatore.setText(""); // Svuota il campo allenatore.
                JOptionPane.showMessageDialog(this, "Squadra '" + nomeTeam + "' con allenatore '" + usernameAllenatore + "' aggiunta con successo alla Lega!");
            } else { // Se i campi erano vuoti o il nome squadra era già esistente.
                JOptionPane.showMessageDialog(this, esito); // Popup di errore.
            }
        });

        // --- AZIONE PULSANTE ENTRA IN ASTA (Passa alla schermata Live) ---
        btnEntraAsta.addActionListener(e -> { // Listener sul pulsante di avvio asta.
            if (controller.getSquadre().size() >= 2) { // Controlla che siano state inserite almeno 2 squadre.
                new SchermataAstaLive(controller); // Apre la plancia d'asta live.
                dispose(); // Chiude la schermata di preparazione.
            } else {
                JOptionPane.showMessageDialog(this, "Inserisci almeno 2 squadre nella lega prima di entrare in asta!"); // Avviso per l'utente.
            }
        });

        setVisible(true); // Rende visibile la schermata.
    }
}