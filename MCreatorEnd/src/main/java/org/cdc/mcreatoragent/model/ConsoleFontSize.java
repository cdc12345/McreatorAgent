package org.cdc.mcreatoragent.model;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.cdc.mcreatoragent.AgentClass;
import org.cdc.transformer.ModelClassTransformer;

/**
 * e-mail: 3154934427@qq.com
 * 控制台字体大小修改
 *
 * @author cdc123
 * @classname ConsoleFontSize
 * @date 2022/12/22 17:41
 */
public class ConsoleFontSize implements ModelClassTransformer {

    public ConsoleFontSize() {
        System.out.println("控制台字体补丁已经启用");
    }

    @Override
    public byte[] transform(String className) {
        ClassPool classPool = ClassPool.getDefault();
        try {
            if ("net.mcreator.ui.component.ConsolePane".equals(className)) {
                CtClass ctClass = classPool.getCtClass(className);
                CtClass attributionSet = classPool.getCtClass("javax.swing.text.SimpleAttributeSet");
                CtMethod parseCss = ctClass.getDeclaredMethod("parseSimpleAttributeSetToCSS",new CtClass[]{attributionSet});
                parseCss.insertBefore(String.format("javax.swing.text.StyleConstants.setFontSize(set,%s);","org.cdc.mcreatoragent.model.ConsoleFontSize.getConsoleFontSize()"));
                return ctClass.toBytecode();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static int getConsoleFontSize(){
        return Integer.parseInt(AgentClass.config
                .getOrDefault("ConsoleFontSize.size","9").toString());
    }
}
