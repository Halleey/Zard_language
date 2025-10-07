#ifndef ARRAYLIST_H
#define ARRAYLIST_H
#include <stdlib.h>
#include "Stringz.h"

typedef struct {
    void **data;
    size_t length;
    size_t capacity;
} ArrayList;

ArrayList* arraylist_create(size_t initial_capacity);



void arraylist_add_string(ArrayList* list, char* str);
void arraylist_add_String(ArrayList* list, String* str);



void arraylist_addAll_string(ArrayList* list, char** strings, size_t n);
void arraylist_addAll_String(ArrayList* list, String** strings, size_t n);



void removeItem(ArrayList* list, size_t index);
int update_int(ArrayList* list, size_t index, int value);
int update_double(ArrayList* list, size_t index, double value);
int update_string(ArrayList* list, size_t index, char* str);

void* getItem(ArrayList* list, size_t index);
int length(ArrayList* list);

void clearList(ArrayList* list);
void freeList(ArrayList* list);

#endif
