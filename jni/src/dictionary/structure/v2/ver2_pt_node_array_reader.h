/*
 * Copyright (C) 2014, The Android Open Source Project
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

#ifndef TURBOKEYBOARD_VER2_PT_NODE_ARRAY_READER_H
#define TURBOKEYBOARD_VER2_PT_NODE_ARRAY_READER_H

#include <cstdint>

#include "defines.h"
#include "dictionary/structure/pt_common/pt_node_array_reader.h"
#include "utils/byte_array_view.h"

namespace turbokeyboard {

class Ver2PtNodeArrayReader : public PtNodeArrayReader {
 public:
    Ver2PtNodeArrayReader(const ReadOnlyByteArrayView buffer) : mBuffer(buffer) {};

    virtual bool readPtNodeArrayInfoAndReturnIfValid(const int ptNodeArrayPos,
            int *const outPtNodeCount, int *const outFirstPtNodePos) const;
    virtual bool readForwardLinkAndReturnIfValid(const int forwordLinkPos,
            int *const outNextPtNodeArrayPos) const;

 private:
    DISALLOW_COPY_AND_ASSIGN(Ver2PtNodeArrayReader);

    const ReadOnlyByteArrayView mBuffer;
};
} // namespace turbokeyboard
#endif /* TURBOKEYBOARD_VER2_PT_NODE_ARRAY_READER_H */
