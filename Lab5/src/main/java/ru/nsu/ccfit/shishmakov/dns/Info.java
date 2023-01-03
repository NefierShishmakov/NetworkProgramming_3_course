package ru.nsu.ccfit.shishmakov.dns;

import java.nio.channels.SelectionKey;

public record Info(SelectionKey key, int port) {}
