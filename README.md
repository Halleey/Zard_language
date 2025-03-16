# Zard

Zard é uma linguagem de programação baseada em Java, criada para fins de estudo e aprimoramento da lógica de programação. Seu objetivo é oferecer uma sintaxe simples e acessível, facilitando o aprendizado sobre a criação de linguagens e a estrutura de compiladores.

## ✨ Características Atuais

- **Sintaxe Simples:** Inspirada no Java, mas reduzida para facilitar a interpretação.
- **Declaração de Variáveis:** Suporte a tipos como `int`, `double` e `string`.
- **Atribuição de Valores:** Permite atribuir valores a variáveis no momento da declaração ou posteriormente.
- **Sistema de Execução Baseado em AST:** Utiliza uma Árvore de Sintaxe Abstrata (AST) para interpretação.
- **Saída de Dados:** Suporte ao comando `print` para exibição de valores no console.
- **Bloco Main:** Todo programa deve começar com `main { }`.

## 📝 Exemplo de Código

```zard
main {
    int x = 6;

    if (x >= 10 ) {
        print("IF AQUI");
    }
    else if(x == 6) {
        while(x < 10){
            x++;
            print(x);
        }
    }
    else {
        print("ELSE EXECUTADO");
    }
}
```

## 🚀 Futuro da Zard

A linguagem continuará recebendo melhorias para tornar-se mais robusta e versátil. Algumas das metas incluem:

- 📌 **Suporte a Funções** (declaração e chamada de funções).
- 📚 **Tipos de Dados Avançados** (listas e mapas dinâmicos).
- 🔧 **Sistema de Módulos e Importação de Código**.
- 🛠 **Compilação para LLVM IR** para execução independente.
- 🏗 **Criação de um compilador** para alcançar o sonhado Bootstrapping .

## 🔄 Melhorias em Desenvolvimento

Atualmente, a Zard está passando por implementações importantes:
- [x]**Criação de IF'S** para permitir decições lógicas.
- [x]**Criação do While** para permitir Loopings.   
- []**Adição do return** para encerrar loopings.
- []**Refatorando a AST** para permitir melhor análise e otimização do código.
- []**Implementação de listas dinâmicas** para facilitar manipulação de coleções.

## 📂 Uso

1. Escreva seu código em um arquivo `.zd`.
2. Utilize o interpretador para executar o código.
3. Experimente a sintaxe da linguagem e acompanhe as atualizações futuras!

---

🔗 **Contribuição**

Caso tenha sugestões ou queira contribuir para o projeto, fique à vontade para compartilhar ideias e feedback!

📧 **Contato**

Se quiser saber mais sobre a Zard, entre em contato para discutir melhorias e novos recursos!



