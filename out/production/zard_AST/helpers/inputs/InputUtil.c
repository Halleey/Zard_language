#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "InputUtil.h"
#include "Stringz.h"
int inputInt(const char* prompt) {
    char buffer[256];
    if (prompt && strlen(prompt) > 0) printf("%s: ", prompt);
    if (!fgets(buffer, sizeof(buffer), stdin)) return 0;
    return (int)strtol(buffer, NULL, 10);
}

double inputDouble(const char* prompt) {
    char buffer[256];
    if (prompt && strlen(prompt) > 0) printf("%s: ", prompt);
    if (!fgets(buffer, sizeof(buffer), stdin)) return 0.0;
    return strtod(buffer, NULL);
}

int inputBool(const char* prompt) {
    char buffer[256];
    if (prompt && strlen(prompt) > 0) printf("%s: ", prompt);
    if (!fgets(buffer, sizeof(buffer), stdin)) return 0;
    buffer[strcspn(buffer, "\n")] = 0;
    if (strcasecmp(buffer, "true") == 0) return 1;
    return 0;
}

char* inputString(const char* prompt) {
    static char buffer[256];
    if (prompt && strlen(prompt) > 0) printf("%s: ", prompt);
    if (!fgets(buffer, sizeof(buffer), stdin)) buffer[0] = '\0';
    buffer[strcspn(buffer, "\n")] = 0;
    return buffer;
}


char inputChar(const char* prompt) {
    char buffer[256];
    if (prompt && strlen(prompt) > 0) printf("%s: ", prompt);
    if (!fgets(buffer, sizeof(buffer), stdin)) return '\0';
    return buffer[0];
}
