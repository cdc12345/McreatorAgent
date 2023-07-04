package org.cdc.agentlib.transformer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.cdc.agentlib.AbstractConfiguration;
import org.cdc.agentlib.transformer.annotation.FilteredClass;
import org.cdc.agentlib.transformer.annotation.Module;
import org.cdc.agentlib.transformer.annotation.ModuleSection;
import org.cdc.agentlib.transformer.exception.IgnoreClassException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;

/**
 * e-mail: 3154934427@qq.com
 * 过滤器型类传输
 *
 * @author cdc123
 * @classname FilterClassTransformer
 * @date 2022/12/22 21:37
 */
public class FilterClassTransformer implements ModelClassTransformer {
    public static HashSet<String> classes = new HashSet<>();
    protected String moduleName = "";
    private final AbstractConfiguration configuration;

    public FilterClassTransformer(AbstractConfiguration configuration){
        this.configuration = configuration;
    }

    protected AbstractConfiguration getConfiguration(){
        return configuration;
    }

    @Override
    public byte[] transform(String className, ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (moduleName.isEmpty()) loadCurrentModel();
        ClassPool classPool = AgentClassTransformer.classPool;
        CtClass ctClass = null;
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method:methods){
            if (!method.isAnnotationPresent(FilteredClass.class)) continue;
            if (method.isAnnotationPresent(ModuleSection.class)
                    && !configuration.isEnable(moduleName+"."+method.getAnnotation(ModuleSection.class).value())) {
                if (configuration.isEnable("debug"))System.out.println(moduleName+"."+method.getAnnotation(ModuleSection.class).value() + "=" +
                        configuration.isEnable(moduleName+"."+method.getAnnotation(ModuleSection.class).value()));
                continue;
            }
            FilteredClass filteredClass = method.getAnnotation(FilteredClass.class);
            String className1 = filteredClass.value();
            if (match(className,className1,filteredClass)){
                if (configuration.isEnable("debug")) System.out.println(method.getName());
                try {
                    if (ctClass == null) ctClass = classPool.getCtClass(className);
                    Object[] args = new Object[]{ctClass,classPool,loader,classBeingRedefined,classfileBuffer,protectionDomain};
                    method.invoke(this, Arrays.copyOf(args,method.getParameterCount()));
                    classes.add(className);
                } catch (IllegalAccessException | NotFoundException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e){
                    try {
                        throw e.getTargetException();
                    } catch (IgnoreClassException exception) {
                        ctClass = null;
                    } catch (Throwable exception){
                        exception.printStackTrace();
                    }
                }
            }
        }
        return new byte[0];
    }

    public boolean match(String className,String className1,FilteredClass filteredClass){
        switch (filteredClass.mode()){
            case "head" -> {
                return className.startsWith(className1);
            }
            case "full" -> {
                return className.equals(className1);
            }
            case "end" -> {
                return className.endsWith(className1);
            }
        }
        return false;
    }

    private void loadCurrentModel(){
        if (getClass().isAnnotationPresent(Module.class)){
            moduleName = getClass().getAnnotation(Module.class).value();
        } else {
            moduleName = getClass().getSimpleName();
        }
    }
}
