package me.tomassetti.turin.idea;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by federico on 30/08/15.
 */
public class TurinFileType extends LanguageFileType {

    public static final TurinFileType INSTANCE = new TurinFileType();

    private TurinFileType(){
        super(TurinLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Turin file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Turin language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "to";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return TurinIcons.FILE;
    }
}
