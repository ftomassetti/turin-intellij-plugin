package me.tomassetti.turin.idea.psi;

import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.TurinLanguage;

public class TurinTokenType extends IElementType {

    public TurinTokenType(String debugName) {
        super(debugName, TurinLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "TurinTokenType " + super.toString();
    }
}
