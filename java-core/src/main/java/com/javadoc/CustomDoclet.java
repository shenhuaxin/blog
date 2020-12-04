package com.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

/**
 * @author shenhuaxin
 * @date 2020/12/4
 */
public class CustomDoclet extends Doclet {

    public static boolean start(RootDoc var0) {
        ClassDoc[] classes = var0.classes();
        for (int i = 0; i < classes.length; ++i) {
            System.out.println(classes[i]);
            System.out.println(classes[i].commentText());
            for(MethodDoc method:classes[i].methods()){
                System.out.printf("\t%s\n", method.commentText());
            }
        }
        return true;
    }
}
