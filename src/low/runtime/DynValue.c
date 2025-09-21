#include "DynValue.h"
#include <stdlib.h>
#include <string.h>

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
    strcpy(p, val);
    dv->type = TYPE_STRING;
    dv->value = p;
    return dv;
}
