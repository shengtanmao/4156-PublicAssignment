package controllers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;

class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  /**
   * Main method of the application.
   * 
   * @param args Command line arguments
   */
  public static void main(final String[] args) {

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
    // redirect to player 1 page
    app.get("/newgame", ctx -> {
      ctx.redirect("/tictactoe.html");
    });
    GameBoard gb = new GameBoard();
    Gson gson = new Gson();
    // player symbols
    char[] symbols = new char[2];

    // player 1 initializes the game board
    app.post("/startgame", ctx -> {
      char t1 = ctx.body().charAt(5);
      symbols[0] = t1;
      Player p1 = new Player(t1, 1);
      gb.setP1(p1);
      gb.setGameStarted(false);
      gb.setTurn(1);
      char[][] ibs = { { '\u0000', '\u0000', '\u0000' }, { '\u0000', '\u0000', '\u0000' },
          { '\u0000', '\u0000', '\u0000' } };
      gb.setBoardState(ibs);
      gb.setWinner(0);
      gb.setDraw(false);
      ctx.result(gson.toJson(gb));
    });

    // player 2 joins the game
    app.get("/joingame", ctx -> {
      ctx.redirect("/tictactoe.html?p=2");
      char t2 = 'X';
      if (gb.getP1().getType() == 'X') {
        t2 = 'O';
      }
      symbols[1] = t2;
      Player p2 = new Player(t2, 2);
      gb.setP2(p2);
      gb.setGameStarted(true);
      sendGameBoardToAllPlayers(gson.toJson(gb));
    });

    // updates UI after move
    app.post("/move/*", ctx -> {
      int id = ctx.path().charAt(6) - '0';
      String move = ctx.body();
      int x = move.charAt(2) - '0';
      int y = move.charAt(6) - '0';
      Message m = new Message();
      char[][] bs = gb.getBoardState();
      m.setCode(100);
      // check if valid move
      if (bs[x][y] != '\u0000' || gb.getTurn() != id) {
        m.setMoveValidity(false);
        m.setMessage("invalid move");
        ctx.result(gson.toJson(m));
      } else { // updating gameboard
        bs[x][y] = symbols[id - 1];
        gb.setBoardState(bs);
        if (id == 1) {
          gb.setTurn(2);
        } else {
          gb.setTurn(1);
        }
        m.setMoveValidity(true);
        m.setMessage("");
        ctx.result(gson.toJson(m));

        // checking for draw
        boolean full = true;
        for (int i = 0; i < 3; i++) {
          for (int j = 0; j < 3; j++) {
            if (bs[i][j] == '\u0000') {
              full = false;
              break;
            }
          }
        }
        if (full) {
          gb.setDraw(true);
        }

        // checking for winner
        char sym = symbols[id - 1];
        boolean win = ((bs[(x + 1) % 3][y] == sym && bs[(x + 2) % 3][y] == sym)
            || (bs[x][(y + 1) % 3] == sym && bs[x][(y + 2) % 3] == sym)
            || (bs[1][1] != '\u0000' && bs[0][0] == bs[1][1] && bs[2][2] == bs[1][1])
            || (bs[1][1] != '\u0000' && bs[0][2] == bs[1][1] && bs[2][0] == bs[1][1]));

        if (win) {
          gb.setWinner(id);
        }

        sendGameBoardToAllPlayers(gson.toJson(gb));
      }
    });

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }

  /**
   * Send message to all players.
   * 
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        // Add logger here
      }
    }
  }

  public static void stop() {
    app.stop();
  }
}
