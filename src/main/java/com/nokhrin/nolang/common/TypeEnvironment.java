package com.nokhrin.nolang.common;

public interface TypeEnvironment {
  void declare(String name, Type type);

  Type resolve(String name);
}
