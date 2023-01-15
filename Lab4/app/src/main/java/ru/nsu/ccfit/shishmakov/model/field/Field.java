package ru.nsu.ccfit.shishmakov.model.field;

import ru.nsu.ccfit.shishmakov.proto.SnakesProto;

public interface Field {
    int getSizeX();

    int getSizeY();

    FieldTitle[] getField();

    FieldTitle getTitle(int x, int y);

    FieldTitle getTitle(SnakesProto.GameState.Coord coord);

    SnakesProto.GameState.Snake recalculateSnake(SnakesProto.GameState.Snake snake);

    SnakesProto.GameState.Coord getCoordByDirection(SnakesProto.Direction direction, SnakesProto.GameState.CoordOrBuilder coord);
//    List<FieldTitle> getUpdatedTitles();

}
