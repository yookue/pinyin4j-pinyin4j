/**
 * This file is part of pinyin4j (http://sourceforge.net/projects/pinyin4j/) and distributed under
 * GNU GENERAL PUBLIC LICENSE (GPL).
 * <p>
 * pinyin4j is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * <p>
 * pinyin4j is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with pinyin4j.
 */

package net.sourceforge.pinyin4j;


import java.io.IOException;
import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.ParseException;
import com.hp.hpl.sparta.Parser;
import lombok.extern.slf4j.Slf4j;


/**
 * A class contains resource that translates from Hanyu Pinyin to Gwoyeu Romatzyh
 *
 * @author Li Min (xmlerlimin@gmail.com)
 */
@Slf4j
@SuppressWarnings("unused")
class GwoyeuRomatzyhResource {
    /**
     * A DOM model contains Hanyu Pinyin to Gwoyeu Romatzyh mapping
     */
    private Document pinyinToGwoyeuMappingDoc;

    /**
     * Private constructor as part of the singleton pattern.
     */
    private GwoyeuRomatzyhResource() {
        initializeResource();
    }

    /**
     * Singleton factory method
     *
     * @return the one and only MySingleton.
     */
    static GwoyeuRomatzyhResource getInstance() {
        return GwoyeuRomatzyhSystemResourceHolder.theInstance;
    }

    /**
     * @return the pinyinToGwoyeuMappingDoc.
     */
    Document getPinyinToGwoyeuMappingDoc() {
        return pinyinToGwoyeuMappingDoc;
    }

    /**
     * @param pinyinToGwoyeuMappingDoc The pinyinToGwoyeuMappingDoc to set
     */
    private void setPinyinToGwoyeuMappingDoc(Document pinyinToGwoyeuMappingDoc) {
        this.pinyinToGwoyeuMappingDoc = pinyinToGwoyeuMappingDoc;
    }

    /**
     * Initialize a DOM contains Hanyu Pinyin to Gwoyeu mapping
     */
    private void initializeResource() {
        try {
            final String mappingFileName = "/META-INF/data/pinyin4j/pinyin_gwoyeu_mapping.xml";
            final String systemId = "";
            // Parse file to DOM Document
            setPinyinToGwoyeuMappingDoc(Parser.parse(systemId, ResourceHelper.getResourceInputStream(mappingFileName)));
        } catch (IOException | ParseException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * Singleton implementation helper.
     */
    private static class GwoyeuRomatzyhSystemResourceHolder {
        static final GwoyeuRomatzyhResource theInstance = new GwoyeuRomatzyhResource();
    }
}
