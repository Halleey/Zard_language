#ifndef DYNVALUE_H
#define DYNVALUE_H

typedef enum { TYPE_INT, TYPE_DOUBLE, TYPE_BOOL, TYPE_STRING } ValueType;

typedef struct {
    ValueType type;
    void* value;  // ponteiro para valor real (int*, double*, etc.)
} DynValue;


DynValue* createInt(int val);
DynValue* createDouble(double val);
DynValue* createBool(int val);
DynValue* createString(const char* val);
void printDynValue(DynValue* val);
#endif
