#ifndef ARRAYLIST_BOOL_H
#define ARRAYLIST_BOOL_H

#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

typedef struct {
    uint8_t* data;      // cada bool ocupa 1 byte, alinhando com llvm corretamente
    size_t length;
    size_t capacity;
} ArrayListBool;

ArrayListBool* arraylist_create_bool(size_t initial_capacity);
void arraylist_free_bool(ArrayListBool* list);
void arraylist_add_bool(ArrayListBool* list, bool value);
void arraylist_addAll_bool(ArrayListBool* list, uint8_t* values, size_t n);
void arraylist_remove_bool(ArrayListBool* list, size_t index);
void arraylist_clear_bool(ArrayListBool* list);
int arraylist_update_bool(ArrayListBool* list, size_t index, bool value);
int arraylist_get_bool(ArrayListBool* list, size_t index, bool* out);
int arraylist_size_bool(ArrayListBool* list);


#endif