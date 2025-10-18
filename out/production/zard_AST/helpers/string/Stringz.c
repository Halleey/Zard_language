#include <stdlib.h>
#include <stdio.h>
#include "Stringz.h"

String* createString(const char *text) {
    if (text == NULL) text = "";
    String *str = malloc(sizeof(String));
    if (!str) return NULL;

    str->length = my_strlen(text);
    str->data = malloc(str->length + 1);
    if (str->data != NULL) {
        my_copyString(text, str->data);
    }
    return str;
}

void setString(String *str, const char *data) {
    if (!str) return;
    if (data == NULL) data = "";

    free(str->data);
    str->length = my_strlen(data);
    str->data = malloc(str->length + 1);
    if (str->data != NULL) {
        my_copyString(data, str->data);
    }
}

void concatString(String *str, const char *nConcat) {
    if (!str) return;
    if (nConcat == NULL) nConcat = "";

    size_t extraLength = my_strlen(nConcat);
    size_t newLength = str->length + extraLength;
    char *data = malloc(newLength + 1);
    if (data != NULL) {
        my_copyString(str->data, data);
        my_copyString(nConcat, data + str->length);
        free(str->data);
        str->data = data;
        str->length = newLength;
    }
}

void toUpperCase(String *str) {
    if (!str) return;
    for (size_t i = 0; i < str->length; i++) {
        if (str->data[i] >= 'a' && str->data[i] <= 'z') {
            str->data[i] -= ('a' - 'A');
        }
    }
}

void toLowerCase(String *str) {
    if (!str) return;
    for (size_t i = 0; i < str->length; i++) {
        if (str->data[i] >= 'A' && str->data[i] <= 'Z') {
            str->data[i] += ('a' - 'A');
        }
    }
}

void reverseString(String *str) {
    if (!str) return;
    int i = 0, j = str->length - 1;
    while (i < j) {
        char temp = str->data[i];
        str->data[i] = str->data[j];
        str->data[j] = temp;
        i++;
        j--;
    }
}

int compareString(String *strOne, String *strTwo) {
    if (!strOne || !strTwo) return 0;
    if (strOne->length != strTwo->length) return 0;
    for (size_t i = 0; i < strOne->length; i++) {
        if (strOne->data[i] != strTwo->data[i]) return 0;
    }
    return 1;
}

int startWith(String *str, const char *prefix) {
    if (!str || prefix == NULL) return 0;
    size_t prefixLength = my_strlen(prefix);
    if (prefixLength > str->length) return 0;
    for (size_t i = 0; i < prefixLength; i++) {
        if (str->data[i] != prefix[i]) return 0;
    }
    return 1;
}

void printString(String *str) {
    if (str != NULL && str->data != NULL) {
        printf("%s\n", str->data);
    } else {
        printf("(string vazia)\n");
    }
}

void freeString(String *str) {
    if (!str) return;
    free(str->data);
    free(str);
}

size_t my_strlen(const char *text) {
    if (text == NULL) return 0;
    size_t length = 0;
    while (text[length] != '\0') length++;
    return length;
}

size_t my_copyString(const char *origem, char *destino) {
    if (origem == NULL) {
        destino[0] = '\0';
        return 0;
    }
    size_t length = 0;
    while (origem[length] != '\0') {
        destino[length] = origem[length];
        length++;
    }
    destino[length] = '\0';
    return length;
}
