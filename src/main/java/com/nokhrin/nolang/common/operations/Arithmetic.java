package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.NumericValue;
import com.nokhrin.nolang.common.values.RealValue;

public class Arithmetic {

    private Arithmetic() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static NumericValue neg(NumericValue numericValue) {
        return switch (numericValue) {
            case IntValue value -> new IntValue(-value.number());
            case RealValue value -> new RealValue(-value.number());
        };
    }

    public static NumericValue abs(NumericValue numericValue) {
        return switch (numericValue) {
            case IntValue value -> new IntValue(Math.abs(value.number()));
            case RealValue value -> new RealValue(Math.abs(value.number()));
        };
    }

    public static NumericValue add(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> new IntValue(l.number() + r.number());
                case RealValue r -> new RealValue(l.number() + r.number());
            };

            case RealValue l -> switch (right) {
                case IntValue r -> new RealValue(l.number() + r.number());
                case RealValue r -> new RealValue(l.number() + r.number());
            };

        };
    }

    public static NumericValue sub(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> new IntValue(l.number() - r.number());
                case RealValue r -> new RealValue(l.number() - r.number());
            };

            case RealValue l -> switch (right) {
                case IntValue r -> new RealValue(l.number() - r.number());
                case RealValue r -> new RealValue(l.number() - r.number());
            };

        };
    }

    public static NumericValue mul(NumericValue left, NumericValue right) {
        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> new IntValue(l.number() * r.number());
                case RealValue r -> new RealValue(l.number() * r.number());
            };

            case RealValue l -> switch (right) {
                case IntValue r -> new RealValue(l.number() * r.number());
                case RealValue r -> new RealValue(l.number() * r.number());
            };

        };
    }

    public static NumericValue div(NumericValue left, NumericValue right) {
        double divisor = switch (right) {
            case IntValue r -> r.number();
            case RealValue r -> r.number();
        };
        if (divisor == 0.0) {
            throw new ArithmeticException("Division by zero");
        }

        return switch (left) {
            case IntValue l -> switch (right) {
                case IntValue r -> new IntValue(l.number() / r.number());
                case RealValue r -> new RealValue(l.number() / r.number());
            };

            case RealValue l -> switch (right) {
                case IntValue r -> new RealValue(l.number() / r.number());
                case RealValue r -> new RealValue(l.number() / r.number());
            };

        };
    }

    public static NumericValue pow(NumericValue left, NumericValue right) {
        double baseTest = switch (left) {
            case IntValue l -> l.number();
            case RealValue l -> l.number();
        };
        double exponentTest = switch (right) {
            case IntValue r -> r.number();
            case RealValue r -> r.number();
        };
        if (baseTest == 0.0 && exponentTest < 0.0) {
            throw new ArithmeticException("Division by zero in power operation");
        }
        boolean exponentIsInteger = Double.isFinite(exponentTest) && exponentTest == Math.floor(exponentTest);
        if (baseTest < 0.0 && !exponentIsInteger) {
            throw new ArithmeticException(
                "Power of negative base with non-integer exponent is not defined");
        }

        return switch (left) {
            case IntValue base -> switch (right) {
                case IntValue exponent -> {
                    if (exponent.number() < 0) {
                        yield new RealValue(Math.pow(base.number(), exponent.number()));
                    }
                    long result = 1;
                    for (long i = 0; i < exponent.number(); i++) {
                        result *= base.number();
                    }
                    yield new IntValue(result);
                }
                case RealValue exponent -> new RealValue(Math.pow(base.number(), exponent.number()));
            };

            case RealValue base -> switch (right) {
                case IntValue exponent -> new RealValue(Math.pow(base.number(), exponent.number()));
                case RealValue exponent -> new RealValue(Math.pow(base.number(), exponent.number()));
            };

        };
    }

    public static NumericValue fact(NumericValue numericValue) {
        return switch (numericValue) {
            case IntValue(long n) -> {
                if (n < 0) {
                    throw new ArithmeticException("Factorial of negative number");
                }
                long result = 1;
                for (long i = 2; i <= n; i++) {
                    result *= i;
                }
                yield new IntValue(result);
            }
            case RealValue(double n) -> {
                if (n != Math.floor(n)) {
                    throw new ArithmeticException("Factorial of non-integer");
                }
                if (n < 0) {
                    throw new ArithmeticException("Factorial of negative number");
                }
                long result = 1;
                for (long i = 2; i <= n; i++) {
                    result *= i;
                }
                yield new IntValue(result);
            }
        };
    }

}
