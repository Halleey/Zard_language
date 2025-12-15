#include "ArrayList.h"
#include <stdio.h>
#include <stdlib.h>


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

static void ensureCapacity(ArrayList* list) {
    if (list->length < list->capacity) return;

    size_t newCapacity = list->capacity == 0 ? 4 : list->capacity * 2;
    void** newData = realloc(list->data, sizeof(void*) * newCapacity);
    if (!newData) {
        fprintf(stderr, "[ArrayList] Falha ao realocar\n");
        abort();
    }

    list->data = newData;
    list->capacity = newCapacity;
}

void arraylist_add_ptr(ArrayList* list, void* element) {
    ensureCapacity(list);
    list->data[list->length++] = element;
}

void* arraylist_get_ptr(ArrayList* list, size_t index) {
    if (!list || index >= list->length) {
        fprintf(stderr, "[ArrayList] Acesso inválido (%zu)\n", index);
        abort();
    }
    return list->data[index];
}

size_t length(ArrayList* list) {
    return list ? list->length : 0;
}

void arraylist_add_String(ArrayList* list, String* str) {
    if (!list || !str) return;

    ensureCapacity(list);

    String* copy = createString(str->data);
    list->data[list->length++] = copy;
}

void arraylist_addAll_String(ArrayList* list, String** strings, size_t n) {
    for (size_t i = 0; i < n; i++) {
        arraylist_add_String(list, strings[i]);
    }
}



void removeItem(ArrayList* list, size_t index) {
    if (!list || index >= list->length) return;

    String* s = (String*) list->data[index];
    if (s) freeString(s);

    for (size_t i = index; i + 1 < list->length; i++) {
        list->data[i] = list->data[i + 1];
    }

    list->length--;
    list->data[list->length] = NULL;
}

void clearList(ArrayList* list) {
    if (!list) return;

    for (size_t i = 0; i < list->length; i++) {
        String* s = (String*) list->data[i];
        if (s) freeString(s);
    }

    list->length = 0;
}

void freeList(ArrayList* list) {
    if (!list) return;

    clearList(list);
    free(list->data);
    free(list);
}
