/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.phonemetra.turbo.keyboard.latin;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;


import com.phonemetra.turbo.keyboard.latin.ContactsManager.ContactsChangedListener;
import com.phonemetra.turbo.keyboard.latin.common.StringUtils;
import com.phonemetra.turbo.keyboard.latin.permissions.PermissionsUtil;
import com.phonemetra.turbo.keyboard.latin.personalization.AccountUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

public class ContactsBinaryDictionary extends ExpandableBinaryDictionary
        implements ContactsChangedListener {
    private static final String TAG = "ContactsBinaryDictionary";
    private static final String NAME = "contacts";

    private final boolean mUseFirstLastBigrams;
    private final ContactsManager mContactsManager;

    protected ContactsBinaryDictionary(final Context context, final Locale locale,
            final File dictFile, final String name) {
        super(context, getDictName(name, locale, dictFile), locale, Dictionary.TYPE_CONTACTS,
                dictFile);
        mUseFirstLastBigrams = ContactsDictionaryUtils.useFirstLastBigramsForLocale(locale);
        mContactsManager = new ContactsManager(context);
        mContactsManager.registerForUpdates(this /* listener */);
        reloadDictionaryIfRequired();
    }

    
    public static ContactsBinaryDictionary getDictionary(final Context context, final Locale locale,
            final File dictFile, final String dictNamePrefix, @Nullable final String account) {
        return new ContactsBinaryDictionary(context, locale, dictFile, dictNamePrefix + NAME);
    }

    @Override
    public synchronized void close() {
        mContactsManager.close();
        super.close();
    }

    /**
     * Typically called whenever the dictionary is created for the first time or
     * recreated when we think that there are updates to the dictionary.
     * This is called asynchronously.
     */
    @Override
    public void loadInitialContentsLocked() {
        loadDeviceAccountsEmailAddressesLocked();
        loadDictionaryForUriLocked(ContactsContract.Profile.CONTENT_URI);
        // TODO: Switch this URL to the newer ContactsContract too
        loadDictionaryForUriLocked(Contacts.CONTENT_URI);
    }

    /**
     * Loads device accounts to the dictionary.
     */
    private void loadDeviceAccountsEmailAddressesLocked() {
        final List<String> accountVocabulary =
                AccountUtils.getDeviceAccountsEmailAddresses(mContext);
        if (accountVocabulary == null || accountVocabulary.isEmpty()) {
            return;
        }
        for (String word : accountVocabulary) {
             
            runGCIfRequiredLocked(true /* mindsBlockByGC */);
            addUnigramLocked(word, ContactsDictionaryConstants.FREQUENCY_FOR_CONTACTS,
                    false /* isNotAWord */, false /* isPossiblyOffensive */,
                    BinaryDictionary.NOT_A_VALID_TIMESTAMP);
        }
    }

    /**
     * Loads data within content providers to the dictionary.
     */
    private void loadDictionaryForUriLocked(final Uri uri) {
        if (!PermissionsUtil.checkAllPermissionsGranted(
                mContext, Manifest.permission.READ_CONTACTS)) {
            Log.i(TAG, "No permission to read contacts. Not loading the Dictionary.");
        }

        final ArrayList<String> validNames = mContactsManager.getValidNames(uri);
        for (final String name : validNames) {
            addNameLocked(name);
        }
        if (uri.equals(Contacts.CONTENT_URI)) {
            // Since we were able to add content successfully, update the local
            // state of the manager.
            mContactsManager.updateLocalState(validNames);
        }
    }

    private void addNameLocked(final String name) {
        int len = StringUtils.codePointCount(name);
        NgramContext ngramContext = NgramContext.getEmptyPrevWordsContext(
                BinaryDictionary.MAX_PREV_WORD_COUNT_FOR_N_GRAM);
        for (int i = 0; i < len; i++) {
            if (Character.isLetter(name.codePointAt(i))) {
                int end = ContactsDictionaryUtils.getWordEndPosition(name, len, i);
                String word = name.substring(i, end);
                i = end - 1;
                // Don't add single letter words, possibly confuses
                // capitalization of i.
                final int wordLen = StringUtils.codePointCount(word);
                if (wordLen <= MAX_WORD_LENGTH && wordLen > 1) {
                    runGCIfRequiredLocked(true /* mindsBlockByGC */);
                    addUnigramLocked(word,
                            ContactsDictionaryConstants.FREQUENCY_FOR_CONTACTS, false /* isNotAWord */,
                            false /* isPossiblyOffensive */,
                            BinaryDictionary.NOT_A_VALID_TIMESTAMP);
                    if (ngramContext.isValid() && mUseFirstLastBigrams) {
                        runGCIfRequiredLocked(true /* mindsBlockByGC */);
                        addNgramEntryLocked(ngramContext,
                                word,
                                ContactsDictionaryConstants.FREQUENCY_FOR_CONTACTS_BIGRAM,
                                BinaryDictionary.NOT_A_VALID_TIMESTAMP);
                    }
                    ngramContext = ngramContext.getNextNgramContext(
                            new NgramContext.WordInfo(word));
                }
            }
        }
    }

    @Override
    public void onContactsChange() {
        setNeedsToRecreate();
    }
}
