package com.nokhrin.nolang.common;

public interface Executor {
  void runInteractive();

  void runFile(String path);
}
