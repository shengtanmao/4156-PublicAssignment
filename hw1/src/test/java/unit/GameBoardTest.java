package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import models.GameBoard;
import models.Move;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class GameBoardTest {
  GameBoard gb = new GameBoard();

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
    char[][] ibs = { { '\u0000', '\u0000', '\u0000' }, { '\u0000', '\u0000', '\u0000' },
        { '\u0000', '\u0000', '\u0000' } };
    assertEquals(true, Arrays.deepEquals(ibs, gb.getBoardState()));
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
    char[][] ibs = { { '\u0000', '\u0000', '\u0000' }, { '\u0000', '\u0000', '\u0000' },
        { '\u0000', '\u0000', '\u0000' } };
    assertEquals(true, Arrays.deepEquals(ibs, gb.getBoardState()));
    assertEquals(0, gb.getWinner());
    assertEquals(false, gb.isDraw());
  }

  // test game board update for P1
  @Test
  public void testUpdateP1() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    assertEquals(2, gb.getTurn());
    assertEquals('X', gb.getBoardState()[1][1]);
    // check if turn switches correctly when p2 moves
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    gb.updateBoard(m);
    assertEquals(1, gb.getTurn());
  }

  // test game board update for P2
  @Test
  public void testUpdateP2() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    gb.updateBoard(m);
    assertEquals(1, gb.getTurn());
  }

  // test checkMove for inputs outside of x range
  @Test
  public void testMoveOutsideX() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveY(1);
    m.setMoveX(-1);
    assertEquals(false, gb.checkMove(m));
    m.setMoveX(3);
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for inputs outside of y range
  @Test
  public void testMoveOutsideY() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(-1);
    assertEquals(false, gb.checkMove(m));
    m.setMoveY(3);
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for inputs on occupied space
  @Test
  public void testOccupied() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for player 1 making consecutive moves
  @Test
  public void testConsecutive1() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setMoveY(2);
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for player 2 making consecutive moves
  @Test
  public void testConsecutive2() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveY(2);
    gb.updateBoard(m);
    m.setMoveX(2);
    assertEquals(false, gb.checkMove(m));
  }

  // test checkMove for valid player 1 move
  @Test
  public void testValid1() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    assertEquals(true, gb.checkMove(m));
  }

  // test checkMove for valid player 2 move
  @Test
  public void testValid2() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    assertEquals(true, gb.checkMove(m));
  }

  // test p1 winning on horizontal
  @Test
  public void testHoriz1() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(0);
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
    assertEquals(1, gb.getWinner());
  }

  // test p1 winning on vertical
  @Test
  public void testVert1() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(0);
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
  public void testDiag1() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(0);
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
  public void testRevDiag1() {
    gb.initGameBoard('X');
    Move m = new Move();

    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(0);
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

  // test p2 winning on horizontal
  @Test
  public void testHoriz2() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(2);

    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(2);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(2, gb.getWinner());
  }

  // test p2 winning on vertical
  @Test
  public void testVert2() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(2);

    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(2, gb.getWinner());
  }

  // test p2 winning on diagonal
  @Test
  public void testDiag2() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(2);

    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(2, gb.getWinner());
  }

  // test p1 winning on reverse diagonal
  @Test
  public void testRevDiag2() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(2);
    m.setMoveY(2);

    m.setPlayer(gb.getP2());
    m.setMoveX(2);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(0);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(1);
    gb.updateBoard(m);
    m.setPlayer(gb.getP2());
    m.setMoveX(0);
    m.setMoveY(2);
    gb.updateBoard(m);
    gb.updateWin(m);
    assertEquals(2, gb.getWinner());
  }

  // test draw when a player has won
  @Test
  public void testDrawWin() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(0);
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
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(1);
    m.setMoveY(1);
    gb.updateBoard(m);
    gb.updateWin(m);
    gb.updateDraw();
    assertEquals(false, gb.isDraw());
  }

  // test draw on filled board with no winners
  @Test
  public void testIsDraw() {
    gb.initGameBoard('X');
    Move m = new Move();
    m.setPlayer(gb.getP1());
    m.setMoveX(0);
    m.setMoveY(0);
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