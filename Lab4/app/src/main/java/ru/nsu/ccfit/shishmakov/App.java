package ru.nsu.ccfit.shishmakov;

import ru.nsu.ccfit.shishmakov.controller.ControllerForViewImpl;
import ru.nsu.ccfit.shishmakov.model.GameModel;
import ru.nsu.ccfit.shishmakov.model.entities.Config;
import ru.nsu.ccfit.shishmakov.network.NetworkLogic;
import ru.nsu.ccfit.shishmakov.proto.SnakesProto;
import ru.nsu.ccfit.shishmakov.view.javafxgui.JavaFXView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) {
        ControllerForViewImpl controller = new ControllerForViewImpl();
        NetworkLogic networkLogic = new NetworkLogic();
        JavaFXView view = new JavaFXView();
        GameModel model = new GameModel(view, networkLogic, Config.getInstance());
        networkLogic.init(model);
        controller.init(model, networkLogic);

        Thread thread = new Thread( () -> view.startGui(controller));
        thread.start();

        try {
            Thread.sleep(250);
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.recreateField(model.getField());
        model.applyConfig(SnakesProto.GameConfig.newBuilder().setHeight(25).setWidth(25).setFoodStatic(1).setStateDelayMs(300).build());

        }

}