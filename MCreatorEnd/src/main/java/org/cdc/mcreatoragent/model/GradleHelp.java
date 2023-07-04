package org.cdc.mcreatoragent.model;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.cdc.agentlib.AbstractConfiguration;
import org.cdc.agentlib.transformer.FilterClassTransformer;
import org.cdc.agentlib.transformer.annotation.FilteredClass;
import org.cdc.agentlib.transformer.annotation.ModuleSection;
import org.cdc.mcreatoragent.AgentClass;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * e-mail: 3154934427@qq.com
 * 构建协助模块
 *
 * @author cdc123
 * @classname GradleHelp
 * @date 2023/7/1 20:21
 */
public class GradleHelp extends FilterClassTransformer {
    public GradleHelp(AbstractConfiguration configuration) {
        super(configuration);
    }
    @ModuleSection("attachGradle")
    @FilteredClass("net.mcreator.ui.gradle.GradleConsole")
    public void monitorGradleRun(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        ctClass.getDeclaredMethod("exec",new CtClass[]{classPool.get("java.lang.String"),classPool
                .get("net.mcreator.gradle.GradleTaskFinishedListener")})
                .insertAfter("org.cdc.mcreatoragent.model.GradleHelp.runGradleAttach();");
    }

    @ModuleSection("resetWorkspaceBase")
    @FilteredClass("net.mcreator.generator.setup.WorkspaceGeneratorSetup")
    public void setupWorkspaceBase(CtClass ctClass) throws NotFoundException, CannotCompileException {
        ctClass.getDeclaredMethod("setupWorkspaceBase").insertAfter("org.cdc.mcreatoragent.model.GradleHelp.setupWorkspaceBase($1.getWorkspaceFolder());");
    }

    public static void runGradleAttach(){
        System.out.println("start to attach");
        try {
            new ProcessBuilder(System.getProperty("java.home")+"\\bin\\java.exe","-jar","gradleEnd.jar",
                    AgentClass.config.getProperty("GradleHelp.forgeMirror","https://maven.minecraftforge.net"),AgentClass.config.getProperty("GradleHelp.assetsMirror","https://bmclapi2.bangbang93.com/assets"))
                    .directory(new File(System.getProperty("user.dir")))
                    .redirectOutput(new File("output.log")).redirectError(new File("err.log")).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,e.getMessage(),"title",JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void setupWorkspaceBase(File workspaceFolder) throws IOException {
        //重新书写build.gradle
        var build = new File(workspaceFolder,"build.gradle");
        if (build.exists()){
            System.out.println("replacing build.gradle");
            replaceFileContent(build,"https://maven.minecraftforge.net", AgentClass
                    .config.getProperty("GradleHelp.forgeMirror","https://maven.minecraftforge.net"));
        }
        //重新书写gradle-wrapper.properties
        var gradleProperties = new File(workspaceFolder,"gradle/wrapper/gradle-wrapper.properties");
        if (gradleProperties.exists()){
            System.out.println("replacing gradle-wrapper");
            replaceFileContent(gradleProperties,"services\\.gradle\\.org/distributions",
                    AgentClass.config.getProperty("GradleHelp.distMirror","services.gradle.org/distributions"));
        }
        //重新书写settings.gradle
        var settings = new File(workspaceFolder,"settings.gradle");
        if (settings.exists()){
            System.out.println("replacing settings.gradle");
            replaceFileContent(settings,"https://maven.minecraftforge.net", AgentClass
                    .config.getProperty("GradleHelp.forgeMirror","https://maven.minecraftforge.net"));
        }
    }

    private static void replaceFileContent(File file, String regx, String text) throws IOException {
        var str = new String(Files.readAllBytes(file.toPath()));
        var result = str.replaceAll(regx, text);
        System.out.println(result);
        Files.copy(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)),file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
