/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.phonemetra.turbo.keyboard.latin.personalization;

import android.content.Context;

import com.phonemetra.turbo.keyboard.annotations.ExternallyReferenced;
import com.phonemetra.turbo.keyboard.annotations.UsedForTesting;
import com.phonemetra.turbo.keyboard.latin.BinaryDictionary;
import com.phonemetra.turbo.keyboard.latin.Dictionary;
import com.phonemetra.turbo.keyboard.latin.ExpandableBinaryDictionary;
import com.phonemetra.turbo.keyboard.latin.NgramContext;
import com.phonemetra.turbo.keyboard.latin.define.ProductionFlags;
import com.phonemetra.turbo.keyboard.latin.makedict.DictionaryHeader;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Locally gathers statistics about the words user types and various other signals like
 * auto-correction cancellation or manual picks. This allows the keyboard to adapt to the
 * typist over time.
 */
public class UserHistoryDictionary extends ExpandableBinaryDictionary {
    static final String NAME = UserHistoryDictionary.class.getSimpleName();

    // TODO: Make this constructor private
    UserHistoryDictionary(final Context context, final Locale locale,
            @Nullable final String account) {
        super(context, getUserHistoryDictName(NAME, locale, null /* dictFile */, account), locale, Dictionary.TYPE_USER_HISTORY, null);
        if (mLocale != null && mLocale.toString().length() > 1) {
            reloadDictionaryIfRequired();
        }
    }

    /**
     * @returns the name of the {@link UserHistoryDictionary}.
     */
    @UsedForTesting
    static String getUserHistoryDictName(final String name, final Locale locale,
            @Nullable final File dictFile, @Nullable final String account) {
        if (!ProductionFlags.ENABLE_PER_ACCOUNT_USER_HISTORY_DICTIONARY) {
            return getDictName(name, locale, dictFile);
        }
        return getUserHistoryDictNamePerAccount(name, locale, dictFile, account);
    }

    /**
     * Uses the currently signed in account to determine the dictionary name.
     */
    private static String getUserHistoryDictNamePerAccount(final String name, final Locale locale,
            @Nullable final File dictFile, @Nullable final String account) {
        if (dictFile != null) {
            return dictFile.getName();
        }
        String dictName = name + "." + locale.toString();
        if (account != null) {
            dictName += "." + account;
        }
        return dictName;
    }

    // Note: This method is called by {@link DictionaryFacilitator} using Java reflection.
    @SuppressWarnings("unused")
    @ExternallyReferenced
    public static UserHistoryDictionary getDictionary(final Context context, final Locale locale,
            final File dictFile, final String dictNamePrefix, @Nullable final String account) {
        return PersonalizationHelper.getUserHistoryDictionary(context, locale, account);
    }

    /**
     * Add a word to the user history dictionary.
     *
     * @param userHistoryDictionary the user history dictionary
     * @param ngramContext the n-gram context
     * @param word the word the user inputted
     * @param isValid whether the word is valid or not
     * @param timestamp the timestamp when the word has been inputted
     */
    public static void addToDictionary(final ExpandableBinaryDictionary userHistoryDictionary,
            @Nonnull final NgramContext ngramContext, final String word, final boolean isValid,
            final int timestamp) {
        if (word.length() > BinaryDictionary.DICTIONARY_MAX_WORD_LENGTH) {
            return;
        }
        userHistoryDictionary.updateEntriesForWord(ngramContext, word,
                isValid, 1 /* count */, timestamp);
    }

    @Override
    public void close() {
        // Flush pending writes.
        asyncFlushBinaryDictionary();
        super.close();
    }

    @Override
    protected Map<String, String> getHeaderAttributeMap() {
        final Map<String, String> attributeMap = super.getHeaderAttributeMap();
        attributeMap.put(DictionaryHeader.USES_FORGETTING_CURVE_KEY,
                DictionaryHeader.ATTRIBUTE_VALUE_TRUE);
        attributeMap.put(DictionaryHeader.HAS_HISTORICAL_INFO_KEY,
                DictionaryHeader.ATTRIBUTE_VALUE_TRUE);
        return attributeMap;
    }

    @Override
    protected void loadInitialContentsLocked() {
        // No initial contents.
    }

    @Override
    public boolean isValidWord(final String word) {
        // Strings out of this dictionary should not be considered existing words.
        return false;
    }
}
