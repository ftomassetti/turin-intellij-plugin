package me.tomassetti.turin.idea.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import me.tomassetti.turin.idea.TurinIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * Created by federico on 30/08/15.
 */
public class TurinColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", TurinSyntaxHighligher.COMMENT),
            new AttributesDescriptor("Keyword", TurinSyntaxHighligher.KEYWORD),
            new AttributesDescriptor("Identifier", TurinSyntaxHighligher.IDENTIFIER),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return TurinIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new TurinSyntaxHighligher();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "// Example\n" +
                "namespace manga\n"+
                "\n"+
                "type MangaCharacter {\n"+
                "}\n";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Turin";
    }
}
