package org.cdc.mcreatoragent;

import org.cdc.agentlib.AbstractConfiguration;
import org.cdc.agentlib.transformer.AgentClassTransformer;
import org.cdc.mcreatoragent.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * e-mail: 3154934427@qq.com
 *
 * @author cdc123
 * @classname AgentClass
 * @date 2022/11/25 14:56
 */
public class AgentClass implements AbstractConfiguration {

    private static AgentClass instance;

    public static AgentClass getInstance() {
        if (instance == null) {
            instance = new AgentClass();
        }
        return instance;
    }

    private AgentClass() {}


    public static String version = "8";
    public static Properties config = new Properties();
    public static Instrumentation instrumentation;
    public static File configFile;

    public static File whereAmI;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1));

    public static void premain(String arg, Instrumentation instrumentation) throws IOException {
        var agentClass = getInstance();

        AgentClass.instrumentation = instrumentation;
        initConfigPath();
        if (agentClass.isEnable("debug")) {
            initClassesPath();
            initTraPath();
        }


        System.out.println("MCreator补丁已经正常启动 , 运行模式:MCR");
        System.out.println(config.toString());

        whereAmI = new File("agent-test.jar");

        AgentClassTransformer classTransformer = new AgentClassTransformer(agentClass);

        classTransformer.addTransformer(new AgentInfo(agentClass));
        if (agentClass.isEnable("OfficialBan"))
            classTransformer.addTransformer(new MCreatorOnlineBan(agentClass));
        if (agentClass.isEnable("ConsoleFontSize"))
            classTransformer.addTransformer(new ConsoleFontSize());
        if (agentClass.isEnable("Translator"))
            classTransformer.addTransformer(new Translator(agentClass));
        if (agentClass.isEnable("FontFix"))
            classTransformer.addTransformer(new FontFix(agentClass));
        if (agentClass.isEnable("CNPool"))
            classTransformer.addTransformer(new CNPool(agentClass));
        if (agentClass.isEnable("GradleHelp"))
            classTransformer.addTransformer(new GradleHelp(agentClass));
//        if (isEnable("Experiment")){}

        instrumentation.addTransformer(classTransformer, false);

        initConfigurationAutoLoadThread();
    }

    public boolean isEnable(String name) {
        if (System.getProperty(name) == null) {
            return Boolean.parseBoolean(config.getProperty(name, "false"));
        } else {
            return Boolean.parseBoolean(System.getProperty(name, "false"));
        }
    }

    private static long last = 0;

    public static void initConfigPath() throws IOException {
        var configDir = new File("configs");
        if (!configDir.mkdirs()) {
            System.out.println("创建配置文件目录失败");
        }

        configFile = new File(configDir, "enable.properties");
        String str = new String(Objects.requireNonNull(AgentClass.class.getResourceAsStream("/enable.properties"))
                .readAllBytes()).replace("{version}", version);
        if (!configFile.exists()) {
            Files.copy(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), configFile.toPath());
        }
        var reader = new FileReader(configFile);
        config.load(reader);
        reader.close();
        last = configFile.lastModified();

        if (!config.getOrDefault("Version", "1").equals(version)) {
            Files.copy(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void initClassesPath() {
        var classDir = new File("classes");
        classDir.mkdirs();
    }

    public static void storeClass(String className, String code) throws IOException {
        if (!AgentClass.getInstance().isEnable("debug")) return;
        var path = Paths.get("classes", className + ".txt");
        if (Files.exists(path)) Files.delete(path);
        Files.copy(new ByteArrayInputStream(code.replace(";", ";" + System.lineSeparator()).getBytes(StandardCharsets.UTF_8)), path);
    }


    private static void initConfigurationAutoLoadThread() {
        threadPoolExecutor.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (configFile.lastModified() != last) {
                    System.out.println("config loaded");
                    try {
                        config.load(new FileReader(configFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    last = configFile.lastModified();
                }
            }
        });
    }

    public static void initTraPath() {
        var tra = new File("tra");
        tra.mkdirs();
    }

    public static String insertCode(String code) {
        return code.endsWith(";") ? code : code + ";";
    }

}
