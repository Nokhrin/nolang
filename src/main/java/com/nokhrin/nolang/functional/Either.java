package com.nokhrin.nolang.functional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Either<L, R> permits Either.Left, Either.Right {
    record Left<L, R>(L value) implements Either<L, R> {
        public Left {
            Objects.requireNonNull(value, "Left value cannot be null");
        }

        @Override
        public <T> T fold(
            Function<? super L, ? extends T> onLeft, Function<? super R, ? extends T> onRight) {
            return onLeft.apply(value);
        }
    }

    record Right<L, R>(R value) implements Either<L, R> {

        public Right {
            Objects.requireNonNull(value, "Right value cannot be null");
        }

        @Override
        public <T> T fold(
            Function<? super L, ? extends T> onLeft, Function<? super R, ? extends T> onRight) {
            return onRight.apply(value);
        }
    }

    static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    <T> T fold(Function<? super L, ? extends T> onLeft, Function<? super R, ? extends T> onRight);

    default boolean isLeft() {
        return this instanceof Left<?, ?>;
    }

    default boolean isRight() {
        return this instanceof Right<?, ?>;
    }

    default <T> Either<L, T> map(Function<? super R, ? extends T> onRight) {
        return flatMap(value -> right(onRight.apply(value)));
    }

    default <T> Either<L, T> flatMap(Function<? super R, Either<L, T>> onRight) {
        return fold(Either::left, onRight);
    }

    default Either<L, R> orElse(Either<L, R> other) {
        return fold(leftValue -> other, rightValue -> this);
    }

    default Either<L, R> orElseGet(Supplier<? extends Either<L, R>> supplier) {
        return fold(leftValue -> supplier.get(), rightValue -> this);
    }

    default Optional<L> leftOptional() {
        return fold(Optional::of, rightValue -> Optional.empty());
    }

    default Optional<R> rightOptional() {
        return fold(leftValue -> Optional.empty(), Optional::of);
    }
}
