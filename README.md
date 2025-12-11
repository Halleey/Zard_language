#  Zard Programming Language

**Zard** is a **typed programming language** inspired by Java, designed for learning and improving **programming logic**, **compiler construction**, and **AST (Abstract Syntax Tree)** interpretation. It focuses on a **simple, explicit, and educational syntax** to explore compiler and interpreter concepts in practice.

---
##  Requirements

![JDK Required](https://img.shields.io/badge/Requirement-JDK%2017%2B-blue?style=for-the-badge)
![Clang Required](https://img.shields.io/badge/Requirement-Clang%20Compiler-orange?style=for-the-badge)

Before running or compiling Zard programs, ensure you have:

* **JDK 17+** installed (required for interpreter and compiler)
* **Clang/LLVM** installed (required to compile the generated LLVM IR)

##  How to Run

1. Write code in a `.zd` file.

2. Add your file path to the runner.

3. Run the interpreter:

   ```java
   String filePath = args.length > 0 ? args[0] : "src/language/main.zd";
   String code = Files.readString(Path.of(filePath));
   ```

4. Or compile to LLVM IR and then to native executable using `clang`.

---

## 🔗 Contributing

Contributions are welcome! Open issues, suggest features, or submit pull requests to help evolve the language.
