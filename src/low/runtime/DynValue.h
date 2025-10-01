#ifndef DYNVALUE_H
#define DYNVALUE_H

typedef enum { TYPE_INT, TYPE_DOUBLE, TYPE_BOOL, TYPE_STRING } ValueType;

typedef struct {
    ValueType type;
    void* value;  // ponteiro para valor real (int*, double*, etc.)
} DynValue;

// Criação
DynValue* createInt(int val);
DynValue* createDouble(double val);
DynValue* createBool(int val);
DynValue* createString(const char* val);

// Liberação e clonagem
void freeDynValue(DynValue *dv);
DynValue* cloneDynValue(DynValue* src);

// Conversão
int dynToInt(DynValue* dv);
double dynToDouble(DynValue* dv);
int dynToBool(DynValue* dv);
char* dynToString(DynValue* dv);
//prints
void printDynValue(DynValue* dv);
#endif