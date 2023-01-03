package ru.nsu.ccfit.shishmakov.protocol;

public record InitRequestMessage(byte version, byte nmethods, byte[] methods) {}