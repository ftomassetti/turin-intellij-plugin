package me.tomassetti.turin.idea.psi;

import com.intellij.psi.tree.IElementType;
import me.tomassetti.turin.idea.TurinLanguage;

public class TurinElementType extends IElementType {

    public TurinElementType(String debugName) {
        super(debugName, TurinLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "TurinNodeType " + super.toString();
    }
}
