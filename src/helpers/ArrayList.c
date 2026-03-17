#include "ArrayList.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


ArrayList* arraylist_create(size_t initial_capacity) {
    ArrayList* list = malloc(sizeof(ArrayList));
    if (!list) return NULL;
    list->data = malloc(sizeof(void*) * initial_capacity);
    if (!list->data) { free(list); return NULL; }
    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

static void ensureCapacity(ArrayList* list) {
    if (list->length == list->capacity) {
        size_t newCapacity = list->capacity * 2;
        void **newData = realloc(list->data, sizeof(void*) * newCapacity);
        if (!newData) { fprintf(stderr, "Falha ao redimensionar ArrayList\n"); return; }
        list->data = newData;
        list->capacity = newCapacity;
    }
}


void arraylist_add_ptr(ArrayList* list, void* element) {
    ensureCapacity(list);
    list->data[list->length++] = element;
}

void* arraylist_get_ptr(ArrayList* list, size_t index) {
    if (!list) {
        fprintf(stderr, "[ArrayList] Erro: lista nula.\n");
        abort();
    }
    if (index >= list->length) {
        fprintf(stderr, "[ArrayList] Erro: acesso inválido no índice %zu (tamanho = %zu)\n",
                index, list->length);
        abort();
    }
    return list->data[index];
}



void removeItem(ArrayList* list, size_t index) {
    if (index >= list->length) return;
    free(list->data[index]);
    for (size_t i = index; i < list->length - 1; i++) list->data[i] = list->data[i+1];
    list->length--;
    list->data[list->length] = NULL;
}


int length(ArrayList* list) {
    return list ? list->length : 0;
}


/*
fiz uma pequena mudança,agora libera os itens internos mas provavel q tera 2 metodos
 um para referencias e outro objets bruto
*/
void freeList(ArrayList* list) {
    if (!list) return;

    for (size_t i = 0; i < list->length; i++) {
        if (list->data[i]) {
            free(list->data[i]);
        }
    }

    free(list->data);
    free(list);
}


void freeListRef(ArrayList* list){
    if(!list) return;

    free(list->data);
    free(list);
}

void clearList(ArrayList* list) {
    if (!list) return;

    for (size_t i = 0; i < list->length; i++) {
        list->data[i] = NULL;
    }
    list->length = 0;
}
