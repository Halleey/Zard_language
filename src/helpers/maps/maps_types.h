#ifndef MAP_TYPES_H
#define MAP_TYPES_H

#include "map_template.h"

// special case
typedef const char* cstring;

// Define the map type to perform the conversion at pre-processing time
DEFINE_MAP(int, double)
DEFINE_MAP(cstring, int)
DEFINE_MAP(int, cstring)
DEFINE_MAP(cstring, cstring)

#endif
