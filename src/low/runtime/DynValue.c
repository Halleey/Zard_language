#include "DynValue.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

static char* strdup_portable(const char* s) {
    if (!s) return NULL;
    size_t n = strlen(s) + 1;
    char* r = (char*)malloc(n);
    if (!r) return NULL;
    memcpy(r, s, n);
    return r;
}

DynValue* cloneDynValue(DynValue* val) {
    if (!val) return NULL;

    DynValue* copy = malloc(sizeof(DynValue));
    if (!copy) return NULL;
    copy->type = val->type;
    copy->value = NULL;

    switch (val->type) {
        case TYPE_INT: {
            int* i = malloc(sizeof(int));
            if (!i) { free(copy); return NULL; }
            *i = *(int*)val->value;
            copy->value = i;
            break;
        }
        case TYPE_DOUBLE: {
            double* d = malloc(sizeof(double));
            if (!d) { free(copy); return NULL; }
            *d = *(double*)val->value;
            copy->value = d;
            break;
        }
        case TYPE_BOOL: {
            int* b = malloc(sizeof(int));
            if (!b) { free(copy); return NULL; }
            *b = *(int*)val->value;
            copy->value = b;
            break;
        }
        case TYPE_STRING: {
            char* s = strdup_portable((char*)val->value);
            if (!s) { free(copy); return NULL; }
            copy->value = s;
            break;
        }
        default:
            free(copy);
            return NULL;
    }

    return copy;
}

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
    if (!dv) return NULL;
    char* p = strdup_portable(val);
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

void freeDynValue(DynValue* dv) {
    if (!dv) return;
    if (dv->value) free(dv->value);
    free(dv);
}
