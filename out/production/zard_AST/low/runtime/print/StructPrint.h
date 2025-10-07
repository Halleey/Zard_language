#ifndef STRUCTPRINT_H
#define STRUCTPRINT_H
#include <stdbool.h>
#include <stddef.h>

typedef struct ArrayList {
    void** data;
    size_t length;
    size_t capacity;
} ArrayList;

typedef struct ArrayListInt {
    int* data;
    size_t length;
    size_t capacity;
} ArrayListInt;

typedef struct ArrayListDouble {
    double* data;
    size_t length;
    size_t capacity;
} ArrayListDouble;

typedef struct ArrayListBool {
    bool* data;
    size_t length;
    size_t capacity;
} ArrayListBool;


#endif
