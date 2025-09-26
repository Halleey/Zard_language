#include "InputUtil.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#define strcasecmp _stricmp
#endif

DynValue* input(const char* prompt) {
    char buffer[256];

    if (prompt && strlen(prompt) > 0) {
        printf("%s: ", prompt);
    }

    if (!fgets(buffer, sizeof(buffer), stdin)) {
        return createString("");
    }


    buffer[strcspn(buffer, "\n")] = 0;


    char* end;
    long intValue = strtol(buffer, &end, 10);
    if (*end == '\0') {
        return createInt((int)intValue);
    }


    double doubleValue = strtod(buffer, &end);
    if (*end == '\0') {
        return createDouble(doubleValue);
    }


    if (strcasecmp(buffer, "true") == 0) return createBool(1);
    if (strcasecmp(buffer, "false") == 0) return createBool(0);
    return createString(buffer);
}
