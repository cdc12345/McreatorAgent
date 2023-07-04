package org.cdc.gradleend;

import agentlib.AbstractConfiguration;
import agentlib.transformer.FilterClassTransformer;
import agentlib.transformer.annotation.FilteredClass;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * e-mail: 3154934427@qq.com
 * gradle
 *
 * @author cdc123
 * @classname GradleEditor
 * @date 2023/7/1 19:46
 */
public class GradleClassTransformer extends FilterClassTransformer {
    private static final Logger logger = Logger.getLogger("GradleClass");
    public GradleClassTransformer(AbstractConfiguration configuration) {
        super(configuration);
    }
    @FilteredClass("net.minecraftforge.gradle.common.util.DownloadUtils")
    public void injectUrl(CtClass ctClass) throws NotFoundException, CannotCompileException {
        var downloadFile = ctClass.getDeclaredMethod("downloadFile");
        downloadFile.insertBefore("$1 = org.cdc.gradleend.GradleClassTransformer.replaceUrl($1);");
    }

    public static URL replaceUrl(URL url) throws MalformedURLException {
        logger.info(url.toString());
        String url1 = url.toString().replaceAll("https://maven.minecraftforge.net",AgentMain.forgeMirror)
                .replaceAll("https://resources.download.minecraft.net/",AgentMain.assetsMirror)
                .replaceAll("https://libraries.minecraft.net/","https://bmclapi2.bangbang93.com/maven");
        return new URL(url1);
    }
}
