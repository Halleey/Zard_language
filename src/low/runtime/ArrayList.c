#include "ArrayList.h"
#include <stdlib.h>
#include <stdio.h>

ArrayList* arraylist_create(size_t initial_capacity) {
    ArrayList* list = malloc(sizeof(ArrayList));
    if (!list) return NULL;
    list->data = malloc(sizeof(void*) * initial_capacity);
    if (!list->data) {
        free(list);
        return NULL;
    }
    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

void setItems(ArrayList* list, DynValue* data) {
    if (list->length == list->capacity) {
        size_t newCapacity = list->capacity * 2;
        void** newData = realloc(list->data, sizeof(void*) * newCapacity);
        if (!newData) {
            fprintf(stderr, "Falha ao salvar array\n");
            return;
        }
        list->data = newData;
        list->capacity = newCapacity;
    }
    list->data[list->length++] = (void*) data;
}

DynValue* getItem(ArrayList* list, size_t index) {
    if (index >= list->length) {
        fprintf(stderr, "Index out of bounds\n");
        return NULL;
    }
    return (DynValue*) list->data[index];
}

void removeItem(ArrayList* list, size_t position) {
    if (position >= list->length) {
        fprintf(stderr, "Position not found\n");
        return;
    }
    free(list->data[position]);
    for (size_t i = position; i < list->length - 1; i++) {
        list->data[i] = list->data[i+1];
    }
    list->length--;
}

void freeList(ArrayList* list) {
    if (!list) return;
    for (size_t i = 0; i < list->length; i++) {
        DynValue* dv = (DynValue*) list->data[i];
        if (dv) {
            free(dv->value);
            free(dv);
        }
    }
    free(list->data);
    free(list);
}
