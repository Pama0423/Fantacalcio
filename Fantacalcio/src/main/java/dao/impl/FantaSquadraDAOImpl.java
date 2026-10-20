package dao.impl; // Dichiara il package dao.impl. Perché: Implementazione delle operazioni sulle squadre[cite: 4].

import dao.FantaSquadraDAO; // Importa l'interfaccia FantaSquadraDAO[cite: 4].
import db.DatabaseConnection; // Importa il connettore del DB.
import model.Calciatore; // Importa l'Entity Calciatore[cite: 4].
import model.FantaSquadra; // Importa l'Entity FantaSquadra[cite: 4].

import java.sql.Connection; // Gestione connessione JDBC.
import java.sql.PreparedStatement; // Gestione query parametrizzate.
import java.sql.ResultSet; // Gestione risultati query.
import java.sql.SQLException; // Gestione errori database[cite: 4].
import java.util.ArrayList; // Gestione liste in memoria.
import java.util.HashMap; // Gestione mappe in memoria.
import java.util.List; // Interfaccia lista.
import java.util.Map; // Interfaccia mappa.

public class FantaSquadraDAOImpl implements FantaSquadraDAO { // Implementazione concreta delle query squadre[cite: 4].

    @Override
    public List<FantaSquadra> findAllByLega(int idLega) { // Carica tutte le squadre della lega dal DB.
        List<FantaSquadra> squadre = new ArrayList<>(); // Inizializza la lista vuota.
        String sql = "SELECT * FROM FantaSquadra WHERE id_lega = ? ORDER BY id_squadra ASC"; // Query SQL.
        try (Connection conn = DatabaseConnection.getConnection(); // Connette al DB.
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara la SELECT.
            stmt.setInt(1, idLega); // Inserisce l'id della lega.
            ResultSet rs = stmt.executeQuery(); // Esegue l'interrogazione.
            while (rs.next()) { // Scorre tutte le squadre trovate.
                FantaSquadra sq = new FantaSquadra(); // Istanzia l'Entity[cite: 4].
                sq.setId(rs.getInt("id_squadra")); // Mappa l'ID squadra.
                sq.setNomeSquadra(rs.getString("nomeSquadra")); // Mappa il nome squadra.
                sq.setCreditiResidui(rs.getInt("creditiResidui")); // Mappa il saldo crediti.
                squadre.add(sq); // Aggiunge la squadra alla lista.
            }
        } catch (SQLException e) { // Gestione eccezioni[cite: 4].
            System.err.println("Errore FantaSquadraDAO.findAllByLega: " + e.getMessage());
        }
        return squadre; // Ritorna l'elenco delle squadre partecipanti.
    }

    @Override
    public boolean inserisciNuovaSquadra(String nomeSquadra, String usernameAllenatore, int idLega) { // Inserisce il fantallenatore e la sua nuova squadra nel DB.
        String sqlAllenatore = "INSERT INTO FantaAllenatore (username) VALUES (?) RETURNING id_allenatore"; // Inserisce l'allenatore e restituisce l'ID generato.
        String sqlSquadra = "INSERT INTO FantaSquadra (nomeSquadra, creditiResidui, id_lega, id_allenatore) VALUES (?, 500, ?, ?)"; // Inserisce la squadra con la Foreign Key.

        try (Connection conn = DatabaseConnection.getConnection()) { // Connette al DB PostgreSQL.
            conn.setAutoCommit(false); // Disattiva l'autocommit per eseguire una transazione atomica.

            int idAllenatore = 0;
            try (PreparedStatement stmtAllenatore = conn.prepareStatement(sqlAllenatore)) { // 1. Inserisci Fantallenatore.
                stmtAllenatore.setString(1, usernameAllenatore);
                ResultSet rs = stmtAllenatore.executeQuery();
                if (rs.next()) {
                    idAllenatore = rs.getInt(1); // Recupera l'ID univoco assegnato al fantallenatore.
                }
            }

            try (PreparedStatement stmtSquadra = conn.prepareStatement(sqlSquadra)) { // 2. Inserisci FantaSquadra associata.
                stmtSquadra.setString(1, nomeSquadra);
                stmtSquadra.setInt(2, idLega);
                stmtSquadra.setInt(3, idAllenatore); // Assegna l'ID allenatore come Foreign Key.
                stmtSquadra.executeUpdate();
            }

            conn.commit(); // Conferma entrambe le modifiche nel DB.
            return true; // Ritorna esito positivo.
        } catch (SQLException e) { // Gestione eccezioni SQL[cite: 4].
            System.err.println("Errore inserisciNuovaSquadra: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean aggiungiAllaRosa(int idSquadra, int idCalciatore) { // Inserisce un giocatore nella rosa della squadra.
        String sql = "INSERT INTO Rosa (id_squadra, id_calciatore) VALUES (?, ?)"; // Comando SQL di inserimento.
        try (Connection conn = DatabaseConnection.getConnection(); // Connette al DB PostgreSQL.
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara l'INSERT.
            stmt.setInt(1, idSquadra); // Imposta l'ID squadra.
            stmt.setInt(2, idCalciatore); // Imposta l'ID calciatore.
            stmt.executeUpdate(); // Esegue l'INSERT.
            return true;
        } catch (SQLException e) { // Cattura le violazioni dei Trigger SQL del DB[cite: 3, 4].
            throw new RuntimeException(e.getMessage()); // Rilancia il messaggio del Trigger SQL[cite: 4].
        }
    }

    @Override
    public int getCreditiResidui(int idSquadra) { // Sincronizza i crediti attuali leggendo il valore calcolato dal DB.
        String sql = "SELECT creditiResidui FROM FantaSquadra WHERE id_squadra = ?"; // Query di lettura saldo.
        try (Connection conn = DatabaseConnection.getConnection(); // Connette al DB.
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara il comando.
            stmt.setInt(1, idSquadra); // Filtra per ID squadra.
            ResultSet rs = stmt.executeQuery(); // Esegue la query.
            if (rs.next()) return rs.getInt("creditiResidui"); // Ritorna il valore aggiornato dei crediti.
        } catch (SQLException e) { // Gestione eccezioni[cite: 4].
            System.err.println("Errore FantaSquadraDAO.getCreditiResidui: " + e.getMessage());
        }
        return 0; // Fallback se la lettura fallisce.
    }

    @Override
    public Map<Calciatore, Integer> getRosaConPrezzi(int idSquadra) { // Recupera i calciatori della squadra con il prezzo d'acquisto pagato.
        Map<Calciatore, Integer> mappaRosa = new HashMap<>(); // Inizializza la mappa.
        String sql = "SELECT c.id_calciatore, c.nome, c.ruolo, COALESCE(MAX(t.importoCrediti), 0) AS prezzo " +
                "FROM Rosa r " +
                "JOIN Calciatore c ON r.id_calciatore = c.id_calciatore " +
                "LEFT JOIN Transazione t ON t.id_squadra = r.id_squadra AND t.id_calciatore = r.id_calciatore AND t.tipo_transazione = 'OFFERTA' " +
                "WHERE r.id_squadra = ? " +
                "GROUP BY c.id_calciatore, c.nome, c.ruolo"; // Query JOIN.

        try (Connection conn = DatabaseConnection.getConnection(); // Connette al DB.
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara la SELECT.
            stmt.setInt(1, idSquadra); // Filtra per ID della squadra.
            ResultSet rs = stmt.executeQuery(); // Esegue la query.
            while (rs.next()) { // Scorre ciascun calciatore in rosa.
                Calciatore c = new Calciatore(); // Istanzia l'Entity Calciatore[cite: 4].
                c.setId(rs.getInt("id_calciatore")); // Mappa ID.
                c.setNome(rs.getString("nome")); // Mappa Nome.
                c.setRuolo(rs.getString("ruolo")); // Mappa Ruolo.
                int prezzo = rs.getInt("prezzo"); // Recupera l'importo pagato.
                mappaRosa.put(c, prezzo); // Associa il calciatore al suo prezzo.
            }
        } catch (SQLException e) { // Gestione errori DB[cite: 4].
            System.err.println("Errore FantaSquadraDAO.getRosaConPrezzi: " + e.getMessage());
        }
        return mappaRosa; // Ritorna la mappa completa.
    }

    @Override
    public boolean svincolaCalciatore(int idSquadra, int idCalciatore, int importoRiaccredito) { // Esegue lo svincolo con eliminazione e riaccredito.
        String sqlDeleteRosa = "DELETE FROM Rosa WHERE id_squadra = ? AND id_calciatore = ?"; // Query delete.
        String sqlInsertTransazione = "INSERT INTO Transazione (tipo_transazione, importoCrediti, id_squadra, id_calciatore) VALUES ('SVINCOLO', ?, ?, ?)";
        String sqlUpdateCrediti = "UPDATE FantaSquadra SET creditiResidui = creditiResidui + ? WHERE id_squadra = ?";

        try (Connection conn = DatabaseConnection.getConnection()) { // Connette al DB PostgreSQL.
            conn.setAutoCommit(false); // Disattiva autocommit per atomicità.

            try (PreparedStatement stmt1 = conn.prepareStatement(sqlDeleteRosa)) {
                stmt1.setInt(1, idSquadra);
                stmt1.setInt(2, idCalciatore);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(sqlInsertTransazione)) {
                stmt2.setInt(1, importoRiaccredito);
                stmt2.setInt(2, idSquadra);
                stmt2.setInt(3, idCalciatore);
                stmt2.executeUpdate();
            }

            try (PreparedStatement stmt3 = conn.prepareStatement(sqlUpdateCrediti)) {
                stmt3.setInt(1, importoRiaccredito);
                stmt3.setInt(2, idSquadra);
                stmt3.executeUpdate();
            }

            conn.commit(); // Conferma le modifiche.
            return true;
        } catch (SQLException e) {
            System.err.println("Errore durante lo svincolo nel DB: " + e.getMessage());
            return false;
        }
    }
}