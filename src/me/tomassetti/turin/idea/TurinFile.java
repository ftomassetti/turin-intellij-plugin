package me.tomassetti.turin.idea;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Created by federico on 30/08/15.
 */
public class TurinFile extends PsiFileBase {

    protected TurinFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, TurinLanguage.getInstance());
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return TurinFileType.INSTANCE;
    }


}
