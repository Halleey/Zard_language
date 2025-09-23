#include "PrintList.h"
#include "DynValue.h"
#include <stdio.h>

void printList(ArrayList* list) {
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        DynValue* dv = (DynValue*) list->data[i];
        if (dv) printDynValue(dv);

        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}