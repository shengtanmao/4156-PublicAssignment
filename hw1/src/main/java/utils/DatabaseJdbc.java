package utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.Move;
import models.Player;

public class DatabaseJdbc {

  /**
   * create new connection.
   * 
   * @return Connection object
   */
  public Connection createConnection() {
    Connection c = null;

    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:ase.db");
    } catch (Exception e) {
      // System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return c;
    }
    // System.out.println("Opened database successfully");
    return c;
  }

  /**
   * create new table
   * 
   * @param con    Connection object
   * @param string table name
   * @return true if created successfully, false otherwise
   */
  public boolean createTable(Connection con) {
    Statement stmt = null;

    try {
      stmt = con.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS MOVE_TABLE " + "(PLAYER_ID INT NOT NULL, "
          + "PLAYER_TYPE INT NOT NULL, " + " MOVE_X INT NOT NULL, " + " MOVE_Y INT NOT NULL)";
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (Exception e) {
      // System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    }
    // System.out.println("Table created successfully");
    return true;
  }

  /**
   * adds move data to the database table. adding a move with (-1,-1) means the player has
   * started/joined the game
   * 
   * @param con  Connection object
   * @param move Move object containing data
   * @return true if data added successfully, false otherwise
   */
  public boolean addMoveData(Connection con, Move move) {
    Statement stmt = null;

    try {
      con.setAutoCommit(false);
      // System.out.println((int) move.getPlayer().getType());

      stmt = con.createStatement();

      String sql = "INSERT INTO MOVE_TABLE (PLAYER_ID,PLAYER_TYPE,MOVE_X,MOVE_Y) " + "VALUES ("
          + move.getPlayer().getId() + ", " + ((int) move.getPlayer().getType()) + ", "
          + move.getMoveX() + ", " + move.getMoveY() + " );";
      stmt.executeUpdate(sql);

      stmt.close();
      con.commit();
    } catch (Exception e) {
      // System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    }
    // System.out.println("Records created successfully");
    return true;
  }

  public boolean clear(Connection con) {
    Statement stmt = null;

    try {
      con.setAutoCommit(false);
      // System.out.println("Opened database successfully");

      stmt = con.createStatement();
      String sql = "DELETE FROM MOVE_TABLE;";
      stmt.executeUpdate(sql);
      con.commit();

      stmt.close();
    } catch (Exception e) {
      // System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * 
   * @param con
   * @return true if the table is empty
   * @throws SQLException
   */
  public int size(Connection con) throws SQLException {
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM MOVE_TABLE;");
    int size = 0;
    while (rs.next()) {
      size++;
    }
    return size;
  }

}
