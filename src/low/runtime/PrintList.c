#include "ArrayList.h"
#include "DynValue.h"
#include <stdio.h>

void printList(void* ptr) {
    ArrayList* list = (ArrayList*) ptr;
    if (!list) {
        printf("[]\n");
        return;
    }

    printf("[");
    for (size_t i = 0; i < list->length; i++) {
        DynValue* dv = (DynValue*) list->data[i];
        if (!dv) continue;

        switch(dv->type) {
            case TYPE_INT:
                printf("%d", *(int*)dv->value);
                break;
            case TYPE_DOUBLE:
                printf("%f", *(double*)dv->value);
                break;
            case TYPE_BOOL:
                printf("%s", (*(int*)dv->value) ? "true" : "false");
                break;
            case TYPE_STRING:
                printf("%s", (char*)dv->value);
                break;
        }

        if (i < list->length - 1)
            printf(", ");
    }
    printf("]\n");
}
