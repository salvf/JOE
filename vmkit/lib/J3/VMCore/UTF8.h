//===------------------- UTF8.h - Utilities for UTF8 ----------------------===//
//
//                            The VMKit project
//
// This file is distributed under the University of Illinois Open Source
// License. See LICENSE.TXT for details.
//
//===----------------------------------------------------------------------===//

#ifndef _JNJVM_UTF8_H_
#define _JNJVM_UTF8_H_

#include "types.h"
#include <map>
#include "MvmDenseMap.h"
#include "MvmDenseSet.h"

namespace j3 {

class UTF8Map;

class UTF8 {
  friend class UTF8Map;
private:

public:
  /// size - The (constant) size of the UTF8.
  ssize_t size;

  /// elements - Elements of this UTF8.
  /// The size should be set to zero, but this is invalid C99.
  uint16* elements;

  /// extract - Extracts an UTF8 from the current UTF8
  const UTF8* extract(UTF8Map* map, uint32 start, uint32 len) const;

  /// equals - Are the two UTF8s equal?
  bool equals(const UTF8* other) const {
    if (other == this) return true;
    else if (size != other->size) return false;
    else return !memcmp(elements, other->elements, size * sizeof(uint16));
  }

  /// equals - Does the UTF8 equal to the buffer?
  bool equals(const uint16* buf, sint32 len) const {
    if (size != len) return false;
    else return !memcmp(elements, buf, size * sizeof(uint16));
  }

  /// lessThan - strcmp-like function for UTF8s, used by hash tables.
  bool lessThan(const UTF8* other) const {
    if (size < other->size) return true;
    else if (size > other->size) return false;
    else return memcmp((const char*)elements, (const char*)other->elements,
                       size * sizeof(uint16)) < 0;
  }

	static uint32_t readerHasher(const uint16* buf, sint32 size);

	uint32_t hash() const {
		return readerHasher(elements, size);
	}

  UTF8(sint32 n) {
    size = n;
    if(n >= 0)
    	elements = new uint16_t[n];
    else
    	elements = NULL;
  }
};

extern "C" const UTF8 TombstoneKey;
extern "C" const UTF8 EmptyKey;

struct UTF8MapKey {
  ssize_t length;
  const uint16_t* data;

  UTF8MapKey(const uint16_t* d, ssize_t l) {
    data = d;
    length = l;
  }
};

// Provide MvmDenseMapInfo for UTF8.
template<>
struct MvmDenseMapInfo<const UTF8*> {
  static inline const UTF8* getEmptyKey() {
    return &EmptyKey;
  }
  static inline const UTF8* getTombstoneKey() {
    return &TombstoneKey;
  }
  static unsigned getHashValue(const UTF8* PtrVal) {
    return PtrVal->hash();
  }
  static bool isEqual(const UTF8* LHS, const UTF8* RHS) { return LHS->equals(RHS); }
  static bool isEqualKey(const UTF8* LHS, const UTF8MapKey& Key) {
    return LHS->equals(Key.data, Key.length);
  }
  static UTF8MapKey toKey(const UTF8* utf8) {
    return UTF8MapKey(utf8->elements, utf8->size);
  }
};


// Provide MvmDenseMapInfo for UTF8MapKey.
template<>
struct MvmDenseMapInfo<UTF8MapKey> {
  static inline const UTF8MapKey getEmptyKey() {
    static UTF8MapKey EmptyKey(NULL, -1);
    return EmptyKey;
  }
  static inline const UTF8MapKey getTombstoneKey() {
    static UTF8MapKey TombstoneKey(NULL, -2);
    return TombstoneKey;
  }
  static unsigned getHashValue(const UTF8MapKey& key) {
    return UTF8::readerHasher(key.data, key.length);
  }
  static bool isEqual(const UTF8MapKey& LHS, const UTF8MapKey& RHS) {
    if (LHS.data == RHS.data) return true;
    if (LHS.length != RHS.length) return false;
    return !memcmp(LHS.data, RHS.data, RHS.length * sizeof(uint16));
  }
};

class UTF8Map {
public:
  typedef MvmDenseSet<UTF8MapKey, const UTF8*>::iterator iterator;

  MvmDenseSet<UTF8MapKey, const UTF8*> map;

  const UTF8* lookupOrCreateAsciiz(const char* asciiz);
  const UTF8* lookupOrCreateReader(const uint16* buf, uint32 size);
  const UTF8* lookupAsciiz(const char* asciiz);
  const UTF8* lookupReader(const uint16* buf, uint32 size);

  UTF8Map() {}
  UTF8Map(MvmDenseSet<UTF8MapKey, const UTF8*>* m)
      : map(*m) {}

  ~UTF8Map() {
//    for (iterator i = map.begin(), e = map.end(); i!= e; ++i) {
//      allocator.Deallocate((void*)*i);
//    }
  }
};

class UTF8Builder {
	uint16 *buf;
	uint32  cur;
	uint32  size;

public:
	UTF8Builder(size_t size) {
		size = (size < 4) ? 4 : size;
		this->buf = new uint16[size];
		this->size = size;
	}

	UTF8Builder *append(const UTF8 *utf8, uint32 start=0, ssize_t length=-1) {
		length = length == -1 ? utf8->size : length;
		uint32 req = cur + length;

		if(req > size) {
			uint32 newSize = size<<1;
			while(req < newSize)
				newSize <<= 1;
			uint16 *newBuf = new uint16[newSize];
			memcpy(newBuf, buf, cur<<1);
			delete []buf;
			buf = newBuf;
			size = newSize;
		}

		memcpy(buf + cur, &utf8->elements + start, length<<1);
		cur = req;

		return this;
	}

	const UTF8 *toUTF8(UTF8Map *map) {
		return map->lookupOrCreateReader(buf, size);
	}

	~UTF8Builder() {
		delete [] buf;
	}
};

/// UTF8Buffer - Helper class to create char* buffers suitable for
/// printf.
///
class UTF8Buffer {
public:
  char* contents;

  /// UTF8Buffer - Create a buffer with the following UTF8.
  UTF8Buffer(const UTF8* val) {
    contents = new char[val->size + 1];
    for (int i = 0; i < val->size; i++) {
      contents[i] = (char)val->elements[i];
    }
    contents[val->size] = 0;
  }

  char* cString() const {
    return contents;
  }

  /// toCompileName - Change the utf8 following JNI conventions.
  ///
  UTF8Buffer* toCompileName(const char* suffix = "") {
		const char *buffer = contents;
    uint32 len = strlen(buffer);
    uint32 suffixLen = strlen(suffix);
    char* newBuffer = new char[(len << 3) + suffixLen + 1];
    uint32 j = 0;
    for (uint32 i = 0; i < len; ++i) {
      if (buffer[i] == '/') {
        newBuffer[j++] = '_';
      } else if (buffer[i] == '_') {
        newBuffer[j++] = '_';
        newBuffer[j++] = '1';
      } else if (buffer[i] == ';') {
        newBuffer[j++] = '_';
        newBuffer[j++] = '2';
      } else if (buffer[i] == '[') {
        newBuffer[j++] = '_';
        newBuffer[j++] = '3';
      } else if (buffer[i] == '$') {
        newBuffer[j++] = '_';
        newBuffer[j++] = '0';
        newBuffer[j++] = '0';
        newBuffer[j++] = '0';
        newBuffer[j++] = '2';
        newBuffer[j++] = '4';
      } else {
        newBuffer[j++] = buffer[i];
      }
    }
    for (uint32 i = 0; i < suffixLen; i++) {
      newBuffer[j++] = suffix[i];
    }
    newBuffer[j] = 0;
    delete[] contents;
		contents = newBuffer;
    return this;
  }

  ~UTF8Buffer() {
    delete[] contents;
  }
};

}

#endif
