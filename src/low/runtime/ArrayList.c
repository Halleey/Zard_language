#include "ArrayList.h"
#include <stdlib.h>
#include <stdio.h>

// Criação
ArrayList* arraylist_create(size_t initial_capacity) {
    ArrayList* list = malloc(sizeof(ArrayList));
    if (!list) return NULL;

    list->data = malloc(sizeof(DynValue*) * initial_capacity);
    if (!list->data) { free(list); return NULL; }

    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

// Adiciona único DynValue
void setItems(ArrayList* list, DynValue* data) {
    if (!list || !data) return;

    if (list->length == list->capacity) {
        size_t newCapacity = list->capacity * 2;
        DynValue** newData = realloc(list->data, sizeof(DynValue*) * newCapacity);
        if (!newData) { fprintf(stderr,"Falha ao salvar array\n"); return; }
        list->data = newData;
        list->capacity = newCapacity;
    }

    list->data[list->length++] = data;
}

// Adiciona múltiplos DynValues
void addAll(ArrayList* list, DynValue** data, size_t dataLength) {
    if (!list || !data) return;
    for (size_t i=0; i<dataLength; i++) {
        if (list->length == list->capacity) {
            size_t newCapacity = list->capacity*2;
            DynValue** newData = realloc(list->data, sizeof(DynValue*)*newCapacity);
            if (!newData) { fprintf(stderr,"Falha ao redimensionar array\n"); return; }
            list->data = newData;
            list->capacity = newCapacity;
        }
        DynValue* dvClone = cloneDynValue(data[i]);
        if (!dvClone) { fprintf(stderr,"Falha ao clonar DynValue\n"); continue; }
        list->data[list->length++] = dvClone;
    }
}

// Acesso
DynValue* getItem(ArrayList* list, size_t index) {
    if (!list || index>=list->length) { fprintf(stderr,"Index out of bounds\n"); return NULL; }
    return list->data[index];
}

void removeItem(ArrayList* list, size_t position) {
    if (!list || position>=list->length) { fprintf(stderr,"Position not found\n"); return; }
    freeDynValue(list->data[position]);
    for (size_t i=position;i<list->length-1;i++) list->data[i]=list->data[i+1];
    list->length--;
}

void clearList(ArrayList* list) {
    if (!list) return;
    for (size_t i=0;i<list->length;i++) freeDynValue(list->data[i]);
    list->length=0;
}

size_t size(ArrayList* list) { return list ? list->length : 0; }

void freeList(ArrayList* list) {
    if (!list) return;
    for (size_t i=0;i<list->length;i++) freeDynValue(list->data[i]);
    free(list->data);
    free(list);
}
