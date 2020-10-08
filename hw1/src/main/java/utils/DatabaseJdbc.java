package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import models.Move;

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
   * creates new table named MOVE_TABLE.
   * 
   * @return true if created successfully, false otherwise
   */
  public boolean createTable(Connection con) {
    Statement stmt = null;

    try {
      stmt = con.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS MOVE_TABLE " + "(PLAYER_ID INT NOT NULL, "
          + "PLAYER_TYPE INT NOT NULL, " + " MOVE_X INT NOT NULL, " + " MOVE_Y INT NOT NULL, "
          + "UNIQUE (PLAYER_ID, MOVE_X, MOVE_Y))";
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (Exception e) {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      return false;
    }
    // System.out.println("Table created successfully");
    return true;
  }

  /**
   * adds move data to the database table. adding a move with (-1,-1) means the player has
   * started/joined the game
   * 
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
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      return false;
    }
    // System.out.println("Records created successfully");
    return true;
  }

  /**
   * clears the table entries but keeps the table.
   * 
   * @return true if cleared successfully, false otherwise
   */
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
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      return false;
    }
    return true;
  }

  /**
   * finds the number of entries in MOVE_TABLE.
   * 
   * @return number of entries, also returns 0 if MOVE_TABLE doesn't exist
   */
  public int size(Connection con) {
    Statement stmt = null;
    ResultSet rs = null;
    int size = 0;

    try {
      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT * FROM MOVE_TABLE;");
      while (rs.next()) {
        size++;
      }
      stmt.close();
      rs.close();
    } catch (SQLException e) {
      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException e2) {
        e2.printStackTrace();
      }
      e.printStackTrace();
    }
    return size;
  }

}
