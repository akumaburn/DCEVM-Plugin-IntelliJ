package com.eonmux.dcevm;

import com.intellij.execution.ui.JreProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

/**
 * @author Phoenix
 * @author Amir Eslampanah
 * @date date(" EEEEE yyyy - MM - dd HH : mm : ssZ ")
 */
public class JdkProvider implements JreProvider {

    public JdkProvider() {
    }

    @NotNull
    @Override
    public String getJrePath() {
        File dir = new File(Attributes.JDK_DIR);
        return (dir.isDirectory() && Objects.requireNonNull(dir.list()).length > 0) ? dir.getAbsolutePath() : "";
    }
}