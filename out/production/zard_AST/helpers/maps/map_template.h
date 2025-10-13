#ifndef MAP_TEMPLATE_H
#define MAP_TEMPLATE_H

#include <stdlib.h>
#include <string.h>


//Criado um macro para definir de modo generico toda a estrutura dos mapas
#define DEFINE_MAP(KEY_TYPE, VALUE_TYPE) \
typedef struct { \
    KEY_TYPE key; \
    VALUE_TYPE value; \
} MapEntry_##KEY_TYPE##_##VALUE_TYPE; \
\
typedef struct { \
    MapEntry_##KEY_TYPE##_##VALUE_TYPE *data; \
    size_t size; \
    size_t capacity; \
} Map_##KEY_TYPE##_##VALUE_TYPE; \
\
static inline Map_##KEY_TYPE##_##VALUE_TYPE* createMap_##KEY_TYPE##_##VALUE_TYPE() { \
    Map_##KEY_TYPE##_##VALUE_TYPE *map = malloc(sizeof(*map)); \
    map->size = 0; \
    map->capacity = 4; \
    map->data = malloc(map->capacity * sizeof(MapEntry_##KEY_TYPE##_##VALUE_TYPE)); \
    return map; \
} \
\
static inline void mapPut_##KEY_TYPE##_##VALUE_TYPE(Map_##KEY_TYPE##_##VALUE_TYPE *map, KEY_TYPE key, VALUE_TYPE value) { \
    for (size_t i = 0; i < map->size; i++) { \
        if (memcmp(&map->data[i].key, &key, sizeof(KEY_TYPE)) == 0) { \
            map->data[i].value = value; \
            return; \
        } \
    } \
    if (map->size >= map->capacity) { \
        map->capacity *= 2; \
        map->data = realloc(map->data, map->capacity * sizeof(MapEntry_##KEY_TYPE##_##VALUE_TYPE)); \
    } \
    map->data[map->size].key = key; \
    map->data[map->size].value = value; \
    map->size++; \
} \
\
static inline VALUE_TYPE mapGet_##KEY_TYPE##_##VALUE_TYPE(Map_##KEY_TYPE##_##VALUE_TYPE *map, KEY_TYPE key) { \
    for (size_t i = 0; i < map->size; i++) { \
        if (memcmp(&map->data[i].key, &key, sizeof(KEY_TYPE)) == 0) { \
            return map->data[i].value; \
        } \
    } \
    VALUE_TYPE empty = {0}; \
    return empty; \
} \
\
static inline void freeMap_##KEY_TYPE##_##VALUE_TYPE(Map_##KEY_TYPE##_##VALUE_TYPE *map) { \
    free(map->data); \
    free(map); \
}

#endif
