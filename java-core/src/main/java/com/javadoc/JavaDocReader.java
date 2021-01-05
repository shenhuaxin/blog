package com.javadoc;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * yessss
 * @author shenhuaxin
 * @date 2020/12/4
 */
public class JavaDocReader {

    public static void main(final String ... args) throws Exception{
//        com.sun.tools.javadoc.Main.execute(new String[] {"-doclet",
//                CustomDoclet.class.getName(), "-classpath", "C:\\workspace\\blog\\java-core\\target\\classes",
//                "C:\\workspace\\blog\\java-core\\src\\main\\java\\com\\javadoc\\JavaDocReader.java"});
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName();
        System.out.println(name);


        System.in.read();
    }
}