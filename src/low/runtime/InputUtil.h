#ifndef INPUTUTIL_H
#define INPUTUTIL_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#define strcasecmp _stricmp
#endif

int inputInt(const char* prompt);
double inputDouble(const char* prompt);
int inputBool(const char* prompt);
char* inputString(const char* prompt);

#endif
