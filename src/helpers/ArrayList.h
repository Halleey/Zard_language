#ifndef ARRAYLIST_H
#define ARRAYLIST_H
#include <stdlib.h>

typedef struct {
    void **data;
    size_t length;
    size_t capacity;
} ArrayList;

ArrayList* arraylist_create(size_t initial_capacity);

void removeItem(ArrayList* list, size_t index);
int size(ArrayList* list);
int length(ArrayList* list);

void arraylist_add_ptr(ArrayList* list, void* element);
void* arraylist_get_ptr(ArrayList* list, size_t index);

void clearList(ArrayList* list);
void freeList(ArrayList* list);
void freeListRef(ArrayList* list);
#endif
