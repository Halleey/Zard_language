#include "ArrayListBool.h"

static void ensureCapacityBool(ArrayListBool* list) {
    if (list->length >= list->capacity) {
        size_t newCapacity = list->capacity * 2;
        uint8_t* newData = realloc(list->data, newCapacity * sizeof(uint8_t));
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
    if (!list) {
        fprintf(stderr, "Erro malloc ArrayListBool\n");
        exit(1);
    }
    list->data = malloc(initial_capacity * sizeof(uint8_t));
    if (!list->data) {
        fprintf(stderr, "Erro malloc data ArrayListBool\n");
        exit(1);
    }
    list->length = 0;
    list->capacity = initial_capacity;
    return list;
}

void arraylist_free_bool(ArrayListBool* list) {
    if (!list) return;
    free(list->data);
    free(list);
}
void arraylist_add_bool(ArrayListBool* list, bool value) {
    ensureCapacityBool(list);
    list->data[list->length++] = value ? 1 : 0;
}

void arraylist_addAll_bool(ArrayListBool* list, uint8_t* values, size_t n) {
    for (size_t i = 0; i < n; i++) {
        arraylist_add_bool(list, values[i] != 0);
    }
}
void arraylist_remove_bool(ArrayListBool* list, size_t index) {
    if (index >= list->length) return;
    for (size_t i = index; i < list->length - 1; i++) {
        list->data[i] = list->data[i + 1];
    }
    list->length--;
}

void arraylist_clear_bool(ArrayListBool* list) {
    list->length = 0;
}
int arraylist_update_bool(ArrayListBool* list, size_t index, bool value) {
    if (index >= list->length) return 0;
    list->data[index] = value ? 1 : 0;
    return 1;
}

int arraylist_get_bool(ArrayListBool* list, size_t index, bool* out) {
    if (index >= list->length) return 0;
    *out = list->data[index] != 0;
    return 1;
}

int arraylist_size_bool(ArrayListBool* list) {
    return (int)list->length;
}
