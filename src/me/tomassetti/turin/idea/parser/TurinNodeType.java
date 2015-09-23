package me.tomassetti.turin.idea.parser;

import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.TurinLanguage;

public class TurinNodeType extends IElementType {

    public TurinNodeType(String debugName) {
        super(debugName, TurinLanguage.getInstance());
    }

    @Override
    public String toString() {
        return "TurinNodeType " + super.toString();
    }
}
