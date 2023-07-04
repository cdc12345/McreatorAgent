package org.cdc.mcreatoragent.model;

import javassist.*;
import org.cdc.agentlib.AbstractConfiguration;
import org.cdc.mcreatoragent.AgentClass;
import org.cdc.agentlib.transformer.annotation.FilteredClass;
import org.cdc.mcreatoragent.utils.TranslatablePool;
import org.cdc.agentlib.transformer.FilterClassTransformer;


import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * e-mail: 3154934427@qq.com
 * 中文汉化池
 *
 * @author cdc123
 * @classname CNPool
 * @date 2023/1/9 12:52
 */
public class CNPool extends FilterClassTransformer {

    public CNPool(AbstractConfiguration configuration) {
        super(configuration);
    }

    @FilteredClass(value = "net.mcreator.ui.modgui",mode = "head")
    public void injectCNToGui(CtClass ctclass) throws NotFoundException, CannotCompileException, IOException {
        if (!ctclass.getName().endsWith("GUI")) return;
        CtMethod initGui = ctclass.getDeclaredMethod("initGUI");
        var fields = ctclass.getDeclaredFields();
        var code = getCodeForCN(ctclass.getSimpleName(),fields);
        AgentClass.storeClass(ctclass.getName(),code);
        if (AgentClass.getInstance().isEnable("debug"))
            Logger.getGlobal().info(code);
        if (code != null&&!code.isEmpty())
            initGui.insertAfter(code);
    }
    @FilteredClass("net.mcreator.ui.minecraft.villagers.JVillagerTradeProfession")
    public void injectVillagerTrade(CtClass ctClass) throws CannotCompileException {
        CtConstructor constructor = ctClass.getConstructors()[0];
        constructor.insertAfter("villagerProfession.setRenderer(org.cdc.mcreatoragent.model.CNPool." +
                "getCNPoolRender(\"JVillagerTradeProfession.trade\",villagerProfession.getRenderer()));");
    }
    @FilteredClass("net.mcreator.ui.minecraft.villagers.JVillagerTradeEntry")
    public void injectVillagerEntry(CtClass ctClass) throws IOException, NotFoundException, CannotCompileException {
        CtConstructor constructor = ctClass.getConstructors()[0];
        var code = getCodeForCN(ctClass.getSimpleName(), ctClass.getDeclaredFields());
        AgentClass.storeClass(ctClass.getName(),code);
        constructor.insertAfter(code);
    }

    @FilteredClass("net.mcreator.ui.minecraft.DataListComboBox")
    public void injectDataListComboBox(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod init = ctClass.getDeclaredMethod("init");
        init.insertAfter("setRenderer(org.cdc.mcreatoragent.model.CNPool." +
                "getCNPoolRender(\"DataListComboBox\",getRenderer()));");
    }
    @FilteredClass("net.mcreator.ui.dialogs.StringSelectorDialog")
    public void injectStringDialog(CtClass ctClass) throws CannotCompileException {
        CtConstructor constructor = ctClass.getConstructors()[0];
        constructor.insertAfter("list.setCellRenderer(org.cdc.mcreatoragent.model.CNPool." +
                "getCNPoolRender(\"StringSelectorDialog\",list.getCellRenderer()));");
    }

    @FilteredClass("net.mcreator.ui.dialogs.DataListSelectorDialog")
    public void injectDataListDialog(CtClass ctClass) throws CannotCompileException {
        CtConstructor constructor = ctClass.getConstructors()[0];
        constructor.insertAfter("list.setCellRenderer(org.cdc.mcreatoragent.model.CNPool." +
                "getCNPoolRender(\"DataListCellRenderer\",list.getCellRenderer()));");
    }
    @FilteredClass("net.mcreator.workspace.elements.VariableType")
    public void injectVariableType(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod toString = ctClass.getDeclaredMethod("toString");
        toString.setBody("return org.cdc.mcreatoragent.model.CNPool.getCN(\"VariableType\",getName())+\"(\"+getName()+\")\";");
    }

    @FilteredClass("net.mcreator.minecraft.DataListEntry")
    public void injectDataListEntry(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod readableName = ctClass.getDeclaredMethod("setReadableName");
        readableName.insertAfter("$0.readableName = org.cdc.mcreatoragent.model.CNPool.getReadable(readableName);");
    }

    @FilteredClass("net.mcreator.ui.wysiwyg.WYSIWYGEditor")
    public void injectEditor(CtClass ctClass) throws NotFoundException, IOException, CannotCompileException {
        CtConstructor constructor = ctClass.getConstructors()[0];
        var code = getCodeForCN(ctClass.getSimpleName(), ctClass.getDeclaredFields());
        AgentClass.storeClass(ctClass.getName(),code);
        constructor.insertAfter(code);
    }
    @FilteredClass("net.mcreator.ui.component.JItemListField")
    public void injectJItemField(CtClass ctClass,ClassPool pool) throws NotFoundException, CannotCompileException {
        CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{pool.get("net.mcreator.ui.MCreator"),CtClass.booleanType});
        constructor.insertAfter("elementsList.setCellRenderer(org.cdc.mcreatoragent.model.CNPool.getCNPoolRender(\"JItemListField\"" +
                ",elementsList.getCellRenderer()));");
    }

    public static String getCN(String nameSpace,String key) {
        if (key == null) return null;
        if (AgentClass.getInstance().isEnable("debug")) {
            try {
                Files.writeString(Paths.get("tra", "tra.log"), System.lineSeparator() + "正在寻找: " + nameSpace + ":" + key.toLowerCase(Locale.ROOT) + "," +
                        TranslatablePool.getPool().containValue(nameSpace, key), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TranslatablePool.getPool().getValue(nameSpace,key);
    }

    public static String getReadable(String readableName){
        return getCN("",readableName);
    }

    public String getCodeForCN(String sClassName,CtField[] ctFields) throws NotFoundException {
        StringBuilder builder = new StringBuilder();
        for (CtField ctField:ctFields){
            if ("javax.swing.JComboBox".equals(ctField.getType().getName())||
                    "net.mcreator.ui.validation.component.VComboBox".equals(ctField.getType().getName())){
                String fieldName = ctField.getName();
                builder.append(fieldName).append(".setRenderer(org.cdc.mcreatoragent.model.CNPool.getCNPoolRender(")
                        .append("\"").append(sClassName).append(".").append(fieldName).append("\"").append(",").append(fieldName).append(".getRenderer() ) );");
            }
        }
        return builder.toString();
    }

    public static<E> ListCellRenderer<E> getCNPoolRender(String nameSpace,ListCellRenderer<E> renderer){
        return new CNPoolRender<>(nameSpace,renderer);
    }

    public record CNPoolRender<F>(String nameSpace,
                                  ListCellRenderer<F> renderer) implements ListCellRenderer<F> {

        @Override
        public Component getListCellRendererComponent(JList<? extends F> list, F value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (AgentClass.getInstance().isEnable("debug")) {
                label.setToolTipText(nameSpace + ":" + label.getText().toLowerCase(Locale.ROOT));
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        StringSelection se = new StringSelection(label.getToolTipText());
                        JOptionPane.showMessageDialog(null,"复制成功");
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(se,se);
                    }
                });
            } else {
                label.setToolTipText(label.getText());
            }
            label.setText(getCN(nameSpace, label.getText()));
            return label;
        }
    }
}
