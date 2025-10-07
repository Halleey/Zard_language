#ifndef ARRAYLIST_DOUBLE_H
#define ARRAYLIST_DOUBLE_H

#include <stdlib.h>
#include <stdio.h>

typedef struct {
    double* data;
    size_t length;
    size_t capacity;
} ArrayListDouble;

ArrayListDouble* arraylist_create_double(size_t initial_capacity);
void arraylist_add_double(ArrayListDouble* list, double value);
void arraylist_addAll_double(ArrayListDouble* list, double* values, size_t n);
void arraylist_remove_double(ArrayListDouble* list, size_t index);
void arraylist_clear_double(ArrayListDouble* list);
void arraylist_free_double(ArrayListDouble* list);
int arraylist_update_double(ArrayListDouble* list, size_t index, double value);
int arraylist_get_double(ArrayListDouble* list, size_t index, double* out);

#endif
