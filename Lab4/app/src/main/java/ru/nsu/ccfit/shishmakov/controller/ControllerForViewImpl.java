package ru.nsu.ccfit.shishmakov.controller;

import ru.nsu.ccfit.shishmakov.model.Model;
import ru.nsu.ccfit.shishmakov.network.NetworkHandler;
import ru.nsu.ccfit.shishmakov.proto.SnakesProto;
import javafx.collections.ObservableList;

public class ControllerForViewImpl implements ControllerForView {

    private Model model;

    private NetworkHandler networkHandler;

    public void init(Model model, NetworkHandler networkHandler){
        this.model = model;
        this.networkHandler = networkHandler;
    }

    @Override
    public void moveUp() {
        this.networkHandler.sendRotation(SnakesProto.Direction.UP);
    }

    @Override
    public void moveDown() {
        this.networkHandler.sendRotation(SnakesProto.Direction.DOWN);
    }

    @Override
    public void moveLeft() {
        this.networkHandler.sendRotation(SnakesProto.Direction.LEFT);
    }

    @Override
    public void moveRight() {
        this.networkHandler.sendRotation(SnakesProto.Direction.RIGHT);
    }

    @Override
    public SnakesProto.GameMessage connectToGame(String gameName) {
        return this.networkHandler.connectToGame(gameName);
    }

    @Override
    public void askServerForGames(String ip, int port) {
        this.networkHandler.askServerForGames(ip, port);
    }

    @Override
    public void changeRoleToView(){
        this.networkHandler.sendChangeRoleToViewToMaster();
    }

    @Override
    public void disconnect(){
        this.model.turnOffServerIfRun();
        this.networkHandler.disconnect();
    }
    @Override
    public void turnOffApp() {
        this.model.turnOffServerIfRun();
        this.networkHandler.turnOff();
    }

    @Override
    public void startListeningForGames(ObservableList<String> listForGames) {
        this.networkHandler.startListeningForGames(listForGames);
    }

    @Override
    public void stopListeningForGames() {
        this.networkHandler.stopListeningForGames();
    }
    @Override
    public void updateConfig(SnakesProto.GameConfig gameConfig) {
        this.model.applyConfig(gameConfig);
    }
    @Override
    public void startServerModeAndPlay() {
        this.model.startServerWithNewGame();
        this.networkHandler.hostNewGame();
    }

}
