package org.cdc.mcreatoragent.model;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.cdc.agentlib.AbstractConfiguration;
import org.cdc.mcreatoragent.AgentClass;
import org.cdc.agentlib.transformer.annotation.FilteredClass;
import org.cdc.agentlib.transformer.annotation.ModuleSection;
import org.cdc.agentlib.transformer.FilterClassTransformer;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * e-mail: 3154934427@qq.com
 * 字体修复
 *
 * @author cdc123
 * @classname FontFix
 * @date 2023/1/8 17:36
 */
public class FontFix extends FilterClassTransformer {

    public FontFix(AbstractConfiguration configuration) {
        super(configuration);
    }

    @ModuleSection("chineseFont")
    @FilteredClass("net.mcreator.ui.laf.MCreatorTheme")
    public void injectChineseFontFix(CtClass ctClass) throws CannotCompileException {
        var constructor = ctClass.getConstructors()[0];
        var fontFile = new File("cn.ttf");
        if (fontFile.exists())
            constructor.insertAfter("console_font = org.cdc.mcreatoragent.model.FontFix.getCNFont();");
        else
            constructor.insertAfter("console_font=secondary_font;");
    }

    @ModuleSection("fontSizeEdit")
    @FilteredClass("net.mcreator.ui.component.util.ComponentUtils")
    public void injectFontSize(CtClass ctClass) throws CannotCompileException, NotFoundException {
        var method = ctClass.getDeclaredMethod("deriveFont");
        method.insertBefore("$2 = org.cdc.mcreatoragent.model.FontFix.getFontSize($2);");
    }

    public static float getFontSize(float defaultSize){

        if (AgentClass.config.containsKey("FontFix.fontSize"))
            return Integer.parseInt(AgentClass.config.getProperty("FontFix.fontSize"));
        else
            return defaultSize;
    }

    public static Font getCNFont() throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT,new File("cn.ttf"));
    }
}
