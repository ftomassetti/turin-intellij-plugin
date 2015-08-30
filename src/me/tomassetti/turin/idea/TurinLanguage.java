package me.tomassetti.turin.idea;

import com.intellij.lang.Language;

public class TurinLanguage extends Language {
    public static final TurinLanguage INSTANCE = new TurinLanguage();

    private TurinLanguage(){
        super("Turin");
    }
}
