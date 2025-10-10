package com.smartorder.orderservice.application.exception;

public class MixedCurrenciesNotAllowedException extends RuntimeException {

    public MixedCurrenciesNotAllowedException() {
        super("Mixed currencies not allowed");
    }
}

