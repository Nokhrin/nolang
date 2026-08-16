package com.nokhrin.nolang.common.definitions;

public sealed interface Symbol permits VariableSymbol, FunctionSymbol {
  String name();
}
