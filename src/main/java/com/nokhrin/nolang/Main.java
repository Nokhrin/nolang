package com.nokhrin.nolang;

public class Main {
  static void main(String[] args) {
    String runtimeName = "algebraic";
    if (args.length > 0) {
      runtimeName = args[0];
    }

    switch (runtimeName) {
      case "algebraic", "1" -> AlgebraicRunner.run(args);
      case "dynamic", "2" -> DynamicTypedRunner.run(args);
      case "static", "3" -> StaticTypedRunner.run(args);
      default -> {
        System.err.println("Unknown runtime: " + runtimeName);
        System.exit(1);
      }
    }
  }
}
