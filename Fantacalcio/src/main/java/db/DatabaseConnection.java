package db; // Dichiara il package db. Perché: Isolamento della gestione della connessione[cite: 4].

import java.sql.Connection; // Importa l'interfaccia Connection. Perché: Gestisce la sessione SQL verso il database.
import java.sql.DriverManager; // Importa la classe DriverManager. Perché: Crea la connessione JDBC fisica tramite URL.
import java.sql.SQLException; // Importa la classe SQLException. Perché: Gestisce gli errori di rete o credenziali del DB[cite: 4].

public class DatabaseConnection { // Classe ausiliaria Singleton/Statica per la connessione. Perché: Centralizza i parametri del DB.

    private static final String URL = "jdbc:postgresql://localhost:5432/fantalab_db"; // URL di connessione a PostgreSQL. Perché: Indica indirizzo e nome del database.
    private static final String USER = "postgres"; // Nome utente del DB. Perché: Autenticazione sul DBMS.
    private static final String PASSWORD = "admin"; // Password del DB. Perché: Accesso protetto al database locale.

    public static Connection getConnection() throws SQLException { // Metodo statico per ottenere la connessione. Perché: Permette alle classi DAO di aprire la comunicazione.
        return DriverManager.getConnection(URL, USER, PASSWORD); // Ritorna l'oggetto Connection. Perché: Stabilisce la sessione SQL col DB.
    }
}