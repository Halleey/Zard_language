# Zard

**Zard** é uma linguagem de programação tipada, inspirada em Java, criada para fins de estudo e aprimoramento da lógica de programação, compiladores e ASTs. Seu objetivo é oferecer uma **sintaxe simples, clara e didática**, permitindo explorar conceitos de interpretação e compilação.

---

## ✨ Características Atuais

* **Tipagem Explícita:** Suporte a tipos `int`, `double`, `string`, `list` e `map`.
* **Declaração e Atribuição:** Variáveis podem ser declaradas com tipo explícito e receber valores imediatamente ou posteriormente.
* **Funções:** Declaração de funções com parâmetros tipados, suporte a **funções recursivas** e retorno de valores.
* **Execução via AST:** A linguagem utiliza uma Árvore de Sintaxe Abstrata para interpretar e executar o código.
* **Controle de Fluxo:** Suporte a `if`, `else` e `while`.
* **Listas Dinâmicas:** Métodos `add()`, `remove()`, `clear()` e `size()`.
* **Mapas Dinâmicos:** Criação e manipulação de mapas com funções auxiliares.
* **Importação de Código:** Suporte a importação de arquivos externos com alias, ex.:

  ```zard
  import "src/language/stdlib/Math.zd" as math;
  ```
* **Funções como Valores:** Variáveis podem armazenar funções e chamá-las dinamicamente.
* **Bloco Main Obrigatório:** Todo programa deve começar com `main { }`.
* **Print de Saída:** Suporte ao comando `print()` para exibir resultados no console.

---

## 📝 Exemplo de Código

```zard
import "src/language/stdlib/Math.zd" as math;

main {
    int contador = 0;
    string mensagem = "Início do programa";

    print(mensagem);

    map numeros = {"0": 1, "1": 2, "2": 3};

    function dobrar(int valor) {
        print("Dobro de " + valor + " é " + (valor * 2));
    }

    while (contador < 5) {
        print("Contador: " + contador);

        if (contador == 3) {
            print("Chegou no 3, pulando para próximo");
        }

        call dobrar(contador);
        call math.adicionarMapa(numeros, contador, contador);

        contador++;
    }

    function fatorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * fatorial(n - 1);
        }
    }

    int resultado = fatorial(5);
    print("Fatorial de 5: " + resultado);
}
```

---

## 🚀 Futuro da Zard

A linguagem continuará evoluindo para se tornar mais robusta e versátil. Algumas metas incluem:

* 🛠 **Compilação para LLVM IR** para execução independente.
* 🏗 **Criação de um compilador completo**, visando bootstrapping e geração de binários nativos.
* 🔄 **Expansão da biblioteca padrão** com funções matemáticas, manipulação de strings, listas e mapas.

---

## 🔄 Melhorias Implementadas

*

---

## 📂 Uso

1. Escreva seu código em um arquivo `.zd`.
2. Utilize o interpretador para executar o código.
3. Experimente a sintaxe da linguagem e acompanhe as atualizações futuras.

---

## 🔗 Contribuição

Se você quiser sugerir melhorias ou contribuir com o projeto, sinta-se à vontade para enviar pull requests ou abrir issues.

---

## 📧 Contato

Para discutir melhorias e novos recursos da linguagem Zard, entre em contato via email ou GitHub.
