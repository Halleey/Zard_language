#pragma once
#include <stddef.h>
#include "Stringz.h"

typedef struct {
    void **data;
    size_t length;
    size_t capacity;
} ArrayList;

ArrayList* arraylist_create(size_t initial_capacity);

void arraylist_add_ptr(ArrayList* list, void* element);
void* arraylist_get_ptr(ArrayList* list, size_t index);
size_t length(ArrayList* list);

void removeItem(ArrayList* list, size_t index);
void clearList(ArrayList* list);
void freeList(ArrayList* list);

void arraylist_add_String(ArrayList* list, String* str);
void arraylist_addAll_String(ArrayList* list, String** strings, size_t n);
