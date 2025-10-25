#  Zard Programming Language

**Zard** is a **typed programming language** inspired by Java, designed for learning and improving **programming logic**, **compiler construction**, and **AST (Abstract Syntax Tree)** interpretation. It focuses on a **simple, explicit, and educational syntax** to explore compiler and interpreter concepts in practice.

---

##  Requirements

![JDK Required](https://img.shields.io/badge/Requirement-JDK%2017%2B-blue?style=for-the-badge)
![Clang Required](https://img.shields.io/badge/Requirement-Clang%20Compiler-orange?style=for-the-badge)

Before running or compiling Zard programs, ensure you have:

* **JDK 17+** installed (required for interpreter and compiler)
* **Clang/LLVM** installed (required to compile the generated LLVM IR)

---

##  Features

* **Explicit Typing:** Built-in `int`, `double`, `string`, `List`, `Map`, and `Struct`.
* **Structs:** Define and instantiate your own structured types, even across modules.
* **Functions:** Supports typed parameters, recursion, and return values.
* **Functions as Values:** Functions can be stored in variables and called dynamically.
* **Functions with Lists:**

  * Functions now support receiving `List<T>` parameters.
  * Functions can also **return `List<T>` values** (e.g., `function List<int> buildList()`).
* **Control Flow:** Includes `if`, `else`, `while`, and `break`.
* **AST Execution:** Code execution is based on AST interpretation.
* **Dynamic Lists & Maps:**

  * `add(value)` – append an element.
  * `addAll(values...)` – adds all elements.
  * `remove(index)` – remove element at index.
  * `get(index)` – retrieve element (works in expressions like `print()` or `if`).
  * `size()` – get list length (works in expressions).
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

### Structs and Nested Structs

```zard
main {
    Struct Pais {
        string nome;
    }

    Struct Endereco {
        string rua;
        string cidade;
        Struct Pais pais;
    }

    Struct Pessoa {
        string nome;
        int idade;
        Struct Endereco endereco;
    }

    List<Pessoa> pessoas;

    Struct Pais brasil;
    brasil.nome = "Brasil";

    Struct Pessoa p1;
    p1.nome = "Alice";
    p1.idade = 25;
    Struct Endereco e1;
    e1.rua = "Rua A";
    e1.cidade = "São Paulo";
    e1.pais = brasil;
    p1.endereco = e1;

    pessoas.add(p1);

    if (pessoas.get(0).endereco.pais.nome == "Brasil") {
        print("Alice mora no Brasil!");
    }
}
```

### Functions and Lists

```zard
main {
    function int sumAges(List<int> ages) {
        int total = 0;
        int i = 0;
        while (i < ages.size()) {
            total = total + ages.get(i);
            i++;
        }
        return total / ages.size();
    }

    List<int> valores;
    valores.add(25);
    valores.add(17);
    valores.add(32);

    int media = sumAges(valores);
    print("Idade média:");
    print(media);
}
```
### Math Library Import

```zard
import "src/language/stdlib/Math.zd" as math;

main {
    double a = 10;
    double b = 3;

    print(math.sum(a, b));
    print(math.sqrt(a));
    print(math.factorial(5));
    print(math.sin(a));
    print(math.cos(a));
}
```

---

##  Future Roadmap

* 🛠 **LLVM IR Compilation:** More robust native code generation.
* 🏗 **Bootstrap Compiler:** Write compiler in Zard itself.
* 🔄 **Expanded Standard Library:** Math, string, list, map, and struct utilities.
* 📦 **Generic Functions:** Extend support for generic functions with `List<T>` and `Map<K,V>`.
* ⚡ **Optimizations:** LLVM optimization passes for better performance.

---

## 📂 How to Run

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
