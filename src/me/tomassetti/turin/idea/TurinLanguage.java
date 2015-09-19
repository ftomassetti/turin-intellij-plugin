package me.tomassetti.turin.idea;

import com.intellij.lang.Language;

public class TurinLanguage extends Language {
    private static TurinLanguage INSTANCE;

    public static TurinLanguage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TurinLanguage();
        }
        return INSTANCE;
    }

    private TurinLanguage(){
        super("Turin");
    }
}
