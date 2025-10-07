#include "PrintList.h"
#include <stdio.h>

void arraylist_print_int(ArrayListInt* list) {
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        printf("%d", list->data[i]);
        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}

void arraylist_print_double(ArrayListDouble* list) {
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        printf("%f", list->data[i]);
        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}

void arraylist_print_string(ArrayList* list) {
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        char* val = (char*) list->data[i];
        printf("%s", val);
        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}
