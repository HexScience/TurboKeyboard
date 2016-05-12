/*
 * Copyright (C) 2013, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * !!!!! DO NOT EDIT THIS FILE !!!!!
 *
 * This file was generated from
 *   dictionary/structure/v4/ver4_patricia_trie_reading_utils.h
 */

#ifndef TURBOKEYBOARD_BACKWARD_V402_VER4_PATRICIA_TRIE_READING_UTILS_H
#define TURBOKEYBOARD_BACKWARD_V402_VER4_PATRICIA_TRIE_READING_UTILS_H

#include <cstdint>

#include "defines.h"

namespace turbokeyboard {
namespace backward {
namespace v402 {

} // namespace v402
} // namespace backward
class BufferWithExtendableBuffer;
namespace backward {
namespace v402 {

class Ver4PatriciaTrieReadingUtils {
 public:
    static int getTerminalIdAndAdvancePosition(const uint8_t *const buffer,
            int *const pos);

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(Ver4PatriciaTrieReadingUtils);
};
} // namespace v402
} // namespace backward
} // namespace turbokeyboard
#endif /* TURBOKEYBOARD_BACKWARD_V402_VER4_PATRICIA_TRIE_READING_UTILS_H */
