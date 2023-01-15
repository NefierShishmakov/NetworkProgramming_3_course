package ru.nsu.ccfit.shishmakov.model;

import ru.nsu.ccfit.shishmakov.proto.SnakesProto;
import lombok.Getter;

import java.util.HashMap;


@Getter
public class PlayersInfo {

    public record PlayerWithSnake(SnakesProto.GamePlayer player, SnakesProto.GameState.Snake snake) {}
    private final HashMap<Integer, SnakesProto.GamePlayer> players;
    private final HashMap<Integer, SnakesProto.GameState.Snake> snakes;
    private int aliveSnakesNum = 0;

    public PlayersInfo(){
        this.players = new HashMap<>();
        this.snakes = new HashMap<>();
    }

    public void clean(){
        this.aliveSnakesNum = 0;
        this.players.clear();
        this.snakes.clear();
    }

     public void putPlayer(SnakesProto.GamePlayer player){
         this.players.put(player.getId(), player);
     }

     public void putPlayer(Integer id,SnakesProto.GamePlayer player){
         this.players.put(id, player);
     }
     public void putSnake(SnakesProto.GameState.Snake snake) {this.snakes.put(snake.getPlayerId(), snake);}

     public void putSnake(Integer id, SnakesProto.GameState.Snake snake) {
         this.snakes.put(id, snake);
     }

     public SnakesProto.GameState.Snake getSnake(Integer id) {
        return this.snakes.get(id);
     }
     public SnakesProto.GamePlayer getPlayer(Integer id) {
        return this.players.get(id);
     }

     public void removePlayerAndMakeSnakeZombie(Integer id) {
        if (this.players.remove(id) != null) {
            SnakesProto.GameState.Snake snake = snakes.remove(id);
            if(snake != null){
                this.aliveSnakesNum--;
                this.snakes.put(id, SnakesProto.GameState.Snake.newBuilder(snake).setState(SnakesProto.GameState.Snake.SnakeState.ZOMBIE).build());
            }
        }
     }

     public void updatePlayersAndSnakes(SnakesProto.GameStateOrBuilder gameState){
        this.updateSnakes(gameState);
        this.updatePlayers(gameState);
     }

    public void updateSnakes(SnakesProto.GameStateOrBuilder gameState){
        this.aliveSnakesNum = 0;
        this.snakes.clear();
        for (SnakesProto.GameState.Snake snake : gameState.getSnakesList()){
            this.snakes.put(snake.getPlayerId(), snake);
            if (snake.getState() != SnakesProto.GameState.Snake.SnakeState.ZOMBIE){
                ++this.aliveSnakesNum;
            }
        }
    }

    private void updatePlayers(SnakesProto.GameStateOrBuilder gameState) {
        this.players.clear();
        for (SnakesProto.GamePlayer gamePlayer : gameState.getPlayers().getPlayersList()) {
            this.players.put(gamePlayer.getId(), gamePlayer);
        }
    }

}
