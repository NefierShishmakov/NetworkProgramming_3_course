package ru.nsu.ccfit.shishmakov.model.field;

import ru.nsu.ccfit.shishmakov.proto.SnakesProto;

import java.util.ArrayList;
import java.util.List;

public class GameField implements Field {
    private final FieldTitle[] field;
    private final int sizeX;
    private final int sizeY;
    //private final ArrayList<FieldTitle> dirtyTitles;

    private final List<FieldTitle> dirtyTitles;

    public GameField(int sizeX, int sizeY){
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.field = new FieldTitle[sizeX * sizeY];
        for (int i = 0; i < sizeX * sizeY; i++) {
            this.field[i] = new FieldTitle();
        }

        this.dirtyTitles = new ArrayList<>();
    }

    public void updateField(SnakesProto.GameStateOrBuilder gameState){
        cleanDirtyTitlesListIfNotEmpty();

        for (SnakesProto.GameState.Coord foodCoord : gameState.getFoodsList()){
            this.dirtyTitles.add(getTitle(foodCoord.getX(), foodCoord.getY()).makeFood());
        }

        for (SnakesProto.GameState.Snake snake : gameState.getSnakesList()){
            putSnakeOnField(snake);
        }

    }

    public void cleanField(){
        this.dirtyTitles.clear();
        for(FieldTitle title : this.field){
            title.makeEmpty();
        }
    }
    private void putSnakeOnField(SnakesProto.GameState.Snake snake){
        boolean isHead = true;
        int prevX = 0;
        int prevY = 0;
        int playerId = snake.getPlayerId();

        for (SnakesProto.GameState.Coord pointCoord : snake.getPointsList()){
            if(isHead){
                isHead = false;
                prevX = pointCoord.getX();
                prevY = pointCoord.getY();
            } else {
                prevX += pointCoord.getX();
                prevY += pointCoord.getY();
            }
            this.dirtyTitles.add(getTitle(prevX,prevY).makeSnake(playerId));
        }
    }

    @Override
    public FieldTitle getTitle(int x, int y) {
        if(x < 0 || y < 0 || x >= this.sizeX || y >= this.sizeY){
            throw new RuntimeException("Invalid coords: x:" + x + ", y:" + y);
        }

        return this.field[y * this.sizeX + x];
    }

    @Override
    public FieldTitle getTitle(SnakesProto.GameState.Coord coord) {
        int x = coord.getX();
        int y = coord.getY();
        if(x < 0 || y < 0 || x >= this.sizeX || y >= this.sizeY){
            throw new RuntimeException("Invalid coords: x:" + x + ", y:" + y);
        }

        return this.field[y * this.sizeX+ x];
    }

    private void cleanDirtyTitlesListIfNotEmpty(){
        if (!this.dirtyTitles.isEmpty())
        {
            for (FieldTitle title : this.dirtyTitles)
            {
                title.makeEmpty();
            }
            this.dirtyTitles.clear();
        }
    }

    @Override
    public SnakesProto.GameState.Snake recalculateSnake(SnakesProto.GameState.Snake snake){
        SnakesProto.GameState.Snake.Builder builder = SnakesProto.GameState.Snake.newBuilder(snake);

        SnakesProto.GameState.Coord currHead = snake.getPoints(0);
        SnakesProto.GameState.Coord newHead = getCoordByDirection(snake.getHeadDirection(), currHead);

        if (getTitle(newHead.getX(), newHead.getY()).isFood()){
            builder.addPoints(0, newHead);
            builder.setPoints(1, getCordDiff(currHead, newHead));
        } else {
            builder.setPoints(0, newHead);
            builder.setPoints(1, getCordDiff(currHead, newHead));
            for (int ind = snake.getPointsCount() - 1; ind > 1; ind--){
                builder.setPoints(ind, snake.getPoints(ind - 1));
            }
        }

        return builder.build();
    }

    private SnakesProto.GameState.Coord getCordDiff(SnakesProto.GameState.Coord currHead,
                                                    SnakesProto.GameState.Coord newHead)
    {
        SnakesProto.GameState.Coord.Builder coordBuilder = SnakesProto.GameState.Coord.newBuilder();
        coordBuilder.setX(currHead.getX() - newHead.getX());
        coordBuilder.setY(currHead.getY() - newHead.getY());
        return coordBuilder.build();
    }
    public SnakesProto.GameState.Coord getCoordByDirection(SnakesProto.Direction direction, SnakesProto.GameState.CoordOrBuilder coord){
        SnakesProto.GameState.Coord.Builder builder = SnakesProto.GameState.Coord.newBuilder();

        int bottomFieldCoordinate = this.sizeY - 1;
        int rightFieldCoordinate = this.sizeX - 1;

        switch (direction){
            case UP -> {
                if(coord.getY() == 0){
                    builder.setY(bottomFieldCoordinate);
                } else {
                    builder.setY(coord.getY() - 1);
                }
                builder.setX(coord.getX());
            }
            case DOWN -> {
                if(coord.getY() == bottomFieldCoordinate){
                    builder.setY(0);
                } else {
                    builder.setY(coord.getY() + 1);
                }
                builder.setX(coord.getX());
            }
            case LEFT -> {
                if(coord.getX() == 0){
                    builder.setX(rightFieldCoordinate);
                } else {
                    builder.setX(coord.getX() - 1);
                }
                builder.setY(coord.getY());
            }
            case RIGHT -> {
                if(coord.getX() == rightFieldCoordinate){
                    builder.setX(0);
                } else {
                    builder.setX(coord.getX() + 1);
                }
                builder.setY(coord.getY());
            }
        }

        return builder.build();
    }
    @Override
    public int getSizeX() {
        return this.sizeX;
    }

    @Override
    public int getSizeY() {
        return this.sizeY;
    }

    @Override
    public FieldTitle[] getField() {
        return this.field;
    }

}
