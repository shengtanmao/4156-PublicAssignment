package models;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;

  /**
   * initializes board with player1's char.
   */
  public void initGameBoard(char t1) {
    char t2 = 'X';
    if (t1 == 'X') {
      t2 = 'O';
    }
    Player player1 = new Player(t1, 1);
    Player player2 = new Player(t2, 2);
    setP1(player1);
    setP2(player2);
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
    return (x >= 0 && x < 3 && y >= 0 && y < 3 && boardState[x][y] == '\u0000'
        && turn == m.getPlayer().getId());
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
   * checks for draw.
   */
  public void checkDraw() {
    boolean full = true;
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (boardState[i][j] == '\u0000') {
          full = false;
          break;
        }
      }
    }
    if (full && winner == 0) {
      isDraw = true;
    }
  }

  /**
   * checks for winner from move.
   */
  public void checkWin(Move m) {
    char sym = m.getPlayer().getType();
    int x = m.getMoveX();
    int y = m.getMoveY();
    boolean win = ((boardState[(x + 1) % 3][y] == sym && boardState[(x + 2) % 3][y] == sym)
        || (boardState[x][(y + 1) % 3] == sym && boardState[x][(y + 2) % 3] == sym)
        || (boardState[1][1] != '\u0000' && boardState[0][0] == boardState[1][1]
            && boardState[2][2] == boardState[1][1])
        || (boardState[1][1] != '\u0000' && boardState[0][2] == boardState[1][1]
            && boardState[2][0] == boardState[1][1]));

    if (win) {
      winner = m.getPlayer().getId();
    }
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

  public void setP1(Player p1) {
    this.p1 = p1;
  }

  public Player getP2() {
    return p2;
  }

  public void setP2(Player p2) {
    this.p2 = p2;
  }
}
