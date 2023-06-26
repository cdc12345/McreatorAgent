package org.cdc.mcreatoragent.model;

import javassist.*;
import org.cdc.AbstractConfiguration;
import org.cdc.mcreatoragent.AgentClass;
import org.cdc.transformer.annotation.FilteredClass;
import org.cdc.mcreatoragent.utils.TranslatorUtils;
import org.cdc.mcreatoragent.utils.ZHConverter;
import org.cdc.transformer.FilterClassTransformer;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * e-mail: 3154934427@qq.com
 * 本地化自动翻译
 *
 * @author cdc123
 * @classname Translator
 * @date 2023/1/8 15:12
 */
public class Translator extends FilterClassTransformer {
    public Translator(AbstractConfiguration configuration){
        super(configuration);
        System.out.println("自动翻译已启用");
    }

    @FilteredClass("net.mcreator.workspace.Workspace")
    public void injectTranslator(CtClass cla) throws NotFoundException, CannotCompileException {
        if (AgentClass.getInstance().isEnable("Translator.autoTranslate")) {
            CtMethod setLocalization = cla.getDeclaredMethod("setLocalization");
            setLocalization.insertAfter("org.cdc.mcreatoragent.model.Translator.resetLanguage(language_map,$1);");
        }
    }

    @FilteredClass("net.mcreator.ui.workspace.WorkspacePanelLocalizations")
    public void addTranslatorButton(CtClass ctClass,ClassPool pool) throws NotFoundException, CannotCompileException {
        CtConstructor localizationCon = ctClass.getDeclaredConstructor(pool.get(new String[]{"net.mcreator.ui.workspace.WorkspacePanel"}));
        localizationCon.insertAt(121,"org.cdc.mcreatoragent.model.Translator.addTranslatorButtonInject(bar,$1.getMCreator().getWorkspace().getLanguageMap());");
    }

    @FilteredClass("net.mcreator.ui.validation.component.VTextField")
    public void addTranslatorDialog(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtConstructor textFieldCon = ctClass.getDeclaredConstructor(new CtClass[]{CtClass.intType});
        textFieldCon.insertAfter("addMouseListener(org.cdc.mcreatoragent.model.Translator.addTranslatorDialogListener());");
    }

    public static MouseListener addTranslatorDialogListener(){
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField textField = (JTextField) e.getSource();
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON3 && textField.isEnabled() && textField.isEditable()) {
                    textField.setText(textField.getText() + TranslatorUtils.translateCNToEN(JOptionPane.showInputDialog(textField.getParent(), "请输入中文,当前翻译引擎:" + AgentClass.config.getProperty("Translator.engine"), "中文翻译扩展窗口", JOptionPane.INFORMATION_MESSAGE)));
                }
            }
        };
    }

    public static void addTranslatorButtonInject(net.mcreator.ui.component.TransparentToolBar bar, Map<String, ConcurrentHashMap<String, String>> language_map){
        JButton tButton = new JButton("一键翻译");
        tButton.setOpaque(false);
        tButton.setContentAreaFilled(false);

        tButton.addActionListener(a->{
            language_map.get("en_us").keySet().forEach(b->resetLanguage((ConcurrentHashMap<String, ConcurrentHashMap<String, String>>) language_map,b));
            JOptionPane.showMessageDialog(null,"翻译完成,您可能需要重新打开当前的本地化界面来刷新本地化条目");
        });

        bar.add(tButton);
    }

    public static void resetLanguage(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> language_map,String key){
        var en = language_map.get("en_us");
        //简体中文
        var zh = language_map.get("zh_cn");
        //翻译为英文
        if (AgentClass.getInstance().isEnable("Translator.toEN")) {
            en.put(key, TranslatorUtils.translateCNToEN(originText(en,zh,key)));
        }
        if (zh != null) {
            //翻译为中文
            if (AgentClass.getInstance().isEnable("Translator.toCN")) {
                zh.put(key, TranslatorUtils.translateENToCN(originText(zh,en,key)));
            }
        }
        //是否转化为繁体
        if (AgentClass.getInstance().isEnable("Translator.toHant")) {
            ToHant(language_map, key, zh);
        }
    }

    private static String originText(ConcurrentHashMap<String,String> current,ConcurrentHashMap<String,String> other,String key){
        if (current == null && other == null){
            return "";
        } else if (current == null){
            return other.get(key);
        } else if (other == null){
            return current.get(key);
        }
        if (AgentClass.config.getProperty("Translator.base").equals("other")) {
            return other.get(key);
        }
        return current.get(key);
    }

    private static void ToHant(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> language_map, String key, ConcurrentHashMap<String, String> zh) {
        //繁体
        var tw = language_map.get("zh_tw");
        var hk = language_map.get("zh_hk");
        var mo = language_map.get("zh_mo");
        if (tw != null)
            tw.put(key, ZHConverter.convert(originText(tw,zh,key),ZHConverter.TRADITIONAL));
        if (hk != null)
            hk.put(key, ZHConverter.convert(originText(hk,zh,key), ZHConverter.TRADITIONAL));
        if (mo != null)
            mo.put(key, ZHConverter.convert(originText(mo,zh,key), ZHConverter.TRADITIONAL));
    }
}
