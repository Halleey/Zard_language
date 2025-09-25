#ifndef ARRAYLIST_H
#define ARRAYLIST_H

#include "DynValue.h"
#include <stddef.h>

typedef struct {
    void** data;
    size_t length;
    size_t capacity;
} ArrayList;

ArrayList* arraylist_create(size_t initial_capacity);
void setItems(ArrayList* list, DynValue* data);
DynValue* getItem(ArrayList* list, size_t index);
void removeItem(ArrayList* list, size_t position);
void freeList(ArrayList* list);
void clearList(ArrayList* list);
size_t size(ArrayList *list);
void addAll(ArrayList* list, DynValue** data, size_t dataLength);

#endif
