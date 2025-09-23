#include "DynValue.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#define _CRT_SECURE_NO_WARNINGS

DynValue* createInt(int val) {
    DynValue* dv = malloc(sizeof(DynValue));
    int* p = malloc(sizeof(int));
    *p = val;
    dv->type = TYPE_INT;
    dv->value = p;
    return dv;
}

DynValue* createDouble(double val) {
    DynValue* dv = malloc(sizeof(DynValue));
    double* p = malloc(sizeof(double));
    *p = val;
    dv->type = TYPE_DOUBLE;
    dv->value = p;
    return dv;
}

DynValue* createBool(int val) {
    DynValue* dv = malloc(sizeof(DynValue));
    int* p = malloc(sizeof(int));
    *p = val ? 1 : 0;
    dv->type = TYPE_BOOL;
    dv->value = p;
    return dv;
}

DynValue* createString(const char* val) {
    DynValue* dv = malloc(sizeof(DynValue));
    char* p = malloc(strlen(val)+1);
    strcpy_s(p, strlen(val) + 1, val);
    dv->type = TYPE_STRING;
    dv->value = p;
    return dv;
}

void printDynValue(DynValue* val) {
    if (!val) {
        printf("null\n");
        return;
    }

    switch (val->type) {
        case TYPE_INT:
            printf("%d\n", *(int*)val->value);
            break;
        case TYPE_DOUBLE:
            printf("%f\n", *(double*)val->value);
            break;
        case TYPE_BOOL:
            printf("%s\n", (*(int*)val->value) ? "true" : "false");
            break;
        case TYPE_STRING:
            printf("%s\n", (char*)val->value);
            break;
        default:
            printf("unknown\n");
    }
}
