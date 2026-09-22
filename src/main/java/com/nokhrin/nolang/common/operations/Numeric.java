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
      case NumericValue.IntValue value -> Eval.pure(new NumericValue.IntValue(-value.number()));
      case NumericValue.RealValue value -> Eval.pure(new NumericValue.RealValue(-value.number()));
    };
  }

  public static Eval<NumericValue> abs(NumericValue numericValue) {
    return switch (numericValue) {
      case NumericValue.IntValue value ->
          Eval.pure(new NumericValue.IntValue(Math.abs(value.number())));
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
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.IntValue(l.number() + r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() + r.number()));
          };

      case NumericValue.RealValue l ->
          switch (right) {
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() + r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() + r.number()));
          };
    };
  }

  public static Eval<NumericValue> sub(NumericValue left, NumericValue right) {
    return switch (left) {
      case NumericValue.IntValue l ->
          switch (right) {
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.IntValue(l.number() - r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() - r.number()));
          };

      case NumericValue.RealValue l ->
          switch (right) {
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() - r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() - r.number()));
          };
    };
  }

  public static Eval<NumericValue> mul(NumericValue left, NumericValue right) {
    return switch (left) {
      case NumericValue.IntValue l ->
          switch (right) {
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.IntValue(l.number() * r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() * r.number()));
          };

      case NumericValue.RealValue l ->
          switch (right) {
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() * r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() * r.number()));
          };
    };
  }

  public static Eval<NumericValue> percent(NumericValue number) {
    return switch (number) {
      case NumericValue.IntValue(long n) -> Eval.pure(new NumericValue.RealValue(n / 100.0));
      case NumericValue.RealValue(double n) -> Eval.pure(new NumericValue.RealValue(n / 100.0));
    };
  }

  public static Eval<NumericValue> div(NumericValue left, NumericValue right) {
    double divisor =
        switch (right) {
          case NumericValue.IntValue r -> r.number();
          case NumericValue.RealValue r -> r.number();
        };
    if (divisor == 0.0) {
      return Eval.raiseError(new EvalError.ArithmeticError("Division by zero"));
    }

    return switch (left) {
      case NumericValue.IntValue l ->
          switch (right) {
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.IntValue(l.number() / r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() / r.number()));
          };

      case NumericValue.RealValue l ->
          switch (right) {
            case NumericValue.IntValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() / r.number()));
            case NumericValue.RealValue r ->
                Eval.pure(new NumericValue.RealValue(l.number() / r.number()));
          };
    };
  }

  public static Eval<NumericValue> pow(NumericValue left, NumericValue right) {
    double baseTest =
        switch (left) {
          case NumericValue.IntValue l -> l.number();
          case NumericValue.RealValue l -> l.number();
        };
    double exponentTest =
        switch (right) {
          case NumericValue.IntValue r -> r.number();
          case NumericValue.RealValue r -> r.number();
        };
    if (baseTest == 0.0 && exponentTest < 0.0) {
      return Eval.raiseError(new EvalError.ArithmeticError("Division by zero in power operation"));
    }
    boolean exponentIsInteger =
        Double.isFinite(exponentTest) && exponentTest == Math.floor(exponentTest);
    if (baseTest < 0.0 && !exponentIsInteger) {
      return Eval.raiseError(
          new EvalError.ArithmeticError(
              "Power of negative base with non-integer exponent is not defined"));
    }

    return switch (left) {
      case NumericValue.IntValue base ->
          switch (right) {
            case NumericValue.IntValue exponent -> {
              if (exponent.number() < 0) {
                yield Eval.pure(
                    new NumericValue.RealValue(Math.pow(base.number(), exponent.number())));
              }
              long result = 1;
              try {

                for (long i = 0; i < exponent.number(); i++) {
                  result = Math.multiplyExact(result, base.number());
                }
              } catch (ArithmeticException e) {
                yield Eval.raiseError(new EvalError.ArithmeticError("Integer overflow"));
              }
              yield Eval.pure(new NumericValue.IntValue(result));
            }
            case NumericValue.RealValue exponent ->
                Eval.pure(new NumericValue.RealValue(Math.pow(base.number(), exponent.number())));
          };

      case NumericValue.RealValue base ->
          switch (right) {
            case NumericValue.IntValue exponent ->
                Eval.pure(new NumericValue.RealValue(Math.pow(base.number(), exponent.number())));
            case NumericValue.RealValue exponent ->
                Eval.pure(new NumericValue.RealValue(Math.pow(base.number(), exponent.number())));
          };
    };
  }

  public static Eval<NumericValue> fact(NumericValue numericValue) {
    return switch (numericValue) {
      case NumericValue.IntValue(long n) -> {
        if (n < 0) {
          yield Eval.raiseError(new EvalError.ArithmeticError("Factorial of negative number"));
        }
        long result = 1;
        for (long i = 2; i <= n; i++) {
          result *= i;
        }
        yield Eval.pure(new NumericValue.IntValue(result));
      }
      case NumericValue.RealValue(double n) -> {
        if (n != Math.floor(n)) {
          yield Eval.raiseError(new EvalError.ArithmeticError("Factorial of non-integer"));
        }
        if (n < 0) {
          yield Eval.raiseError(new EvalError.ArithmeticError("Factorial of negative number"));
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
