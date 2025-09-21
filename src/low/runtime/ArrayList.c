#include "ArrayList.h"
#include <stdlib.h>
#include <stdio.h>


ArrayList *arraylist_create(size_t initial_capacity) {
    ArrayList *list = malloc(sizeof(ArrayList));
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

void setItems(ArrayList *list, void *data)
{
    if (list->length == list->capacity) {
        size_t newCapacity = list->capacity * 2;
        void **newData = realloc(list->data, sizeof(void*) * newCapacity);
        if (!newData) {
            fprintf(stderr, "Falha ao salvar array\n");
            return;
        }
        list->data = newData;
        list->capacity = newCapacity;
    }

    list->data[list->length++] = data;
}


void removeItem(ArrayList *list, size_t position)
{
    if(position >= list->length){
        fprintf(stderr, "This position was not found");
        return;
    }
    for (size_t i = position; i < list->length -1; i++)
    {
       list->data[i] = list->data[i+1];

    }
    list->length--;
}

void freeList(ArrayList *list)
{
    if (list == NULL) return;

    // Libera os elementos armazenados
    for (size_t i = 0; i < list->length; i++) {
        free(list->data[i]);
    }

    // Agora libera o array interno
    if (list->data != NULL) {
        free(list->data);
        list->data = NULL;
    }

    list->length = 0;
    list->capacity = 0;

    free(list);
}

void *getItem(ArrayList *list, size_t index)
{
    if(index >= list->length){
        fprintf(stderr, "index of outbound");
        return NULL;
    }
    return list->data[index];
}
