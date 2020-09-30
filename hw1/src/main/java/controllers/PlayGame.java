package controllers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import org.eclipse.jetty.websocket.api.Session;

public class PlayGame {

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

    // get game board
    app.get("/gameboard", ctx -> {
      ctx.result(gson.toJson(gb));
    });

    // player 1 initializes the game board
    app.post("/startgame", ctx -> {
      char t1 = ctx.body().charAt(5);
      gb.initGameBoard(t1);
      ctx.result(gson.toJson(gb));
    });

    // player 2 joins the game
    app.get("/joingame", ctx -> {
      ctx.redirect("/tictactoe.html?p=2");
      gb.setGameStarted(true);
      sendGameBoardToAllPlayers(gson.toJson(gb));
    });

    // updates UI after move
    app.post("/move/*", ctx -> {
      // setting move model
      // System.out.println(ctx.path());
      int id = ctx.path().charAt(6) - '0';
      String move = ctx.body();
      // System.out.println(ctx.body());
      Move mv = new Move();
      mv.setMoveX(move.charAt(2) - '0');
      mv.setMoveY(move.charAt(6) - '0');
      if (id == 1) {
        mv.setPlayer(gb.getP1());
      } else {
        mv.setPlayer(gb.getP2());
      }

      Message ms = new Message();
      ms.setCode(100);
      // check if valid move
      if (!gb.checkMove(mv)) {
        ms.setMoveValidity(false);
        ms.setMessage("invalid move");
        ctx.result(gson.toJson(ms));
      } else {
        gb.updateBoard(mv);
        ms.setMoveValidity(true);
        ms.setMessage("");
        ctx.result(gson.toJson(ms));

        // checking for win and draw
        gb.checkWin(mv);
        gb.checkDraw();
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
