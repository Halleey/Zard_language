# ⚡ Zard Programming Language

**Zard** is a **typed programming language** inspired by Java, created for studying and improving **programming logic**, **compiler design**, and **AST (Abstract Syntax Tree)** interpretation.
Its main goal is to offer a **simple, clear, and educational syntax** to explore compiler and interpreter concepts in a practical way.

---

## 🧩 Requirements

![JDK Required](https://img.shields.io/badge/Requirement-JDK%2017%2B-blue?style=for-the-badge)
![Clang Required](https://img.shields.io/badge/Requirement-Clang%20Compiler-orange?style=for-the-badge)

Before running or compiling Zard programs, make sure you have:

* **JDK 17+** installed (required to run the interpreter and compiler)
* **Clang/LLVM** installed (required to compile the generated C + LLVM code)

---

## ✨ Current Features

* **Explicit Typing:** Supports `int`, `double`, `string`, `list`, and `map` types.
* **Declaration and Assignment:** Variables can be declared with explicit types and optionally initialized.
* **Functions:** Typed parameters, **recursive functions**, and value returning are supported.
* **AST Execution:** The language uses an Abstract Syntax Tree for code interpretation and execution.
* **Control Flow:** Supports `if`, `else`, and `while` statements.
* **Dynamic Lists:** Built-in methods such as `add()`, `remove()`, `clear()`, and `size()`.
* **Dynamic Maps:** Creation and manipulation with helper functions.
* **Code Importing:** Supports external file imports with aliases, e.g.:

  ```zard
  import "src/language/stdlib/Math.zd" as math;
  ```
* **Functions as Values:** Functions can be stored in variables and called dynamically.
* **Mandatory Main Block:** Every program must start with `main { }`.
* **Output Printing:** `print()` command for displaying data in the console.

---

## 🧠 Example Code

```zard
import "src/language/stdlib/Math.zd" as math;

main {
    int counter = 0;
    string message = "Program start";

    print(message);

    map numbers = {"0": 1, "1": 2, "2": 3};

    function double(int value) {
        print("Double of " + value + " is " + (value * 2));
    }

    while (counter < 5) {
        print("Counter: " + counter);

        if (counter == 3) {
            print("Reached 3, skipping to next");
        }

        call double(counter);
        call math.addToMap(numbers, counter, counter);

        counter++;
    }

    function factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    int result = factorial(5);
    print("Factorial of 5: " + result);
}
```

---

## 🔍 List Example

```zard
import "src/language/stdlib/Math.zd" as math;

main {
    // If no arguments are given, specify the type
    List<int> numbers;

    // If initialized with arguments, the type is inferred automatically
    List list = (3, 4, 5);
}
```

---

## 🚀 Future of Zard

The language will continue to evolve to become more robust and versatile. Upcoming goals include:

* 🛠 **LLVM IR Compilation** for independent execution.
* 🏗 **Full Compiler Implementation** to enable bootstrapping and native binary generation.
* 🔄 **Expanded Standard Library** including mathematical, string, list, and map utilities.

---

## 🔄 Implemented Improvements

* ✅ Added `if` and `else` logical branching.
* ✅ Added `while` looping.
* ✅ Added `return` statements.
* ✅ Refactored AST for better analysis and execution.
* ✅ Implemented dynamic lists (`add`, `remove`, `clear`, `size`).
* ✅ Added function-as-value support.
* ✅ Recursive functions supported.
* ✅ Implemented dynamic maps and helper functions.
* ✅ Added external module import support.
* ✅ Implemented compound operators (`==`, `!=`, `<=`, `>=`).
* ✅ Variables, literals, and complex expressions generating LLVM IR.
* ✅ Initial LLVM IR backend integration for variables, `while`, and `if`.
* ✅ Functional input system for user data.
* ✅ Full list support in compiler via C + LLVM.
* ✅ Support for dynamic list instances with type inference when arguments are provided.
* ✅ **Import support** in the LLVM backend.

---

## 🔄 Improvements in Progress

* 🟡 **Map Support in LLVM:** Generate LLVM for dynamic map creation, access, and modification.
* 🟡 **Functions as Values in LLVM:** Store and call functions dynamically via LLVM IR.
* 🟡 **LLVM Optimization:** Improve LLVM generation performance, especially for data structures.

---

## 📂 How to Use

1. Write your code in a `.zd` file.

2. Place the file in the following directory:

   ```java
   try {
       // Input file path
       String filePath = args.length > 0 ? args[0] : "src/language/main.zd";
       String code = Files.readString(Path.of(filePath));
   ```

3. Run the interpreter to execute the code.

4. Experiment with the language syntax and follow future updates.

5. ⚙ **Make sure Clang and JDK 17+ are installed** — they are required for compiling the generated C + LLVM code.

---

## 🔗 Contributing

If you'd like to suggest improvements or contribute to the project, feel free to open an issue or submit a pull request!
-se à vontade para enviar pull requests ou abrir issues.