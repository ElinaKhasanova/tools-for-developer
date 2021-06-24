package ru.kpfu.elina.services;

import ru.kpfu.elina.models.RestResponse;

@FunctionalInterface
public interface ResponseRunnable {

    public void run(RestResponse response);
}
