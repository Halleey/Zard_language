#ifndef ARRAYLISTSTRING_H
#define ARRAYLISTSTRING_H

#include <stdlib.h>
#include "Stringz.h"

typedef struct {
    String **data;
    size_t length;
    size_t capacity;
} ArrayListString;

ArrayListString* arraylist_string_create(size_t initial_capacity);

void arraylist_string_add(ArrayListString* list, String* str);
void arraylist_string_addAll(ArrayListString* list, String** strings, size_t n);

String* arraylist_string_get(ArrayListString* list, size_t index);

void arraylist_string_remove(ArrayListString* list, size_t index);

void arraylist_string_free(ArrayListString* list);
void arraylist_string_freeRef(ArrayListString* list);

size_t arraylist_string_length(ArrayListString* list);

#endif