package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.core.Eval;
import com.nokhrin.nolang.common.values.Value;

public enum BinaryLogicalOperation implements BinaryOperation<Value.BoolValue> {
  AND("AND"),
  OR("OR");

  private final String operator;

  BinaryLogicalOperation(String operator) {
    this.operator = operator;
  }

  @Override
  public String operator() {
    return operator;
  }

  @Override
  public Eval<Value.BoolValue> apply(Value.BoolValue left, Value.BoolValue right) {
    return switch (this) {
      case AND -> Logical.and(left, right);
      case OR -> Logical.or(left, right);
    };
  }
}
