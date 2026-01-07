# Zard Programming Language

Zard is a statically typed programming language inspired by Java, designed for the study of compiler construction, abstract syntax tree (AST) manipulation, and LLVM IR generation. The project focuses on providing a transparent view of the transition from high-level source code to optimized native binaries.

## Technical Specifications

* **Type System**: Static and explicit.
* **Target Backend**: LLVM Intermediate Representation (IR).
* **Host Environment**: Java Virtual Machine (JDK 17+).
* **Compilation Pipeline**: Source (.zd) -> Lexical Analysis -> Parsing (AST) -> Semantic Analysis -> LLVM IR Generation -> Clang/LLVM Optimization -> Native Binary.

## System Requirements

The following dependencies are required to run the interpreter and the full compilation toolchain:

1.  **JDK 17 or higher**: Necessary for the compiler core and interpreter.
2.  **LLVM Toolchain**: Required for IR optimization and assembler tools.
3.  **Clang Compiler**: Used to link the generated LLVM IR and produce the final native executable.

## Installation and Setup

Ensure that the LLVM tools and Clang are correctly installed and mapped to your system's PATH.

```bash
# Verify environment
java --version
clang --version
```
##  How to Run

1. Write code in a `.zd` file in `src/language`.

2. Add your file path to the runner.

3. Run the compiler:

   ```java
   String filePath = args.length > 0 ? args[0] : "src/language/main.zd";
   String code = Files.readString(Path.of(filePath));
   ```
---

## 🔗 Contributing

Contributions are welcome! Open issues, suggest features, or submit pull requests to help evolve the language.
