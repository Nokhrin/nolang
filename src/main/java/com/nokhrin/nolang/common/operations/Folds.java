package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.NumericValue;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class Folds {

  public static NumericValue left(
      List<NumericValue> values,
      List<? extends java.util.function.BinaryOperator<NumericValue>> operations) {
    NumericValue accumResult = values.getFirst();
    for (int i = 0; i < operations.size(); i++) {
      accumResult = operations.get(i).apply(accumResult, values.get(i + 1));
    }
    return accumResult;
  }

  public static NumericValue right(
      List<NumericValue> values, List<? extends BinaryOperator<NumericValue>> operations) {
    NumericValue accumResult = values.getLast();
    for (int i = operations.size() - 1; i >= 0; i--) {
      accumResult = operations.get(i).apply(values.get(i), accumResult);
    }
    return accumResult;
  }

  public static NumericValue right(
      NumericValue operand, List<? extends UnaryOperator<NumericValue>> operations) {
    NumericValue accumResult = operand;
    for (int i = operations.size() - 1; i >= 0; i--) {
      accumResult = operations.get(i).apply(accumResult);
    }
    return accumResult;
  }
}
