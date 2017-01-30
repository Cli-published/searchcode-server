/*
 * Copyright (c) 2016 Boyter Online Services
 *
 * Use of this software is governed by the Fair Source License included
 * in the LICENSE.TXT file, but will be eventually open under GNU General Public License Version 3
 * see the README.md for when this clause will take effect
 *
 * Version 1.3.7
 */

package com.searchcode.app.util;


public interface ISpellingCorrector {
    int getWordCount();
    void putWord(String word);
    String correct(String word);
    boolean containsWord(String word);
}
