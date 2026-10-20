package dao.impl; // Dichiara il package dao.impl. Perché: Contiene le implementazioni concrete delle interfacce DAO[cite: 4].

import dao.CalciatoreDAO; // Importa l'interfaccia implementata. Perché: Rispetta il contratto DAO.
import db.DatabaseConnection; // Importa il gestore della connessione. Perché: Apre la sessione SQL.
import model.Calciatore; // Importa l'Entity Calciatore. Perché: Istanzia gli oggetti letti dal DB.

import java.sql.Connection; // Importa la connessione JDBC. Perché: Esegue i comandi sul DB.
import java.sql.PreparedStatement; // Importa PreparedStatement. Perché: Sicurezza contro la SQL Injection.
import java.sql.ResultSet; // Importa ResultSet. Perché: Contiene i risultati delle SELECT SQL.
import java.sql.SQLException; // Importa SQLException. Perché: Intercetta gli errori del database[cite: 4].

public class CalciatoreDAOImpl implements CalciatoreDAO { // Implementazione concreta per PostgreSQL. Perché: Esegue le query SQL reali[cite: 4].

    @Override
    public Calciatore findByNome(String nome) { // Cerca un giocatore nel DB. Perché: Permette la chiamata dal listone.
        String sql = "SELECT * FROM Calciatore WHERE UPPER(nome) LIKE UPPER(?) LIMIT 1"; // Query SQL case-insensitive. Perché: Trova il giocatore indipendentemente dal testo.
        try (Connection conn = DatabaseConnection.getConnection(); // Apre la connessione. Perché: Avvia il canale SQL.
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara la query. Perché: Esecuzione ottimizzata e sicura.
            stmt.setString(1, "%" + nome.trim() + "%"); // Assegna il parametro nome. Perché: Pulisce gli spazi vuoti.
            ResultSet rs = stmt.executeQuery(); // Esegue la SELECT. Perché: Recupera i dati dal DB.
            if (rs.next()) { // Se trova un record corrispondente.
                Calciatore c = new Calciatore(); // Crea un nuovo oggetto Calciatore. Perché: Popola l'Entity.
                c.setId(rs.getInt("id_calciatore")); // Imposta la chiave primaria. Perché: Identifica il giocatore.
                c.setNome(rs.getString("nome")); // Imposta il nome. Perché: Salva l'anagrafica.
                c.setRuolo(rs.getString("ruolo")); // Imposta il ruolo. Perché: Salva il ruolo (POR, DIF, CEN, ATT).
                return c; // Ritorna l'oggetto popolato. Perché: Lo passa al Controller.
            }
        } catch (SQLException e) { // Intercetta gli errori SQL[cite: 4].
            System.err.println("Errore CalciatoreDAO.findByNome: " + e.getMessage()); // Registra il log di errore.
        }
        return null; // Ritorna null se il giocatore non esiste nel DB.
    }

    @Override
    public boolean isGiocatoreInRosa(int idCalciatore) { // Verifica la presenza nella tabella Rosa. Perché: Evita acquisti doppi.
        String sql = "SELECT COUNT(*) FROM Rosa WHERE id_calciatore = ?"; // Query di conteggio. Perché: Verifica se è già associato a una squadra.
        try (Connection conn = DatabaseConnection.getConnection(); // Apre la connessione JDBC.
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara il comando SQL.
            stmt.setInt(1, idCalciatore); // Passa l'ID del giocatore.
            ResultSet rs = stmt.executeQuery(); // Esegue la lettura.
            if (rs.next()) { // Se la lettura va a buon fine.
                return rs.getInt(1) > 0; // Ritorna true se il conteggio è maggiore di 0. Perché: Il giocatore è già stato comprato.
            }
        } catch (SQLException e) { // Gestione delle eccezioni[cite: 4].
            System.err.println("Errore CalciatoreDAO.isGiocatoreInRosa: " + e.getMessage());
        }
        return false; // Ritorna false se il giocatore è svincolato/libero.
    }
}