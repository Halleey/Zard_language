#include "DynValue.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

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
    char* p = malloc(strlen(val) + 1);
    strcpy(p, val);
    dv->type = TYPE_STRING;
    dv->value = p;
    return dv;
}

DynValue* cloneDynValue(DynValue* src) {
    if (!src) return NULL;

    switch (src->type) {
        case TYPE_INT:
            return createInt(*(int*)src->value);
        case TYPE_DOUBLE:
            return createDouble(*(double*)src->value);
        case TYPE_BOOL:
            return createBool(*(int*)src->value);
        case TYPE_STRING:
            return createString((char*)src->value);
        default:
            return NULL;
    }
}

void freeDynValue(DynValue* dv) {
    if (!dv) return;
    if (dv->value) free(dv->value);
    free(dv);
}

int dynToInt(DynValue* dv) {
    if (!dv || dv->type != TYPE_INT) { fprintf(stderr,"DynValue não é int\n"); exit(1); }
    return *(int*)dv->value;
}

double dynToDouble(DynValue* dv) {
    if (!dv || dv->type != TYPE_DOUBLE) { fprintf(stderr,"DynValue não é double\n"); exit(1); }
    return *(double*)dv->value;
}

int dynToBool(DynValue* dv) {
    if (!dv || dv->type != TYPE_BOOL) { fprintf(stderr,"DynValue não é bool\n"); exit(1); }
    return *(int*)dv->value;
}

char* dynToString(DynValue* dv) {
    if (!dv || dv->type != TYPE_STRING) { fprintf(stderr,"DynValue não é string\n"); exit(1); }
    return (char*)dv->value;
}

void printDynValue(DynValue* dv) {
    if (!dv) {
        printf("null");
        return;
    }

    switch (dv->type) {
        case TYPE_INT:
            printf("%d", *(int*)dv->value);
            break;
        case TYPE_DOUBLE:
            printf("%f", *(double*)dv->value);
            break;
        case TYPE_BOOL:
            printf("%s", *(int*)dv->value ? "true" : "false");
            break;
        case TYPE_STRING:
            printf("%s", (char*)dv->value);
            break;
    }
}