package controllers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import utils.DatabaseJdbc;

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

    DatabaseJdbc jdbc = new DatabaseJdbc();
    Connection con = jdbc.createConnection();
    jdbc.createTable(con);

    GameBoard gb = new GameBoard();
    Gson gson = new Gson();
    try {
      if (jdbc.size(con) > 0) {
        gb.loadFromDb(jdbc, con);
        sendGameBoardToAllPlayers(gson.toJson(gb));
      }
    } catch (SQLException e) {
      System.err.println("SQLException");
      e.printStackTrace();
    }

    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });
    // redirect to player 1 page
    // clears the database
    app.get("/newgame", ctx -> {
      ctx.redirect("/tictactoe.html");
      gb.reset();
      jdbc.clear(con);
    });

    // get game board
    app.get("/gameboard", ctx -> {
      ctx.result(gson.toJson(gb));
    });

    // player 1 initializes the game board
    app.post("/startgame", ctx -> {
      char t1 = ctx.body().charAt(5);
      gb.initGameBoard(t1);
      ctx.result(gson.toJson(gb));

      jdbc.clear(con);
      jdbc.addMoveData(con, new Move(gb.getP1(), -1, -1));
    });

    // player 2 joins the game
    app.get("/joingame", ctx -> {
      ctx.redirect("/tictactoe.html?p=2");
      gb.setGameStarted(true);
      sendGameBoardToAllPlayers(gson.toJson(gb));

      jdbc.addMoveData(con, new Move(gb.getP2(), -1, -1));
    });

    // updates UI after move
    app.post("/move/*", ctx -> {
      // setting move model
      // System.out.println(ctx.path());
      int id = ctx.path().charAt(6) - '0';
      String move = ctx.body();
      // System.out.println(ctx.body());
      Move mv;
      if (id == 1) {
        mv = new Move(gb.getP1(), move.charAt(2) - '0', move.charAt(6) - '0');
      } else {
        mv = new Move(gb.getP2(), move.charAt(2) - '0', move.charAt(6) - '0');
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
        gb.updateWin(mv);
        gb.updateDraw();
        sendGameBoardToAllPlayers(gson.toJson(gb));

        jdbc.addMoveData(con, mv);
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
