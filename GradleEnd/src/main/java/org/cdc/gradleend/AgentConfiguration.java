package org.cdc.gradleend;
import agentlib.AbstractConfiguration;

/**
 * e-mail: 3154934427@qq.com
 * 配置类
 *
 * @author cdc123
 * @classname AgentConfiguration
 * @date 2023/7/1 19:57
 */
public class AgentConfiguration implements AbstractConfiguration {
    @Override
    public boolean isEnable(String key) {
        return true;
    }
}
