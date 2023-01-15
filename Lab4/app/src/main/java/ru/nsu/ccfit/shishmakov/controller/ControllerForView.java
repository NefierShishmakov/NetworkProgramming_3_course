package ru.nsu.ccfit.shishmakov.controller;

import ru.nsu.ccfit.shishmakov.proto.SnakesProto;
import javafx.collections.ObservableList;

public interface ControllerForView {
    void moveUp();
    void moveDown();
    void moveLeft();
    void moveRight();

    void startServerModeAndPlay();

    SnakesProto.GameMessage connectToGame(String gameName);
    void askServerForGames(String ip, int port);

    void disconnect();

    void changeRoleToView();

    void turnOffApp();

    void startListeningForGames(ObservableList<String> listForGames);

    void stopListeningForGames();
    void updateConfig(SnakesProto.GameConfig gameConfig);
}
