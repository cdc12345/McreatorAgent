package org.cdc.transformer;

import java.security.ProtectionDomain;

/**
 * e-mail: 3154934427@qq.com
 * 模块接口
 *
 * @author cdc123
 * @classname ModelClassTransformer
 * @date 2022/12/22 17:17
 */
public interface ModelClassTransformer {

    default byte[] transform(String className, ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
        return transform(className);
    }
    default byte[] transform(String className){
        return new byte[0];
    }
}
