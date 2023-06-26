package org.cdc.mcreatoragent.model;

import javassist.*;
import org.cdc.AbstractConfiguration;
import org.cdc.transformer.annotation.FilteredClass;
import org.cdc.transformer.FilterClassTransformer;

/**
 * e-mail: 3154934427@qq.com
 * 补丁相关
 *
 * @author cdc123
 * @classname AgentInfo
 * @date 2023/1/20 9:39
 */
public class AgentInfo extends FilterClassTransformer {
    public AgentInfo(AbstractConfiguration configuration) {
        super(configuration);
    }

    @FilteredClass("net.mcreator.ui.workspace.selector.WorkspaceSelector")
    public void injectTitle(CtClass ctClass) throws CannotCompileException {
        CtConstructor constructor = ctClass.getConstructors()[0];
        constructor.insertAfter("setTitle(getTitle()+\" - 已经装载MCR补丁\");");
    }
}
