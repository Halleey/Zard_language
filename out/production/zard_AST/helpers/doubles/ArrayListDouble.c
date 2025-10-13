#include "ArrayListDouble.h"
#include <stdlib.h>
#include <stdio.h>

ArrayListDouble* arraylist_create_double(size_t initial_capacity) {
    ArrayListDouble* list = malloc(sizeof(ArrayListDouble));
    if (!list) return NULL;
    list->data = malloc(sizeof(double) * initial_capacity);
    if (!list->data) {
        free(list);
        return NULL;
    }
    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

static void ensureCapacityDouble(ArrayListDouble* list) {
    if (list->length == list->capacity) {
        size_t newCap = list->capacity * 2;
        double* newData = realloc(list->data, sizeof(double) * newCap);
        if (!newData) {
            fprintf(stderr, "Failed to resize ArrayListDouble\n");
            return;
        }
        list->data = newData;
        list->capacity = newCap;
    }
}

void arraylist_add_double(ArrayListDouble* list, double value) {
    ensureCapacityDouble(list);
    list->data[list->length++] = value;
}

void arraylist_addAll_double(ArrayListDouble* list, double* values, size_t n) {
    for (size_t i = 0; i < n; i++) {
        arraylist_add_double(list, values[i]);
    }
}

int arraylist_update_double(ArrayListDouble* list, size_t index, double value) {
    if (index >= list->length) return 0;
    list->data[index] = value;
    return 1;
}

int arraylist_get_double(ArrayListDouble* list, size_t index, double* out) {
    if (index >= list->length) return 0;
    *out = list->data[index];
    return 1;
}

void arraylist_remove_double(ArrayListDouble* list, size_t index) {
    if (index >= list->length) return;
    for (size_t i = index; i < list->length - 1; i++) {
        list->data[i] = list->data[i + 1];
    }
    list->length--;
}

void arraylist_clear_double(ArrayListDouble* list) {
    list->length = 0;
}

void arraylist_free_double(ArrayListDouble* list) {
    if (!list) return;
    free(list->data);
    free(list);
}
int arraylist_size_double(ArrayListDouble* list) {
    return list ? list->length : 0;
}
