package ru.nsu.ccfit.shishmakov.view;

import ru.nsu.ccfit.shishmakov.model.field.Field;
import ru.nsu.ccfit.shishmakov.proto.SnakesProto;

public interface View {
    void updateView(SnakesProto.GamePlayers players);
    void recreateField(Field field);
}
