package com.patres.homeoffice.exception;

import java.util.function.Function;

public class UncheckedExceptionHandler {

    @FunctionalInterface
    public interface FunctionWithExceptions<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    public static <T, R, E extends Exception> Function<T, R> handle(final FunctionWithExceptions<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception exception) {
                throw new ApplicationException(exception);
            }
        };
    }

}