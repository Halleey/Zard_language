#include "ArrayListBool.h"

static void ensureCapacityBool(ArrayListBool* list) {
    if (list->length >= list->capacity) {
        size_t newCapacity = list->capacity * 2;
        bool* newData = realloc(list->data, newCapacity * sizeof(bool));
        if (!newData) {
            fprintf(stderr, "Erro de realloc em ArrayListBool\n");
            exit(1);
        }
        list->data = newData;
        list->capacity = newCapacity;
    }
}

ArrayListBool* arraylist_create_bool(size_t initial_capacity) {
    ArrayListBool* list = malloc(sizeof(ArrayListBool));
    list->data = malloc(initial_capacity * sizeof(bool));
    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

void arraylist_addAll_bool(ArrayListBool* list, uint8_t* values, size_t n) {
    for (size_t i = 0; i < n; i++) {
        arraylist_add_bool(list, values[i] ? true : false);
    }
}


void arraylist_add_bool(ArrayListBool* list, bool value) {
    ensureCapacityBool(list);
    list->data[list->length++] = value;
}



void arraylist_remove_bool(ArrayListBool* list, size_t index) {
    if (index >= list->length) return;
    for (size_t i = index; i < list->length - 1; i++) {
        list->data[i] = list->data[i+1];
    }
    list->length--;
}

void arraylist_clear_bool(ArrayListBool* list) {
    list->length = 0;
}

void arraylist_free_bool(ArrayListBool* list) {
    if (!list) return;
    free(list->data);
    free(list);
}

int arraylist_update_bool(ArrayListBool* list, size_t index, bool value) {
    if (index >= list->length) return 0;
    list->data[index] = value;
    return 1;
}

int arraylist_get_bool(ArrayListBool* list, size_t index, bool* out) {
    if (index >= list->length) return 0;
    *out = list->data[index];
    return 1;
}

int arraylist_size_bool(ArrayListBool* list) {
    return (int)list->length;
}
