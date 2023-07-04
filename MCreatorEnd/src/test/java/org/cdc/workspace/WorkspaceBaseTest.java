package org.cdc.workspace;

import org.cdc.mcreatoragent.AgentClass;
import org.cdc.mcreatoragent.model.GradleHelp;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * e-mail: 3154934427@qq.com
 * 测试工作区替换
 *
 * @author cdc123
 * @classname WorkspaceBaseTest
 * @date 2023/7/2 13:07
 */
public class WorkspaceBaseTest {
    @Test
    public void replaceWorkspaceBase() throws IOException {
        //属性的基本配置
        AgentClass.config.setProperty("GradleHelp.forgeMirror","000");
        AgentClass.config.setProperty("GradleHelp.distMirror","0000");
        //请改成别的
        File testWorkspaceFolder = new File(System.getProperty("user.home"),".mcreator\\MCreatorWorkspaces\\mcrcdemoforgemod");
        if (testWorkspaceFolder.exists()) {
            GradleHelp.setupWorkspaceBase(testWorkspaceFolder);
        } else {
            System.err.println("不存在工作区目录");
        }
    }
}
