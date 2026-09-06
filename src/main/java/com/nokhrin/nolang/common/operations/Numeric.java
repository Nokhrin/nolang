package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;
import com.nokhrin.nolang.common.values.Value;
import com.nokhrin.nolang.functional.Eval;
import com.nokhrin.nolang.functional.EvalError;

public class Numeric {

    private Numeric() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Eval<NumericValue> pos(NumericValue numericValue) {
        return Eval.pure(numericValue);
    }

    public static Eval<NumericValue> neg(NumericValue numericValue) {
        return switch (numericValue) {
            case IntValue value -> Eval.pure(new IntValue(-value.number()));
            case RealValue value -> Eval.pure(new RealValue(-value.number()));
        };
    }

    public static Eval<NumericValue> abs(NumericValue numericValue) {
        return switch (numericValue) {
            case IntValue value -> Eval.pure(new IntValue(Math.abs(value.number())));
            case RealValue value -> Eval.pure(new RealValue(Math.abs(value.number())));
        };
    }

    public static Eval<NumericValue> add(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> Eval.pure(new IntValue(l.number() + r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() + r.number()));
            };

            case RealValue l -> switch (right) {
                case IntValue r -> Eval.pure(new RealValue(l.number() + r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() + r.number()));
            };
        };
    }

    public static Eval<NumericValue> sub(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> Eval.pure(new IntValue(l.number() - r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() - r.number()));
            };

            case RealValue l -> switch (right) {
                case IntValue r -> Eval.pure(new RealValue(l.number() - r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() - r.number()));
            };
        };
    }

    public static Eval<NumericValue> mul(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> Eval.pure(new IntValue(l.number() * r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() * r.number()));
            };

            case RealValue l -> switch (right) {
                case IntValue r -> Eval.pure(new RealValue(l.number() * r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() * r.number()));
            };
        };
    }


    public static Eval<NumericValue> percent(NumericValue number) {
        return switch (number) {
            case IntValue(long n) -> Eval.pure(new RealValue(n / 100.0));
            case RealValue(double n) -> Eval.pure(new RealValue(n / 100.0));
        };
    }

    public static Eval<NumericValue> div(NumericValue left, NumericValue right) {
        double divisor =
            switch (right) {
                case IntValue r -> r.number();
                case RealValue r -> r.number();
            };
        if (divisor == 0.0) {
            return Eval.raiseError(new EvalError.ArithmeticError("Division by zero"));
        }

        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> Eval.pure(new IntValue(l.number() / r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() / r.number()));
            };

            case RealValue l -> switch (right) {
                case IntValue r -> Eval.pure(new RealValue(l.number() / r.number()));
                case RealValue r -> Eval.pure(new RealValue(l.number() / r.number()));
            };
        };
    }

    public static Eval<NumericValue> pow(NumericValue left, NumericValue right) {
        double baseTest =
            switch (left) {
                case IntValue l -> l.number();
                case RealValue l -> l.number();
            };
        double exponentTest =
            switch (right) {
                case IntValue r -> r.number();
                case RealValue r -> r.number();
            };
        if (baseTest == 0.0 && exponentTest < 0.0) {
            return Eval.raiseError(new EvalError.ArithmeticError("Division by zero in power operation"));
        }
        boolean exponentIsInteger =
            Double.isFinite(exponentTest) && exponentTest == Math.floor(exponentTest);
        if (baseTest < 0.0 && !exponentIsInteger) {
            return Eval.raiseError(new EvalError.ArithmeticError(
                "Power of negative base with non-integer exponent is not defined"));
        }

        return switch (left) {
            case IntValue base -> switch (right) {
                case IntValue exponent -> {
                    if (exponent.number() < 0) {
                        yield Eval.pure(new RealValue(Math.pow(base.number(), exponent.number())));
                    }
                    long result = 1;
                    for (long i = 0; i < exponent.number(); i++) {
                        result *= base.number();
                    }
                    yield Eval.pure(new IntValue(result));
                }
                case RealValue exponent -> Eval.pure(new RealValue(Math.pow(base.number(), exponent.number())));
            };

            case RealValue base -> switch (right) {
                case IntValue exponent -> Eval.pure(new RealValue(Math.pow(base.number(), exponent.number())));
                case RealValue exponent -> Eval.pure(new RealValue(Math.pow(base.number(), exponent.number())));
            };
        };
    }

    public static Eval<NumericValue> fact(NumericValue numericValue) {
        return switch (numericValue) {
            case IntValue(long n) -> {
                if (n < 0) {
                    yield Eval.raiseError(new EvalError.ArithmeticError("Factorial of negative number"));
                }
                long result = 1;
                for (long i = 2; i <= n; i++) {
                    result *= i;
                }
                yield Eval.pure(new IntValue(result));
            }
            case RealValue(double n) -> {
                if (n != Math.floor(n)) {
                    yield Eval.raiseError(new EvalError.ArithmeticError("Factorial of non-integer"));
                }
                if (n < 0) {
                    yield Eval.raiseError(new EvalError.ArithmeticError("Factorial of negative number"));
                }
                long result = 1;
                for (long i = 2; i <= n; i++) {
                    result *= i;
                }
                yield Eval.pure(new IntValue(result));
            }
        };
    }
}
