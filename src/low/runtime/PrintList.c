#include "PrintList.h"
#include <stdio.h>


void arraylist_print_int(ArrayList* list){
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        int* val = (int*) list->data[i];
        printf("%d", *val);

        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}

void arraylist_print_double(ArrayList* list){
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        double* val = (double*) list->data[i];
        printf("%f", *val);

        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}

void arraylist_print_string(ArrayList* list){
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        char* val = (char*) list->data[i]; // ponteiro para string
        printf("%s", val);

        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}
