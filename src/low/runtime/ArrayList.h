#ifndef ARRAYLIST_H
#define ARRAYLIST_H
#include <stdio.h>
typedef struct
{
    void ** data;
    size_t length;
    size_t capacity;
}ArrayList;

ArrayList *arraylist_create(size_t initial_capacity);
void setItems(ArrayList * list, void * data);
void removeItem(ArrayList * list, size_t position);
void freeList(ArrayList * list);
void * getItem(ArrayList * list, size_t index);

#endif