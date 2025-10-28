#ifndef INPUTUTIL_H
#define INPUTUTIL_H


#ifdef _WIN32
#define strcasecmp _stricmp
#endif

int inputInt(const char* prompt);
double inputDouble(const char* prompt);
int inputBool(const char* prompt);
char* inputString(const char* prompt);
char inputChar(const char* prompt);
#endif
