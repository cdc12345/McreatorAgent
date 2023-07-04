package org.cdc.agentlib.transformer;

import javassist.*;
import org.cdc.agentlib.AbstractConfiguration;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashSet;

/**
 * e-mail: 3154934427@qq.com
 *
 *
 * @author cdc123
 * @classname AgentClassTransformer
 * @date 2022/12/22 18:26
 */
public class AgentClassTransformer implements ClassFileTransformer {

    private final AbstractConfiguration configuration;

    public AgentClassTransformer(AbstractConfiguration abstractConfiguration){
        this.configuration = abstractConfiguration;
    }

    public static ClassPool classPool = ClassPool.getDefault();

    private final HashSet<ModelClassTransformer> modelClassTransformers = new HashSet<>();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String className1 = toJavaClassName(className);
        byte[] bytes;
        for (ModelClassTransformer modelClassTransformer:modelClassTransformers){
            bytes = modelClassTransformer.transform(className1,loader,classBeingRedefined,protectionDomain,classfileBuffer);
            if (bytes.length > 0){
                return bytes;
            }
        }
        try {
            if (FilterClassTransformer.classes.contains(className1)) {
                    if (configuration.isEnable("debug")) {
                        System.out.println(className1+" is rewriting");
                    }
                    return classPool.get(className1).toBytecode();
            }
        } catch (IOException | CannotCompileException | NotFoundException ignore) {}
        return new byte[0];
    }

    public AbstractConfiguration getConfiguration(){
        return configuration;
    }

    String toJavaClassName(String pathName){
        return pathName.replace('/','.');
    }

    public void addTransformer(ModelClassTransformer classTransformer) {
        modelClassTransformers.add(classTransformer);
    }
}
