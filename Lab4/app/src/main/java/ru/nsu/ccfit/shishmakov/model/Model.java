package ru.nsu.ccfit.shishmakov.model;

import ru.nsu.ccfit.shishmakov.model.field.Field;
import ru.nsu.ccfit.shishmakov.proto.SnakesProto;

public interface Model {

    void applyGameStateMsg(SnakesProto.GameState gameState);

    void applyConfig(SnakesProto.GameConfig config);

    SnakesProto.GameMessage applyJoinMsg(SnakesProto.GameMessage joinMsg, int playerId);

    void applySteerMsg(SnakesProto.GameMessage message, Integer playerId);

    void applyChangeRoleMsg(SnakesProto.GameMessage message);

    void kickPlayer(int id);
    Field getField();

    SnakesProto.GameState getCurState();

    void makeSelfJoin(int playerId);

    void startServerWithNewGame();
    void startServerWithExistedGame(int oldMaster, int newMaster);
    void turnOffServerIfRun();

}
