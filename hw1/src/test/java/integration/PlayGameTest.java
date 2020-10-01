package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import controllers.PlayGame;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class PlayGameTest {

  // start server
  @BeforeAll
  public static void init() {
    PlayGame.main(null);
    System.out.println("started server");
  }

  // empty method
  @BeforeEach
  public void beforeEach() {
  }

  // A player cannot make a move until both players have joined the game.
  @Test
  @Order(1)
  public void startTest() {
    HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=X")
        .asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("gameStarted"));

    System.out.println("test: game does not start before player 2 joins");
    // System.out.println("Start Game Response: " + responseBody);
  }

  // After game has started Player 1 always makes the first move.
  @Test
  @Order(2)
  public void firstMove() {
    Unirest.get("http://localhost:8080/joingame").asString();
    HttpResponse<String> response = Unirest.get("http://localhost:8080/gameboard").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(true, jsonObject.get("gameStarted"));
    assertEquals(1, jsonObject.get("turn"));
    System.out.println("test: player 1 makes the first move");
    // System.out.println("First Move Response: " + responseBody);
  }

  // A player cannot make two moves in their turn
  @Test
  @Order(3)
  public void twoMoves() {
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=1")
        .asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("moveValidity"));
    System.out.println("test: cannot make two moves in a roll");
    // System.out.println(responseBody);
  }

  // A player should be able to win a game.
  @Test
  @Order(4)
  public void testWin() {
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    HttpResponse<String> response = Unirest.get("http://localhost:8080/gameboard").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(1, jsonObject.get("winner"));
    System.out.println("test: a player can win the game");
    // System.out.println(responseBody);
  }

  // A game should be a draw if all the positions are exhausted and no one has won.
  @Test
  @Order(5)
  public void testDraw() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=2").asString();
    HttpResponse<String> response = Unirest.get("http://localhost:8080/gameboard").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(true, jsonObject.get("isDraw"));
    System.out.println("test: the game draws when board is full and no one has won");
    // System.out.println(responseBody);
  }

  @AfterEach
  public void afterEach() {
  }

  // stop server
  @AfterAll
  public static void close() {
    PlayGame.stop();
    System.out.println("closed server");
  }

}
