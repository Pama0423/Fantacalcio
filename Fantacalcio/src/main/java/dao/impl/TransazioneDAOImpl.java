package dao.impl; // Dichiara il package dao.impl. Perché: Implementazione delle query per le transazioni[cite: 4].

import dao.TransazioneDAO; // Importa l'interfaccia DAO.
import db.DatabaseConnection; // Importa il connettore DB.
import model.OffertaAsta; // Importa l'Entity OffertaAsta.

import java.sql.Connection; // Connessione JDBC.
import java.sql.PreparedStatement; // Query sicure parametrizzate.
import java.sql.SQLException; // Gestione errori DB[cite: 4].

public class TransazioneDAOImpl implements TransazioneDAO { // Classe di implementazione della persistenza rilanci[cite: 4].

    @Override
    public boolean registraOfferta(OffertaAsta offerta) { // Salva ogni rilancio nella tabella Transazione.
        String sql = "INSERT INTO Transazione (tipo_transazione, importoCrediti, isVincente, id_squadra, id_calciatore, id_asta) " +
                "VALUES ('OFFERTA', ?, ?, ?, ?, ?)"; // Comando SQL per inserire il rilancio.
        try (Connection conn = DatabaseConnection.getConnection(); // Connette al DB PostgreSQL.
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Prepara la query di INSERT.
            stmt.setInt(1, offerta.getImportoCrediti()); // Assegna il prezzo offerto.
            stmt.setBoolean(2, offerta.isVincente()); // Assegna lo stato vincente.
            stmt.setInt(3, offerta.getSquadra().getId()); // Assegna la squadra offerente.
            stmt.setInt(4, offerta.getCalciatore().getId()); // Assegna il calciatore conteso.
            stmt.setInt(5, offerta.getAsta().getId()); // Assegna la sessione d'asta.
            stmt.executeUpdate(); // Esegue l'INSERT (Qui scatta il Trigger Check_Rilancio sul DB!).
            return true; // Rilancio salvato con successo.
        } catch (SQLException e) { // Intercetta la violazione del Trigger SQL se l'offerta è troppo bassa[cite: 3, 4].
            throw new RuntimeException(e.getMessage()); // Propaga il messaggio del Trigger all'interfaccia[cite: 4].
        }
    }
}