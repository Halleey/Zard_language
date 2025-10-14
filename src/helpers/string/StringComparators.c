#include "Stringz.h"

//  ==
_Bool strcmp_eq(String* a, String* b) {
    if (!a || !b) return 0; // null-safe
    if (a->length != b->length) return 0;
    for (size_t i = 0; i < a->length; i++) {
        if (a->data[i] != b->data[i]) return 0;
    }
    return 1;
}

//  !=
_Bool strcmp_neq(String* a, String* b) {
    return !strcmp_eq(a, b);
}
