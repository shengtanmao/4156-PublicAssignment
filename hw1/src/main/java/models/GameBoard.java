package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import utils.DatabaseJdbc;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;

  /**
   * resets the game board fields to null or default values.
   */
  public void reset() {
    p1 = null;
    p2 = null;
    gameStarted = false;
    turn = 0;
    boardState = null;
    winner = 0;
    isDraw = false;
  }

  /**
   * loads the moves for the database into the game board.
   */
  public void loadFromDb(DatabaseJdbc jdbc, Connection con) {
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT * FROM MOVE_TABLE;");
      // if p1 started game, initialize board
      if (rs.next()) {
        initGameBoard((char) rs.getInt("PLAYER_TYPE"));
      } else {
        rs.close();
        stmt.close();
        return;
      }
      if (rs.next()) {
        gameStarted = true;
      } else {
        rs.close();
        stmt.close();
        return;
      }

      while (rs.next()) {
        Move move = new Move(new Player((char) rs.getInt("PLAYER_TYPE"), rs.getInt("PLAYER_ID")),
            rs.getInt("MOVE_X"), rs.getInt("MOVE_Y"));
        this.updateBoard(move);
        this.updateWin(move);
        this.updateDraw();
      }
      rs.close();
      stmt.close();
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
  }

  /**
   * initializes board with player1's char.
   */
  public void initGameBoard(char t1) {
    char t2 = 'X';
    if (t1 == 'X') {
      t2 = 'O';
    }
    p1 = new Player(t1, 1);
    p2 = new Player(t2, 2);
    gameStarted = false;
    turn = 1;
    char[][] ibs = { { '\u0000', '\u0000', '\u0000' }, { '\u0000', '\u0000', '\u0000' },
        { '\u0000', '\u0000', '\u0000' } };
    boardState = ibs;
    winner = 0;
    isDraw = false;
  }

  /**
   * checks if move is valid.
   */
  public Boolean checkMove(Move m) {
    int x = m.getMoveX();
    int y = m.getMoveY();
    return (turn == m.getPlayer().getId() && x >= 0 && x < 3 && y >= 0 && y < 3
        && boardState[x][y] == '\u0000');
  }

  /**
   * update game board with move.
   */
  public void updateBoard(Move m) {
    int x = m.getMoveX();
    int y = m.getMoveY();
    int id = m.getPlayer().getId();
    char t = m.getPlayer().getType();
    boardState[x][y] = t;
    turn = 1;
    if (id == 1) {
      turn = 2;
    }
  }

  /**
   * checks for winner from move. must be ran before updateDraw.
   */
  public void updateWin(Move m) {
    char sym = m.getPlayer().getType();
    int x = m.getMoveX();
    int y = m.getMoveY();
    boolean win = ((boardState[x][(y + 1) % 3] == sym && boardState[x][(y + 2) % 3] == sym)
        || (boardState[(x + 1) % 3][y] == sym && boardState[(x + 2) % 3][y] == sym)
        || (boardState[1][1] != '\u0000' && boardState[0][0] == boardState[1][1]
            && boardState[2][2] == boardState[1][1])
        || (boardState[1][1] != '\u0000' && boardState[0][2] == boardState[1][1]
            && boardState[2][0] == boardState[1][1]));

    if (win) {
      winner = m.getPlayer().getId();
    }
  }

  /**
   * checks for draw. must run updateWin beforehand
   */
  public void updateDraw() {
    if (winner != 0) {
      isDraw = false;
      return;
    }
    boolean full = true;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (boardState[i][j] == '\u0000') {
          full = false;
          break;
        }
      }
    }
    isDraw = full;
  }

  public boolean isGameStarted() {
    return gameStarted;
  }

  public void setGameStarted(boolean gameStarted) {
    this.gameStarted = gameStarted;
  }

  public Player getP1() {
    return p1;
  }

  public Player getP2() {
    return p2;
  }

  public int getTurn() {
    return turn;
  }

  public int getWinner() {
    return winner;
  }

  public boolean isDraw() {
    return isDraw;
  }

  public char getSquare(int x, int y) {
    return boardState[x][y];
  }

  public boolean nullBoard() {
    return boardState == null;
  }

}
