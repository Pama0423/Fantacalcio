package gui; // Package gui (Boundary).

import controller.AstaController; // Importa il Controller della logica di business.
import model.Calciatore; // Importa l'Entity Calciatore.
import model.FantaSquadra; // Importa l'Entity FantaSquadra.

import javax.swing.*; // Libreria Swing per l'interfaccia grafica.
import java.awt.*; // Libreria AWT per la gestione dei colori e dei layout.
import java.util.HashMap; // Importa HashMap per associare ogni squadra alla sua plancia grafica.
import java.util.Map; // Importa l'interfaccia Map.

public class SchermataAstaLive extends JFrame { // Finestra principale dell'asta live in tempo reale.

    private final AstaController controller; // Riferimento al Controller per la logica.
    private final JLabel lblGiocatore = new JLabel("Nessun calciatore in asta", SwingConstants.CENTER); // Nome calciatore chiamante.
    private final JLabel lblPrezzo = new JLabel("Offerta attuale: 0 cr", SwingConstants.CENTER); // Prezzo raggiunto dall'asta.
    private final JLabel lblTimer = new JLabel("2", SwingConstants.CENTER); // Etichetta per il conto alla rovescia di 2s.
    private final JLabel lblVincitore = new JLabel("Nessun offerente", SwingConstants.CENTER); // Leader temporaneo dell'asta.
    private final JPanel pnlCrediti = new JPanel(new GridLayout(0, 1)); // Pannello laterale per la classifica crediti.
    private final Map<String, PlanciaSquadra> mappePlanceSquadre = new HashMap<>(); // Mappa per tracciare le plance delle squadre.

    private Timer timerSwing; // Oggetto Timer di Swing per scandire i secondi.
    private int secondiRimanenti = 2; // Variabile per tracciare il tempo (impostato a 2s).

    public SchermataAstaLive(AstaController controller) { // Costruttore della plancia d'asta.
        this.controller = controller; // Salva il controller ricevuto.

        setTitle("Asta Live Fantacalcio"); // Titolo della plancia.
        setSize(1000, 650); // Dimensioni proporzionate per ospitare le plance squadra.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Chiusura dell'app alla chiusura della finestra.
        setLocationRelativeTo(null); // Centra la finestra a schermo.
        setLayout(new BorderLayout(10, 10)); // Layout a zone (NORTH, CENTER, WEST, SOUTH).

        // --- ZONA NORD (Barra di Ricerca Calciatore nel DB) ---
        JPanel pnlNord = new JPanel(new FlowLayout()); // Pannello orizzontale per l'input.
        JTextField txtNome = new JTextField(12); // Campo di testo dove scrivere il nome del calciatore da chiamare.
        JButton btnChiama = new JButton("Chiama Calciatore"); // Pulsante per cercare e chiamare il calciatore.
        pnlNord.add(new JLabel("Nome Calciatore:")); // Aggiunge l'etichetta.
        pnlNord.add(txtNome); // Aggiunge il campo di testo.
        pnlNord.add(btnChiama); // Aggiunge il pulsante di chiamata.
        add(pnlNord, BorderLayout.NORTH); // Posiziona la barra in alto nella finestra.

        // --- ZONA CENTRO (Plancia Asta con Pulsanti di Dimensioni Ridotte) ---
        JPanel pnlCentro = new JPanel(new GridBagLayout()); // GridBagLayout per evitare lo stiramento dei pulsanti.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE; // Impedisce ai componenti di espandersi a tutto schermo.

        lblGiocatore.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTimer.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblPrezzo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblVincitore.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton btnAvvia = new JButton("AVVIA ASTA (2s)"); // Pulsante di avvio.
        btnAvvia.setPreferredSize(new Dimension(180, 32)); // Dimensione contenuta per il pulsante.

        JButton btnFineAsta = new JButton("FINE ASTA"); // Pulsante fine asta.
        btnFineAsta.setPreferredSize(new Dimension(180, 32)); // Dimensione contenuta per il pulsante.

        gbc.gridy = 0; pnlCentro.add(lblGiocatore, gbc);
        gbc.gridy = 1; pnlCentro.add(lblPrezzo, gbc);
        gbc.gridy = 2; pnlCentro.add(lblVincitore, gbc);
        gbc.gridy = 3; pnlCentro.add(lblTimer, gbc);
        gbc.gridy = 4; pnlCentro.add(btnAvvia, gbc);
        gbc.gridy = 5; pnlCentro.add(btnFineAsta, gbc);

        add(pnlCentro, BorderLayout.CENTER); // Posiziona il blocco al centro della finestra.

        // --- ZONA OVEST (Classifica Crediti Sincronizzata col DB) ---
        pnlCrediti.setBorder(BorderFactory.createTitledBorder("Crediti Squadre")); // Bordo con titolo completo per la sezione crediti.
        pnlCrediti.setPreferredSize(new Dimension(170, 0)); // Fissa la larghezza del pannello sinistro per non tagliare il testo.
        aggiornaVistaCrediti(); // Popola l'elenco delle squadre con i crediti attuali.
        add(pnlCrediti, BorderLayout.WEST); // Posiziona il pannello a sinistra.

        // --- ZONA SUD (Buzzer Rilanci e Plance Squadre) ---
        JPanel pnlSudContenitore = new JPanel(new BorderLayout(5, 5)); // Contenitore per i buzzer e le plance delle rose.
        JPanel pnlBuzzer = new JPanel(new FlowLayout()); // Pannello orizzontale per i pulsanti di rilancio.

        int numSquadre = controller.getSquadre().size(); // Conta le squadre della lega.
        JPanel pnlPlance = new JPanel(new GridLayout(1, numSquadre, 5, 0)); // Griglia orizzontale per le plance delle rose.

        for (FantaSquadra sq : controller.getSquadre()) { // Cicla su tutte le squadre caricate dal DB.
            JButton btnBuzzer = new JButton("+1 " + sq.getNomeSquadra()); // Crea un pulsante per ciascuna squadra.
            btnBuzzer.addActionListener(e -> { // Ascoltatore sul click del buzzer.
                String esito = controller.gestisciRilancio(sq, 1); // Tenta di registrare il rilancio sul DB tramite il Controller.
                if (esito.equals("OK")) { // Se il DB approva il rilancio.
                    lblPrezzo.setText("Offerta attuale: " + controller.getPrezzoAttuale() + " cr"); // Aggiorna il prezzo a schermo.
                    lblVincitore.setText("Sta vincendo: " + sq.getNomeSquadra()); // Aggiorna la squadra leader dell'asta.
                    secondiRimanenti = 2; // Reset del timer a 2 secondi a ogni rilancio valido.
                    lblTimer.setText("2"); // Fornisce riscontro visivo immediato all'utente.
                } else { // Se il Trigger SQL rifiuta il rilancio.
                    JOptionPane.showMessageDialog(this, esito); // Mostra popup spiegando il motivo del blocco.
                }
            });
            pnlBuzzer.add(btnBuzzer); // Aggiunge il pulsante della squadra in basso.

            PlanciaSquadra plancia = new PlanciaSquadra(sq.getNomeSquadra()); // Istanzia la plancia della rosa per la squadra.
            mappePlanceSquadre.put(sq.getNomeSquadra(), plancia); // Registra la plancia nella mappa.
            pnlPlance.add(new JScrollPane(plancia)); // Aggiunge la plancia con scorrimento.
        }

        pnlSudContenitore.add(pnlBuzzer, BorderLayout.NORTH); // Posiziona i buzzer sopra le plance.
        pnlSudContenitore.add(pnlPlance, BorderLayout.CENTER); // Posiziona le plance al centro del pannello sud.
        pnlSudContenitore.setPreferredSize(new Dimension(1000, 220)); // Fissa un'altezza adeguata al pannello inferiore.
        add(pnlSudContenitore, BorderLayout.SOUTH); // Posiziona il contenitore in basso nella finestra.

        // --- EVENTO CHIAMA CALCIATORE ---
        btnChiama.addActionListener(e -> { // Ascoltatore sul pulsante Chiama.
            String ris = controller.chiamaCalciatore(txtNome.getText()); // Invoca la verifica sul DB tramite il Controller.
            if (ris.equals("OK")) { // Se il giocatore esiste ed è libero.
                Calciatore c = controller.getCalciatoreCorrente(); // Recupera l'Entity trovata dal DB.
                lblGiocatore.setText(c.getNome() + " (" + c.getRuolo() + ")"); // Aggiorna la GUI con il nome del calciatore.
                lblPrezzo.setText("Offerta base: 0 cr"); // Azzera l'offerta visibile.
                lblVincitore.setText("Nessun offerente"); // Azzera il vincitore visibile.
            } else { // Se il giocatore non esiste o è già in rosa.
                JOptionPane.showMessageDialog(this, ris); // Mostra il messaggio d'errore all'utente.
            }
        });

        // --- EVENTO AVVIA ASTA ---
        btnAvvia.addActionListener(e -> { // Ascoltatore sul pulsante Start.
            if (controller.getCalciatoreCorrente() != null) { // Controlla che vi sia un calciatore pronto.
                secondiRimanenti = 2; // Imposta il conto alla rovescia a 2 secondi.
                lblTimer.setText("2"); // Aggiorna il testo a schermo.
                if (timerSwing != null) timerSwing.stop(); // Ferma eventuali timer precedenti.
                timerSwing.start(); // Avvia il conto alla rovescia.
            } else { // Se non è stato chiamato un calciatore dal DB.
                JOptionPane.showMessageDialog(this, "Prima devi chiamare un calciatore valido dal database!"); // Avviso per l'utente.
            }
        });

        // --- EVENTO FINE ASTA (Apre la schermata della Lega solo se le rose sono complete) ---
        btnFineAsta.addActionListener(e -> { // Ascoltatore sul pulsante FINE ASTA.
            if (!controller.sonoTutteLeRoseComplete()) { // Controllo bloccante sulle rose.
                JOptionPane.showMessageDialog(this,
                        "Impossibile terminare l'asta: non tutte le squadre hanno completato la rosa!",
                        "Asta Incompleta",
                        JOptionPane.PLAIN_MESSAGE); // Messaggio pulito senza icona d'avviso.
                return; // Interrompe il flusso.
            }

            int conferma = JOptionPane.showConfirmDialog(this,
                    "Vuoi terminare l'asta e passare alla vista complessiva della Lega?",
                    "Conferma Fine Asta",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE); // Dialogo di conferma pulito.

            if (conferma == JOptionPane.YES_OPTION) {
                if (timerSwing != null) timerSwing.stop(); // Ferma il timer se attivo.
                new SchermataLega(controller); // Apre la finestra della Lega.
                dispose(); // Chiude la finestra dell'asta live.
            }
        });

        // --- LOOP DEL TIMER (Scade ogni 1 secondo) ---
        timerSwing = new Timer(1000, e -> { // Istanzia il timer di Swing regolato a 1000ms.
            if (secondiRimanenti > 0) { // Se il tempo non è ancora esaurito.
                secondiRimanenti--; // Decrementa di 1 secondo.
                lblTimer.setText(String.valueOf(secondiRimanenti)); // Aggiorna l'etichetta visiva a schermo.
            } else { // TEMPO SCADUTO (0 secondi raggiunti!)
                timerSwing.stop(); // Ferma il timer.
                boolean assegnato = controller.assegnaCalciatoreAllaScadenza(); // Salva l'acquisto nel DB scatenando i Trigger SQL.
                if (assegnato) { // Se l'inserimento sul DB ha avuto successo.
                    FantaSquadra vincitore = controller.getUltimoOfferente(); // Recupera la squadra vincente.
                    Calciatore c = controller.getCalciatoreCorrente(); // Recupera il calciatore acquistato.

                    JOptionPane.showMessageDialog(this, "Acquistato da " + vincitore.getNomeSquadra() +
                            " per " + controller.getPrezzoAttuale() + " cr!"); // Mostra popup di conferma.

                    aggiornaVistaCrediti(); // Aggiorna l'elenco dei crediti ricaricando il saldo aggiornato dal DB.

                    PlanciaSquadra planciaVincente = mappePlanceSquadre.get(vincitore.getNomeSquadra()); // Recupera la plancia della squadra vincente.
                    if (planciaVincente != null) {
                        planciaVincente.aggiungiGiocatore(c, controller.getPrezzoAttuale()); // Aggiunge visivamente il calciatore alla plancia.
                    }
                } else { // Se non ci sono state offerte o il DB ha bloccato l'operazione.
                    JOptionPane.showMessageDialog(this, "Nessuna offerta valida: calciatore INVENDUTO."); // Popup di avviso.
                }
                lblGiocatore.setText("Nessun calciatore in asta"); // Pulisce la label del calciatore per il turno successivo.
                txtNome.setText(""); // Svuota la barra di ricerca.
            }
        });

        setVisible(true); // Rende visibile la schermata d'asta live.
    }

    private void aggiornaVistaCrediti() { // Metodo per ridisegnare la colonna dei crediti aggiornati.
        pnlCrediti.removeAll(); // Svuota l'elenco precedente per evitare sovrapposizioni.
        for (FantaSquadra sq : controller.getSquadre()) { // Scorre le squadre caricate dal Controller.
            pnlCrediti.add(new JLabel(sq.getNomeSquadra() + ": " + sq.getCreditiResidui() + " cr")); // Aggiunge la riga aggiornata col saldo.
        }
        pnlCrediti.revalidate(); // Ricalcola la geometria del pannello Swing.
        pnlCrediti.repaint(); // Ridisegna i pixel aggiornati a schermo.
    }

    // CLASSE INTERNA: Gestisce la plancia e i 4 reparti per squadra
    class PlanciaSquadra extends JPanel {
        private int countPor = 0, countDif = 0, countCen = 0, countAtt = 0; // Contatori numerici per reparto.

        private JLabel lblPor, lblDif, lblCen, lblAtt; // Etichette per le sezioni dei reparti.
        private JPanel pnlPor, pnlDif, pnlCen, pnlAtt; // Mini-pannelli dei singoli ruoli.

        public PlanciaSquadra(String nomeSquadra) { // Costruttore della plancia di rosa.
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Disposizione verticale degli elementi.
            setBorder(BorderFactory.createTitledBorder(nomeSquadra.toUpperCase())); // Bordo con il nome della squadra.

            pnlPor = creaReparto(); // Crea il blocco Portieri.
            lblPor = new JLabel(" PORTIERI (0/1)"); // Etichetta iniziale per portieri (0/1 max).
            pnlPor.add(lblPor); // Aggiunge l'etichetta al blocco.
            add(pnlPor); // Aggiunge il blocco portieri alla colonna.

            pnlDif = creaReparto(); // Crea il blocco Difensori.
            lblDif = new JLabel(" DIFENSORI (0/2)"); // Etichetta iniziale per difensori (0/2 max).
            pnlDif.add(lblDif); // Aggiunge l'etichetta al blocco.
            add(pnlDif); // Aggiunge il blocco difensori alla colonna.

            pnlCen = creaReparto(); // Crea il blocco Centrocampisti.
            lblCen = new JLabel(" CENTROCAMPISTI (0/2)"); // Etichetta iniziale per centrocampisti (0/2 max).
            pnlCen.add(lblCen); // Aggiunge l'etichetta al blocco.
            add(pnlCen); // Aggiunge il blocco centrocampisti alla colonna.

            pnlAtt = creaReparto(); // Crea il blocco Attaccanti.
            lblAtt = new JLabel(" ATTACCANTI (0/1)"); // Etichetta iniziale per attaccanti (0/1 max).
            pnlAtt.add(lblAtt); // Aggiunge l'etichetta al blocco.
            add(pnlAtt); // Aggiunge il blocco attaccanti alla colonna.
        }

        private JPanel creaReparto() { // Metodo ausiliario per creare i sotto-pannelli di ruolo.
            JPanel p = new JPanel(); // Istanzia un nuovo pannello.
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); // Imposta layout verticale per incolonnare i giocatori.
            return p; // Ritorna il pannello pronto.
        }

        public void aggiungiGiocatore(Calciatore c, int prezzo) { // Aggiunge un giocatore acquistato alla plancia della squadra.
            JLabel lblMembro = new JLabel(" - " + c.getNome() + " (" + prezzo + " cr)"); // Crea la riga col calciatore e prezzo.

            switch (c.getRuolo().toUpperCase()) { // Seleziona il ruolo.
                case "POR":
                    countPor++; // Incrementa contatore portieri.
                    lblPor.setText(" PORTIERI (" + countPor + "/1)"); // Aggiorna il contatore visivo a schermo.
                    pnlPor.add(lblMembro); // Aggiunge il nome nel reparto portieri.
                    break;
                case "DIF":
                    countDif++; // Incrementa contatore difensori.
                    lblDif.setText(" DIFENSORI (" + countDif + "/2)"); // Aggiorna il contatore visivo.
                    pnlDif.add(lblMembro); // Aggiunge il nome nel reparto difensori.
                    break;
                case "CEN":
                    countCen++; // Incrementa contatore centrocampisti.
                    lblCen.setText(" CENTROCAMPISTI (" + countCen + "/2)"); // Aggiorna il contatore visivo.
                    pnlCen.add(lblMembro); // Aggiunge il nome nel reparto centrocampisti.
                    break;
                case "ATT":
                    countAtt++; // Incrementa contatore attaccanti.
                    lblAtt.setText(" ATTACCANTI (" + countAtt + "/1)"); // Aggiorna il contatore visivo.
                    pnlAtt.add(lblMembro); // Aggiunge il nome nel reparto attaccanti.
                    break;
            }
            revalidate(); // Ricalcola la geometria del pannello.
            repaint(); // Ridisegna i pixel a schermo.
        }
    }
}