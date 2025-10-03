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

void arraylist_add_int(ArrayList* list, int value) {
    ensureCapacity(list);
    int* ptr = malloc(sizeof(int));
    *ptr = value;
    list->data[list->length++] = ptr;
}

void arraylist_add_double(ArrayList* list, double value) {
    ensureCapacity(list);
    double* ptr = malloc(sizeof(double));
    *ptr = value;
    list->data[list->length++] = ptr;
}

void arraylist_add_string(ArrayList* list, char* str) {
    ensureCapacity(list);
    size_t len = strlen(str) + 1;
    char* copy = malloc(len);
    memcpy(copy, str, len);
    list->data[list->length++] = copy;
}

void arraylist_addAll_int(ArrayList* list, int* values, size_t n) {
    for (size_t i = 0; i < n; i++) arraylist_add_int(list, values[i]);
}

void arraylist_addAll_double(ArrayList* list, double* values, size_t n) {
    for (size_t i = 0; i < n; i++) arraylist_add_double(list, values[i]);
}

void arraylist_addAll_string(ArrayList* list, char** strings, size_t n) {
    for (size_t i = 0; i < n; i++) arraylist_add_string(list, strings[i]);
}

void removeItem(ArrayList* list, size_t index) {
    if (index >= list->length) return;
    free(list->data[index]);
    for (size_t i = index; i < list->length - 1; i++) list->data[i] = list->data[i+1];
    list->length--;
    list->data[list->length] = NULL;
}

int update_int(ArrayList* list, size_t index, int value) {
    if (index >= list->length) return 0;
    *(int*)list->data[index] = value;
    return 1;
}

int update_double(ArrayList* list, size_t index, double value) {
    if (index >= list->length) return 0;
    *(double*)list->data[index] = value;
    return 1;
}

int update_string(ArrayList* list, size_t index, char* str) {
    if (index >= list->length) return 0;
    list->data[index] = str;
    return 1;
}

void* getItem(ArrayList* list, size_t index) {
    if (index >= list->length) return NULL;
    return list->data[index];
}

int length(ArrayList* list) {
    return list ? list->length : 0;
}

void freeList(ArrayList* list) {
    if (!list) return;
    for (size_t i = 0; i < list->length; i++)
        if (list->data[i]) free(list->data[i]); // libera ints e doubles
    free(list->data);
    free(list);
}
