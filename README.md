# ⚡ Zard Programming Language

**Zard** is a **typed programming language** inspired by Java, designed for learning and improving **programming logic**, **compiler construction**, and **AST (Abstract Syntax Tree)** interpretation. It focuses on a **simple, explicit, and educational syntax** to explore compiler and interpreter concepts in practice.

---

## 🧩 Requirements

![JDK Required](https://img.shields.io/badge/Requirement-JDK%2017%2B-blue?style=for-the-badge)
![Clang Required](https://img.shields.io/badge/Requirement-Clang%20Compiler-orange?style=for-the-badge)

Before running or compiling Zard programs, ensure you have:

* **JDK 17+** installed (required for interpreter and compiler)
* **Clang/LLVM** installed (required to compile the generated LLVM IR)

---

## ✨ Features

* **Explicit Typing:** Built-in `int`, `double`, `string`, `List`, `Map`, and `Struct`.
* **Structs:** Define and instantiate your own structured types, even across modules.
* **Functions:** Supports typed parameters, recursion, and return values.
* **Functions as Values:** Functions can be stored in variables and called dynamically.
* **Control Flow:** Includes `if`, `else`, `while`, and `break`.
* **AST Execution:** Code execution is based on AST interpretation.
* **Dynamic Lists & Maps:**

  * `add(value)` – append an element.
  * * `addAll(values...)` – adds all elements.
  * `remove(index)` – remove element at index.
  * `get(index)` – retrieve element (only valid inside expressions like `print()`, `if`, etc.).
  * `size()` – get list length (only valid inside expressions like `print()`, `if`, etc.).
  * `clear()` – remove all elements.
* **External Imports:** Import external modules with aliasing.
* **Mandatory Main Block:** All programs start inside `main { }`.
* **LLVM Backend:** Generates LLVM IR for native compilation.

---

## 🔍 Code Examples

### Hello World

```zard
main {
    print("Hello, Zard!");
}
```

### Structs and Imports

```zard
import "src/language/structs/StructTest.zd" as st;

main {
    // Local struct definition
    Struct Pessoa {
        string nome;
        int idade;
    }

    // Instantiate imported struct with direct arguments
    st.Struct Nomade n1 = {"zard", 19};
    print(n1);

    // Instantiate imported struct without initializer
    st.Struct Nomade n2;
    n2.nome = "sun";
    n2.idade = 20;
    print(n2.nome);

    // Local struct with initializer
    Struct Pessoa p = {"halley", 18};
    print(p);
}
```

### Functions and Recursion

```zard
main {
    function int factorial(int n) {
        if (n == 0) return 1;
        return n * factorial(n - 1);
    }

    int x = factorial(5);
    print(x); // prints 120
}
```

### Lists

```zard
main {
    // Empty list declaration
    List<int> numeros;

    // Adding elements
    numeros.add(1);
    numeros.add(2);
    numeros.add(3);

    // Access with get() must be inside an expression
    print(numeros.get(0));

    // Removing an element
    numeros.remove(1);

    // Getting size (only inside expressions)
    if (numeros.size() > 1) {
        print("More than one element");
    }

    // Clearing list
    numeros.clear();

    // Initialized list with ()
    List<string> nomes = ("Alice", "Bob", "Charlie");
    print(nomes.size()); // prints 3
}
```

---

## 🚀 Future Roadmap

* 🛠 **LLVM IR Compilation:** More robust native code generation.
* 🏗 **Bootstrap Compiler:** Write compiler in Zard itself.
* 🔄 **Expanded Standard Library:** Math, string, list, map, and struct utilities.
* ⚡ **Optimizations:** LLVM optimization passes for better performance.

---

## 📂 How to Run

1. Write code in a `.zd` file.
2. add your file path here
3. Run the interpreter:

   ```bash
        String filePath = args.length > 0 ? args[0] : "src/language/main.zd";
        String code = Files.readString(Path.of(filePath));
        
   ```
5. Or compile to LLVM IR and then to native executable using `clang`.

---

## 🔗 Contributing

Contributions are welcome! Open issues, suggest features, or submit pull requests to help evolve the language.
