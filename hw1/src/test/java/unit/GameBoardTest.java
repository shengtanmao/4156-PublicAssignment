package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import models.GameBoard;
import models.Move;
import models.Player;
import utils.DatabaseJdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameBoardTest {
  GameBoard gb;
  DatabaseJdbc jdbc;
  Connection con;

  @BeforeEach
  public void setUp() {
    gb = new GameBoard();
    jdbc = new DatabaseJdbc();
    con = jdbc.createConnection();
    jdbc.createTable(con);
  }

  @AfterEach
  public void reset() {
    jdbc.clear(con);
    gb = null;
    jdbc = null;
    con = null;
  }

  // tests game board load in the middle of a game
  @Test
  public void testMidGame() {
    jdbc.addMoveData(con, new Move(new Player('X', 1), -1, -1));
    jdbc.addMoveData(con, new Move(new Player('O', 2), -1, -1));
    jdbc.addMoveData(con, new Move(new Player('X', 1), 0, 0));

    gb.loadFromDb(jdbc, con);
    assertEquals('X', gb.getP1().getType());
    assertEquals('O', gb.getP2().getType());
    assertEquals(1, gb.getP1().getId());
    assertEquals(2, gb.getP2().getId());
    assertEquals(true, gb.isGameStarted());
    assertEquals(2, gb.getTurn());
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (i == 0 && j == 0) {
          assertEquals('X', gb.getSquare(i, j));
        } else {
          assertEquals('\u0000', gb.getSquare(i, j));
        }
      }
    }
    assertEquals(0, gb.getWinner());
    assertEquals(false, gb.isDraw());
  }

  // tests game board load from before p2 joins
  @Test
  public void testBefP2() {
    jdbc.clear(con);
    jdbc.addMoveData(con, new Move(new Player('X', 1), -1, -1));
    gb.loadFromDb(jdbc, con);
    assertEquals('X', gb.getP1().getType());
    assertEquals('O', gb.getP2().getType());
    assertEquals(1, gb.getP1().getId());
    assertEquals(2, gb.getP2().getId());
    assertEquals(false, gb.isGameStarted());
    assertEquals(1, gb.getTurn());
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals('\u0000', gb.getSquare(i, j));
      }
    }
    assertEquals(0, gb.getWinner());
    assertEquals(false, gb.isDraw());
  }

  // test game board load from before p1 joins
  @Test
  public void testEmpty() {
    jdbc.clear(con);
    gb.loadFromDb(jdbc, con);
    assertEquals(null, gb.getP1());
    assertEquals(null, gb.getP2());
    assertEquals(false, gb.isGameStarted());
    assertEquals(0, gb.getTurn());
    assertTrue(gb.nullBoard());
    assertEquals(0, gb.getWinner());
    assertEquals(false, gb.isDraw());
  }

  // tests game board initialization with type O
  @Test
  public void testInitO() {
    gb.initGameBoard('O');
    assertEquals('O', gb.getP1().getType());
    assertEquals('X', gb.getP2().getType());
    assertEquals(1, gb.getP1().getId());
    assertEquals(2, gb.getP2().getId());
    assertEquals(false, gb.isGameStarted());
    assertEquals(1, gb.getTurn());
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals('\u0000', gb.getSquare(i, j));
      }
    }
    assertEquals(0, gb.getWinner());
    assertEquals(false, gb.isDraw());
  }

  // tests game board initialization with type X
  @Test
  public void testInitX() {
    gb.initGameBoard('X');
    assertEquals('X', gb.getP1().getType());
    assertEquals('O', gb.getP2().getType());
    assertEquals(1, gb.getP1().getId());
    assertEquals(2, gb.getP2().getId());
    assertEquals(false, gb.isGameStarted());
    assertEquals(1, gb.getTurn());
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals('\u0000', gb.getSquare(i, j));
      }
    }
    assertEquals(0, gb.getWinner());
    assertEquals(false, gb.isDraw());
  }

  // test game board update for P1
  @Test
  public void testUpdateP1() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 1, 1);
    gb.updateBoard(m);
    assertEquals(2, gb.getTurn());
    assertEquals('X', gb.getSquare(1, 1));
  }

  // test game board update for P2
  @Test
  public void testUpdateP2() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 1, 1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    gb.updateBoard(m);
    assertEquals(1, gb.getTurn());
    assertEquals('O', gb.getSquare(2, 1));
  }

  // test checkMove for inputs outside of x range
  @Test
  public void testMoveOutsideX() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), -1, 1);
    assertEquals(false, gb.checkMove(m));
    m.setMoveX(3);
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for inputs outside of y range
  @Test
  public void testMoveOutsideY() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 1, -1);
    assertEquals(false, gb.checkMove(m));
    m.setMoveY(3);
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for inputs on occupied space
  @Test
  public void testOccupied() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 1, 1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for player 1 making consecutive moves
  @Test
  public void testConsecutive() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 1, 1);
    gb.updateBoard(m);
    m.setMoveY(2);
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for valid player 1 move
  @Test
  public void testValid() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 1, 1);
    assertEquals(true, gb.checkMove(m));
  }

  // test no winner
  @Test
  public void noWinner() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 0, 0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());
  }

  // test p1 winning on horizontal
  @Test
  public void testHoriz() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 0, 1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(2);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(1);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(1, gb.getWinner());
  }

  // test p1 winning on vertical
  @Test
  public void testVert() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 0, 0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(1, gb.getWinner());
  }

  // test p1 winning on diagonal
  @Test
  public void testDiag() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 0, 0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(1, gb.getWinner());
  }

  // test p1 winning on reverse diagonal
  @Test
  public void testRevDiag() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 2, 0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(1, gb.getWinner());
  }

  // test draw when a player has won
  @Test
  public void testDrawWin() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 0, 0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(2);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    gb.updateWin(m);
    gb.updateDraw();
    assertEquals(false, gb.isDraw());
  }

  // test draw on unfilled board
  @Test
  public void testUnfilled() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 1, 1);
    gb.updateBoard(m);
    gb.updateWin(m);
    gb.updateDraw();
    assertEquals(false, gb.isDraw());
  }

  // test draw on filled board with no winners
  @Test
  public void testIsDraw() {
    gb.initGameBoard('X');
    Move m = new Move(gb.getP1(), 0, 0);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(1);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(0, gb.getWinner());

    gb.updateDraw();
    assertEquals(true, gb.isDraw());
  }

}
