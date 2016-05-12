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

#ifndef TURBOKEYBOARD_TIME_KEEPER_H
#define TURBOKEYBOARD_TIME_KEEPER_H

#include "defines.h"

namespace turbokeyboard {

class TimeKeeper {
 public:
    static void setCurrentTime();

    static void startTestModeWithForceCurrentTime(const int currentTime);

    static void stopTestMode();

    static int peekCurrentTime() { return sCurrentTime; };

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(TimeKeeper);

    static int sCurrentTime;
    static bool sSetForTesting;
};
} // namespace turbokeyboard
#endif /* TURBOKEYBOARD_TIME_KEEPER_H */
