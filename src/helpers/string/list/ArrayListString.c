#include "ArrayListString.h"
#include <stdio.h>
#include <stdlib.h>

ArrayListString* arraylist_string_create(size_t initial_capacity) {
    if (initial_capacity == 0) initial_capacity = 4;
    ArrayListString* list = malloc(sizeof(ArrayListString));
    if (!list) return NULL;

    list->data = malloc(sizeof(String*) * initial_capacity);
    if (!list->data) {
    free(list); return NULL;
     }

    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

static void ensureCapacity(ArrayListString* list) {
    if (list->length >= list->capacity) {
        size_t newCapacity = list->capacity * 2;
        String** newData = realloc(list->data, sizeof(String*) * newCapacity);
        if (!newData) {
            fprintf(stderr, "[ArrayListString] Falha ao redimensionar\n");
            return;
        }
        list->data = newData;
        list->capacity = newCapacity;
    }
}

void arraylist_string_add(ArrayListString* list, String* str) {
    if (!list || !str) return;
    ensureCapacity(list);
    list->data[list->length++] = str;
}

void arraylist_string_addAll(ArrayListString* list, String** strings, size_t n) {
    if (!list || !strings) return;
    for (size_t i = 0; i < n; i++) {
        arraylist_string_add(list, strings[i]);
    }
}

String* arraylist_string_get(ArrayListString* list, size_t index) {
    if (!list || index >= list->length) return NULL;
    return list->data[index];
}

// Remove e libera a String
void arraylist_string_remove(ArrayListString* list, size_t index) {
    if (!list || index >= list->length) return;
    if (list->data[index]) freeString(list->data[index]); // libera a String

    for (size_t i = index; i < list->length - 1; i++) {
        list->data[i] = list->data[i+1];
    }
    list->length--;
    list->data[list->length] = NULL;
}

void arraylist_string_free(ArrayListString* list) {
    if (!list) return;
    for (size_t i = 0; i < list->length; i++) {
        if (list->data[i]) freeString(list->data[i]);
    }
    free(list->data);
    free(list);
}

void arraylist_string_freeRef(ArrayListString* list) {
    if (!list) return;
    free(list->data);
    free(list);
}

size_t arraylist_string_length(ArrayListString* list) {
    return list ? list->length : 0;
}