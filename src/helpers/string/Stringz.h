#ifndef STRINGZ_H
#define STRINGZ_H
#include <stddef.h>

typedef struct {
    char *data;
    size_t length;
} String;

// Funções de criação/manipulação
String* createString(const char *data);
void setString(String *str, const char *data);
void concatString(String *str, const char *data);
void toUpperCase(String *str);
void toLowerCase(String *str);
void reverseString(String *str);

// Comparações
int compareString(String *strOne, String *strTwo);
int startWith(String *str, const char *prefix);

// Utilitários
void printString(String *str);
void freeString(String *str);
size_t my_copyString(const char *origem, char *destino);
size_t my_strlen(const char *text);
void printString_noNL(String *str);


#endif
