package com.eonmux.dcevm.utils;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.platform.templates.github.DownloadUtil;
import com.intellij.util.io.HttpRequests;
import org.jetbrains.annotations.NotNull;
import com.esotericsoftware.minlog.Log;

import java.io.File;
import java.io.IOException;

public class Network {
    public static void download(@NotNull final Project project,
                         @NotNull final File destination,
                         @NotNull final String url,
                         @NotNull final ProgressIndicator indicator,
                         @NotNull final Runnable callback) {
        try {
            DownloadUtil.downloadContentToFile(indicator, url, destination);
            callback.run();
        }
        catch (IOException e) {
            Log.debug("IOException", e);
        }
    }


    /**
     * Tries to derive size of the content referenced by the given url.
     * <p/>
     * Performs I/O, that's why is expected to not be called from EDT.
     *
     * @param url url which points to the target content
     * @return positive value which identifies size of the content referenced by the given url;
     * non-positive value as an indication that target content size is unknown
     */
    public static int getSize(@NotNull String url) {
        try {
            return HttpRequests.request(url).connect(request -> request.getConnection().getContentLength());
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
