#ifndef DYNVALUE_H
#define DYNVALUE_H

#include <stddef.h>

typedef enum { TYPE_INT, TYPE_DOUBLE, TYPE_BOOL, TYPE_STRING } ValueType;

typedef struct {
    ValueType type;
    void* value;  // ponteiro para valor real (int*, double*, char*, etc.)
} DynValue;


DynValue* createInt(int val);
DynValue* createDouble(double val);
DynValue* createBool(int val);
DynValue* createString(const char* val);


void printDynValue(DynValue* val);
DynValue* cloneDynValue(DynValue* val);
void freeDynValue(DynValue* val);

#endif
