#include "ArrayListInt.h"

ArrayListInt* arraylist_create_int(size_t initial_capacity) {
    ArrayListInt* list = malloc(sizeof(ArrayListInt));
    if (!list) return NULL;
    list->data = malloc(sizeof(int) * initial_capacity);
    if (!list->data) { free(list); return NULL; }
    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

static void ensureCapacityInt(ArrayListInt* list) {
    if (list->length == list->capacity) {
        size_t newCap = list->capacity * 2;
        int* newData = realloc(list->data, sizeof(int) * newCap);
        if (!newData) { fprintf(stderr, "Failed to resize ArrayListInt\n"); return; }
        list->data = newData;
        list->capacity = newCap;
    }
}

void arraylist_add_int(ArrayListInt* list, int value) {
    ensureCapacityInt(list);
    list->data[list->length++] = value;
}

void arraylist_addAll_int(ArrayListInt* list, int* values, size_t n) {
    for (size_t i = 0; i < n; i++) arraylist_add_int(list, values[i]);
}

int arraylist_update_int(ArrayListInt* list, size_t index, int value) {
    if (index >= list->length) return 0;
    list->data[index] = value;
    return 1;
}

int arraylist_get_int(ArrayListInt* list, size_t index, int* out) {
    if (index >= list->length) return 0;
    *out = list->data[index];
    return 1;
}

int arraylist_size_int(ArrayListInt* list) {
    return list ? list->length : 0;
}

void arraylist_remove_int(ArrayListInt* list, size_t index) {
    if (index >= list->length) return;
    for (size_t i = index; i < list->length - 1; i++)
        list->data[i] = list->data[i + 1];
    list->length--;
}

void arraylist_clear_int(ArrayListInt* list) {
    list->length = 0;
}

void arraylist_free_int(ArrayListInt* list) {
    if (!list) return;
    free(list->data);
    free(list);
}
