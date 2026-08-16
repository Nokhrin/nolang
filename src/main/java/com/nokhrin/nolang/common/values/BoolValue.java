package com.nokhrin.nolang.common.values;

import com.nokhrin.nolang.common.Type;

public record BoolValue(boolean value) implements Value {
  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public Type type() {
    return Type.BOOLEAN;
  }
}
