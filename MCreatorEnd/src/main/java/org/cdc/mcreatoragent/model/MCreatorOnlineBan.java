package org.cdc.mcreatoragent.model;


import javassist.*;
import org.cdc.AbstractConfiguration;
import org.cdc.transformer.annotation.FilteredClass;
import org.cdc.transformer.FilterClassTransformer;

/**
 * e-mail: 3154934427@qq.com
 * 屏蔽与官网的相关行为来达到加速启动的效果
 *
 * @author cdc123
 * @classname MCreatorOnlineBan
 * @date 2022/12/22 17:19
 */
public class MCreatorOnlineBan extends FilterClassTransformer {
    public MCreatorOnlineBan(AbstractConfiguration abstractConfiguration){
        super(abstractConfiguration);
        System.out.println("官网屏蔽已经启用");
    }

    @FilteredClass(value = "net.mcreator.ui.workspace.selector.WorkspaceSelector")
    public void deleteAd(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod initWebsitePanel = ctClass.getMethod("initWebsitePanel", "()V");
        initWebsitePanel.setBody("return;");
    }
    @FilteredClass("net.mcreator.io.net.WebIO")
    public void banWebIO(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod readUrlToString = ctClass.getDeclaredMethod("readURLToString");
        readUrlToString.insertBefore("if ($1.contains(\"mcreator.net\")) {return \"\";}");
    }
}
