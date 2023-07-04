package org.cdc.gradleend;

import agentlib.transformer.AgentClassTransformer;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

/**
 * e-mail: 3154934427@qq.com
 *
 * @author cdc123
 * @classname AgentMain
 * @date 2023/7/2 8:02
 */
public class AgentMain {
    public static String folder;
    public static String forgeMirror;
    public static String assetsMirror;

    public static void premain(String agentOptions, Instrumentation instrumentation){
        var mainClassTransformer = new AgentClassTransformer(new AgentConfiguration());

        instrumentation.addTransformer(mainClassTransformer);

        mainClassTransformer.addTransformer(new GradleClassTransformer(mainClassTransformer.getConfiguration()));
    }

    public static void agentmain(String args,Instrumentation instrumentation){
        String[] argsArray = args.split("; ");
        folder = argsArray[0];
        forgeMirror = argsArray[1];
        assetsMirror = argsArray[2];
        System.out.println("附着成功,参数为:"+folder+"\nforgeMirror:"+forgeMirror+"\nassetsMirror:"+assetsMirror);
        try {
            instrumentation.appendToSystemClassLoaderSearch(new JarFile(folder+"\\"+"javassist.jar"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        var mainClassTransformer = new AgentClassTransformer(new AgentConfiguration());

        instrumentation.addTransformer(mainClassTransformer);

        mainClassTransformer.addTransformer(new GradleClassTransformer(mainClassTransformer.getConfiguration()));
    }
}
