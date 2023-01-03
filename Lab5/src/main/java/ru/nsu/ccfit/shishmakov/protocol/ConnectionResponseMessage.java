package ru.nsu.ccfit.shishmakov.protocol;

public record ConnectionResponseMessage(byte version,
                                byte reply,
                                byte reserved,
                                byte addressType,
                                byte[] address,
                                byte[] port) {}
