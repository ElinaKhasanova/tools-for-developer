package ru.kpfu.elina.services;

@FunctionalInterface
public interface ExceptionRunnable {

    public void run(Exception e);
}
