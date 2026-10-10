package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.core.EvalError;
import com.nokhrin.nolang.common.values.NumericValue;

public class Numeric {

    private Numeric() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Eval<NumericValue> pos(NumericValue numericValue) {
        return Eval.pure(numericValue);
    }

    public static Eval<NumericValue> neg(NumericValue numericValue) {
        return switch (numericValue) {
            case NumericValue.IntValue value -> {
                try {
                    yield Eval.pure(new NumericValue.IntValue(Math.negateExact(value.number())));
                } catch (ArithmeticException e) {
                    yield Eval.raiseError(new EvalError.ArithmeticError("Integer overflow"));
                }
            }
            case NumericValue.RealValue value ->
                    Eval.pure(new NumericValue.RealValue(-value.number()));
        };
    }

    public static Eval<NumericValue> abs(NumericValue numericValue) {
        return switch (numericValue) {
            case NumericValue.IntValue value -> {
                if (value.number() == Long.MIN_VALUE) {
                    yield Eval.raiseError(new EvalError.ArithmeticError("Integer overflow"));
                }
                yield Eval.pure(new NumericValue.IntValue(Math.abs(value.number())));
            }
            case NumericValue.RealValue value ->
                    Eval.pure(new NumericValue.RealValue(Math.abs(value.number())));
        };
    }

    public static Eval<NumericValue> sin(NumericValue numericValue) {
        return switch (numericValue) {
            case NumericValue.IntValue value ->
                    Eval.pure(new NumericValue.RealValue(Math.sin(value.number())));
            case NumericValue.RealValue value ->
                    Eval.pure(new NumericValue.RealValue(Math.sin(value.number())));
        };
    }

    public static Eval<NumericValue> add(NumericValue left, NumericValue right) {
        return switch (left) {
            case NumericValue.IntValue l ->
                    switch (right) {
                        case NumericValue.IntValue r -> {
                            try {
                                yield Eval.pure(
                                        new NumericValue.IntValue(
                                                Math.addExact(l.number(), r.number())));
                            } catch (ArithmeticException e) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Integer overflow"));
                            }
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() + r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };

            case NumericValue.RealValue l ->
                    switch (right) {
                        case NumericValue.IntValue r -> {
                            double result = l.number() + r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() + r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };
        };
    }

    public static Eval<NumericValue> sub(NumericValue left, NumericValue right) {
        return switch (left) {
            case NumericValue.IntValue l ->
                    switch (right) {
                        case NumericValue.IntValue r -> {
                            try {
                                yield Eval.pure(
                                        new NumericValue.IntValue(
                                                Math.subtractExact(l.number(), r.number())));
                            } catch (ArithmeticException e) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Integer overflow"));
                            }
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() - r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };

            case NumericValue.RealValue l ->
                    switch (right) {
                        case NumericValue.IntValue r -> {
                            double result = l.number() - r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() - r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };
        };
    }

    public static Eval<NumericValue> mul(NumericValue left, NumericValue right) {
        return switch (left) {
            case NumericValue.IntValue l ->
                    switch (right) {
                        case NumericValue.IntValue r -> {
                            try {
                                yield Eval.pure(
                                        new NumericValue.IntValue(
                                                Math.multiplyExact(l.number(), r.number())));
                            } catch (ArithmeticException e) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Integer overflow"));
                            }
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() * r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };

            case NumericValue.RealValue l ->
                    switch (right) {
                        case NumericValue.IntValue r -> {
                            double result = l.number() * r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() * r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };
        };
    }

    public static Eval<NumericValue> percent(NumericValue number) {
        return switch (number) {
            case NumericValue.IntValue(long n) -> Eval.pure(new NumericValue.RealValue(n / 100.0));
            case NumericValue.RealValue(double n) ->
                    Eval.pure(new NumericValue.RealValue(n / 100.0));
        };
    }

    public static Eval<NumericValue> div(NumericValue dividend, NumericValue divisor) {
        double divisorValue =
                switch (divisor) {
                    case NumericValue.IntValue intDivisor -> intDivisor.number();
                    case NumericValue.RealValue realDivisor -> realDivisor.number();
                };

        if (divisorValue == 0.0) {
            return Eval.raiseError(new EvalError.ArithmeticError("Division by zero"));
        }

        return switch (dividend) {
            case NumericValue.IntValue l ->
                    switch (divisor) {
                        case NumericValue.IntValue r -> {
                            double result = l.number() / r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() / r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };

            case NumericValue.RealValue l ->
                    switch (divisor) {
                        case NumericValue.IntValue r -> {
                            double result = l.number() / r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                        case NumericValue.RealValue r -> {
                            double result = l.number() / r.number();
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };
        };
    }

    public static Eval<NumericValue> pow(NumericValue base, NumericValue exponent) {
        double baseValue =
                switch (base) {
                    case NumericValue.IntValue l -> l.number();
                    case NumericValue.RealValue l -> l.number();
                };
        double exponentValue =
                switch (exponent) {
                    case NumericValue.IntValue r -> r.number();
                    case NumericValue.RealValue r -> r.number();
                };

        if (baseValue == 0.0 && exponentValue < 0.0) {
            return Eval.raiseError(
                    new EvalError.ArithmeticError("Division by zero in power operation"));
        }

        boolean exponentIsInteger =
                Double.isFinite(exponentValue) && exponentValue == Math.floor(exponentValue);
        if (baseValue < 0.0 && !exponentIsInteger) {
            return Eval.raiseError(
                    new EvalError.ArithmeticError(
                            "Power of negative base with non-integer exponent is not defined"));
        }

        return switch (base) {
            case NumericValue.IntValue b ->
                    switch (exponent) {
                        case NumericValue.IntValue ex -> {
                            if (ex.number() < 0) {
                                yield Eval.pure(
                                        new NumericValue.RealValue(
                                                Math.pow(b.number(), ex.number())));
                            }
                            long result = 1;
                            try {

                                for (long i = 0; i < ex.number(); i++) {
                                    result = Math.multiplyExact(result, b.number());
                                }
                            } catch (ArithmeticException e) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Integer overflow"));
                            }
                            yield Eval.pure(new NumericValue.IntValue(result));
                        }
                        case NumericValue.RealValue ex -> {
                            double result = Math.pow(b.number(), ex.number());
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };

            case NumericValue.RealValue b ->
                    switch (exponent) {
                        case NumericValue.IntValue ex -> {
                            double result = Math.pow(b.number(), ex.number());
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }

                        case NumericValue.RealValue ex -> {
                            double result = Math.pow(b.number(), ex.number());
                            if (Double.isInfinite(result)) {
                                yield Eval.raiseError(
                                        new EvalError.ArithmeticError("Real overflow"));
                            }
                            yield Eval.pure(new NumericValue.RealValue(result));
                        }
                    };
        };
    }

    public static Eval<NumericValue> fact(NumericValue numericValue) {
        return switch (numericValue) {
            case NumericValue.IntValue(long n) -> {
                if (n < 0) {
                    yield Eval.raiseError(
                            new EvalError.ArithmeticError("Factorial of negative number"));
                }
                long result = 1;
                try {
                    for (long i = 2; i <= n; i++) {
                        result = Math.multiplyExact(result, i);
                    }
                } catch (ArithmeticException e) {
                    yield Eval.raiseError(new EvalError.ArithmeticError("Integer overflow"));
                }
                yield Eval.pure(new NumericValue.IntValue(result));
            }
            case NumericValue.RealValue(double n) -> {
                if (n != Math.floor(n)) {
                    yield Eval.raiseError(
                            new EvalError.ArithmeticError("Factorial of non-integer"));
                }
                if (n < 0) {
                    yield Eval.raiseError(
                            new EvalError.ArithmeticError("Factorial of negative number"));
                }
                long result = 1;
                try {
                    for (long i = 2; i <= n; i++) {
                        result = Math.multiplyExact(result, i);
                    }
                } catch (ArithmeticException e) {
                    yield Eval.raiseError(new EvalError.ArithmeticError("Integer overflow"));
                }
                yield Eval.pure(new NumericValue.IntValue(result));
            }
        };
    }
}
