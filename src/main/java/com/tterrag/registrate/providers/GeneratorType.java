package com.tterrag.registrate.providers;

public interface GeneratorType<T> {

    static <T> GeneratorType<T> of(String name) {
        return new GeneratorType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
    }
}
