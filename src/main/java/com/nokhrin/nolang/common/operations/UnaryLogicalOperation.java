package com.nokhrin.nolang.common.operations;

import com.nokhrin.nolang.common.values.BoolValue;
import com.nokhrin.nolang.functional.Eval;

public enum UnaryLogicalOperation implements UnaryOperation<BoolValue> {
  NOT("NOT");

  private final String operator;
  UnaryLogicalOperation(String operator) {
      this.operator=operator;
  }

  @Override
  public String operator() {
    return operator;
  }

  @Override
  public Eval<BoolValue> apply(BoolValue operand) {
    return switch (this){
        case NOT -> Logical.not(operand);
    };

  }
}
