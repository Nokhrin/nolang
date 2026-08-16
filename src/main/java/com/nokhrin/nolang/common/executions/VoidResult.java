package com.nokhrin.nolang.common.executions;

public record VoidResult() implements Result {
  @Override
  public String toString() {
    return "void";
  }
}
