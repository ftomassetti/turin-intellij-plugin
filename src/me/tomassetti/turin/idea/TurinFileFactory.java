package me.tomassetti.turin.idea;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * Created by federico on 30/08/15.
 */
public class TurinFileFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(FileTypeConsumer consumer) {
        consumer.consume(TurinFileType.INSTANCE, "to");
    }

}
