package com.nokhrin.nolang.common.core;

public record Arity(int min, int max) {
    public static final int UNBOUNDED = -1;

    public boolean contains(int argsCount) {
        return argsCount >= min && (max == UNBOUNDED || argsCount <= max);
    }

    public Arity(int min) {
        this(min, UNBOUNDED);
    }

    public static Arity exact(int count) {
        return new Arity(count, count);
    }

    public static Arity atLeast(int min) {
        return new Arity(min);
    }

    public static Arity between(int min, int max) {
        return new Arity(min, max);
    }

    public static Arity any() {
        return new Arity(0);
    }

}
