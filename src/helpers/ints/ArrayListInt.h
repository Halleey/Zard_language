#ifndef ARRAYLIST_INT_H
#define ARRAYLIST_INT_H

#include <stdlib.h>
#include <stdio.h>

typedef struct {
    int* data;
    size_t length;
    size_t capacity;
} ArrayListInt;

ArrayListInt* arraylist_create_int(size_t initial_capacity);
void arraylist_add_int(ArrayListInt* list, int value);
void arraylist_addAll_int(ArrayListInt* list, int* values, size_t n);
void arraylist_remove_int(ArrayListInt* list, size_t index);
void arraylist_clear_int(ArrayListInt* list);
void arraylist_free_int(ArrayListInt* list);
int arraylist_update_int(ArrayListInt* list, size_t index, int value);
int arraylist_get_int(ArrayListInt* list, size_t index, int* out);
int arraylist_size_int(ArrayListInt* list);

#endif
