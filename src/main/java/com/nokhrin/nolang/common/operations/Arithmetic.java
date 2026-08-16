package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.DoubleValue;
import com.nokhrin.nolang.common.values.IntValue;
import com.nokhrin.nolang.common.values.Value;

public class Arithmetic {

  private Arithmetic() {
    throw new UnsupportedOperationException("Utility class");
  }

  public static Value neg(Value number) {
    return switch (number) {
      case IntValue(long n) -> new IntValue(-n);
      case DoubleValue(double n) -> new DoubleValue(-n);
      default -> throw new IllegalStateException("Unexpected number: " + number);
    };
  }

  public static Value abs(Value number) {
    return switch (number) {
      case IntValue(long n) -> new IntValue(Math.abs(n));
      case DoubleValue(double n) -> new DoubleValue(Math.abs(n));
      default -> throw new IllegalStateException("Unexpected number: " + number);
    };
  }

  public static Value add(Value left, Value right) {
    return switch (left) {
      case IntValue(long l) ->
          switch (right) {
            case IntValue(long r) -> new IntValue(l + r);
            case DoubleValue(double r) -> new DoubleValue(l + r);
            default -> throw new IllegalStateException("Unexpected number: " + right);
          };
      case DoubleValue(double l) ->
          switch (right) {
            case IntValue(long r) -> new DoubleValue(l + r);
            case DoubleValue(double r) -> new DoubleValue(l + r);
            default -> throw new IllegalStateException("Unexpected number: " + right);
          };
      default -> throw new IllegalStateException("Unexpected number: " + left);
    };
  }

  public static Value sub(Value left, Value right) {
    return switch (left) {
      case IntValue(long l) ->
          switch (right) {
            case IntValue(long r) -> new IntValue(l - r);
            case DoubleValue(double r) -> new DoubleValue(l - r);
            default -> throw new IllegalStateException("Unexpected number: " + right);
          };
      case DoubleValue(double l) ->
          switch (right) {
            case IntValue(long r) -> new DoubleValue(l - r);
            case DoubleValue(double r) -> new DoubleValue(l - r);
            default -> throw new IllegalStateException("Unexpected number: " + right);
          };
      default -> throw new IllegalStateException("Unexpected number: " + left);
    };
  }

  public static Value mul(Value left, Value right) {
    return switch (left) {
      case IntValue(long l) ->
          switch (right) {
            case IntValue(long r) -> new IntValue(l * r);
            case DoubleValue(double r) -> new DoubleValue(l * r);
            default -> throw new IllegalStateException("Unexpected number: " + right);
          };
      case DoubleValue(double l) ->
          switch (right) {
            case IntValue(long r) -> new DoubleValue(l * r);
            case DoubleValue(double r) -> new DoubleValue(l * r);
            default -> throw new IllegalStateException("Unexpected number: " + right);
          };
      default -> throw new IllegalStateException("Unexpected number: " + left);
    };
  }

  public static Value div(Value left, Value right) {
    double divisor = toDouble(right);
    if (divisor == 0) {
      throw new ArithmeticException("Division by zero");
    }
    double result = toDouble(left) / divisor;
    return new DoubleValue(result);
  }

  public static Value pow(Value left, Value right) {
    double base = toDouble(left);
    double exponent = toDouble(right);

    if (base == 0.0 && exponent < 0.0) {
      throw new ArithmeticException("Division by zero in power operation");
    }
    boolean exponentIsInteger = Double.isFinite(exponent) && exponent == Math.floor(exponent);
    if (base < 0.0 && !exponentIsInteger) {
      throw new ArithmeticException(
          "Power of negative base with non-integer exponent is not defined");
    }
    return new DoubleValue(Math.pow(base, exponent));
  }

  public static Value fact(Value operand) {
    return switch (operand) {
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
      case DoubleValue(double n) -> {
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
      default -> throw new ArithmeticException("Factorial is not supported for: " + operand);
    };
  }

  public static double toDouble(Value valueNumber) {
    return switch (valueNumber) {
      case IntValue intValue -> intValue.number();
      case DoubleValue doubleValue -> doubleValue.number();
      default -> throw new IllegalStateException("Unexpected number: " + valueNumber);
    };
  }
}
