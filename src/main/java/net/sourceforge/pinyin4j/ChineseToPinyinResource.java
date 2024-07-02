/**
 * This file is part of pinyin4j (http://sourceforge.net/projects/pinyin4j/) and distributed under
 * GNU GENERAL PUBLIC LICENSE (GPL).
 * <p/>
 * pinyin4j is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * <p/>
 * pinyin4j is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with pinyin4j.
 */

package net.sourceforge.pinyin4j;


import java.io.IOException;
import jakarta.annotation.Nullable;
import net.sourceforge.pinyin4j.multipinyin.Trie;
import lombok.extern.slf4j.Slf4j;


/**
 * Manage all external resources required in PinyinHelper class.
 *
 * @author Li Min (xmlerlimin@gmail.com)
 */
@Slf4j
@SuppressWarnings("unused")
class ChineseToPinyinResource {
    /**
     * A hash table contains <Unicode, HanyuPinyin> pairs
     */
    private Trie unicodeToHanyuPinyinTable = null;

    /**
     * Private constructor as part of the singleton pattern.
     */
    private ChineseToPinyinResource() {
        initializeResource();
    }

    /**
     * Singleton factory method.
     *
     * @return the one and only MySingleton.
     */
    static ChineseToPinyinResource getInstance() {
        return ChineseToPinyinResourceHolder.theInstance;
    }

    /**
     * @return Returns the unicodeToHanyuPinyinTable.
     */
    Trie getUnicodeToHanyuPinyinTable() {
        return unicodeToHanyuPinyinTable;
    }

    /**
     * @param unicodeToHanyuPinyinTable The unicodeToHanyuPinyinTable to set.
     */
    private void setUnicodeToHanyuPinyinTable(Trie unicodeToHanyuPinyinTable) {
        this.unicodeToHanyuPinyinTable = unicodeToHanyuPinyinTable;
    }

    /**
     * Initialize a hash-table contains <Unicode, HanyuPinyin> pairs
     */
    private void initializeResource() {
        try {
            final String resourceName = "/META-INF/data/pinyin4j/unicode_to_hanyu_pinyin.txt";
            final String resourceMultiName = "/META-INF/data/pinyin4j/multi_pinyin.txt";
            setUnicodeToHanyuPinyinTable(new Trie());
            getUnicodeToHanyuPinyinTable().load(ResourceHelper.getResourceInputStream(resourceName));
            getUnicodeToHanyuPinyinTable().loadMultiPinyin(ResourceHelper.getResourceInputStream(resourceMultiName));
            getUnicodeToHanyuPinyinTable().loadMultiPinyinExtend();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    Trie getHanyuPinyinTrie(char ch) {
        String codepointHexStr = Integer.toHexString(ch).toUpperCase();
        // fetch from hashtable
        return getUnicodeToHanyuPinyinTable().get(codepointHexStr);
    }

    /**
     * Get the unformatted Hanyu Pinyin representations of the given Chinese character in array format.
     *
     * @param ch given Chinese character in Unicode
     *
     * @return The Hanyu Pinyin strings of the given Chinese character in array format; return null if there is no corresponding Pinyin string.
     */
    String[] getHanyuPinyinStringArray(char ch) {
        String pinyinRecord = getHanyuPinyinRecordFromChar(ch);
        return parsePinyinString(pinyinRecord);
    }

    String[] parsePinyinString(String pinyinRecord) {
        if (pinyinRecord != null) {
            int indexOfLeftBracket = pinyinRecord.indexOf(Field.LEFT_BRACKET);
            int indexOfRightBracket = pinyinRecord.lastIndexOf(Field.RIGHT_BRACKET);
            String stripedString = pinyinRecord.substring(indexOfLeftBracket + Field.LEFT_BRACKET.length(), indexOfRightBracket);
            return stripedString.split(Field.COMMA);
        }
        return null;    // no record found or mal-formatted record
    }

    /**
     * @param record given record string of Hanyu Pinyin
     *
     * @return return true if record is not null and record is not "none0" and
     * record is not mal-formatted, else return false
     */
    private boolean isValidRecord(String record) {
        final String noneStr = "(none0)";
        return record != null && !record.equals(noneStr) && record.startsWith(Field.LEFT_BRACKET) && record.endsWith(Field.RIGHT_BRACKET);
    }

    /**
     * @param ch given Chinese character in Unicode
     *
     * @return corresponding Hanyu Pinyin Record in Properties file; null if no
     * record found
     */
    @Nullable
    private String getHanyuPinyinRecordFromChar(char ch) {
        // convert Chinese character to code point (integer)
        // please refer to http://www.unicode.org/glossary/#code_point
        // Another reference: http://en.wikipedia.org/wiki/Unicode
        String codepointHexStr = Integer.toHexString(ch).toUpperCase();
        // fetch from hashtable
        Trie trie = getUnicodeToHanyuPinyinTable().get(codepointHexStr);
        String foundRecord = null;
        if (trie != null) foundRecord = trie.getPinyin();
        return isValidRecord(foundRecord) ? foundRecord : null;
    }

    /**
     * Singleton implementation helper.
     */
    private static class ChineseToPinyinResourceHolder {
        static final ChineseToPinyinResource theInstance = new ChineseToPinyinResource();
    }

    /**
     * A class encloses common string constants used in Properties files
     *
     * @author Li Min (xmlerlimin@gmail.com)
     */
    static class Field {
        static final String LEFT_BRACKET = "(";
        static final String RIGHT_BRACKET = ")";
        static final String COMMA = ",";
    }
}
