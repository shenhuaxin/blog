package com.processor;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * 用于查看JCTree中的参数信息
 * @author shenhuaxin
 * @date 2020/9/8
 */
@SupportedAnnotationTypes({
        "com.processor.GetterAnno"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PrintTreeInfoProcessor extends AbstractProcessor {

    private Messager messager;

    private JavacTrees trees;

    private TreeMaker treeMaker;

    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        System.out.println("init --------------------------");
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("process ----------------------------");

        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(GetterAnno.class);
        set.forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {

                /**
                 * public JCTree.JCModifiers mods;                      访问标识符
                 * public Name name;                                    类名
                 * public List<JCTree.JCTypeParameter> typarams;        泛型参数列表
                 * public JCTree.JCExpression extending;                继承
                 * public List<JCTree.JCExpression> implementing;       实现
                 * public List<JCTree> defs;                            字段、方法
                 * public ClassSymbol sym;                              类签名
                 * @param jcClassDecl
                 */
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    System.out.println("class start -------------------");
                    System.out.println("mods: " + jcClassDecl.getModifiers().toString());
                    System.out.println("name: " + jcClassDecl.name.toString());
                    if (jcClassDecl.getTypeParameters() != null) {
                        System.out.println("typarams: ");
                        for (JCTree.JCTypeParameter typaram : jcClassDecl.typarams) {
                            System.out.println("typaram: " + typaram.toString());
                        }
                    }
                    if (jcClassDecl.extending != null)
                        System.out.println("extending: " + jcClassDecl.extending.toString());
                    if (jcClassDecl.implementing != null)
                        System.out.println("implementing: " + jcClassDecl.implementing.toString());
                    System.out.println("defs: " + jcClassDecl.defs.toString());
                    System.out.println("sym: " + jcClassDecl.sym.toString());
                    System.out.println("class end -------------------");
                    super.visitClassDef(jcClassDecl);
                }


                /**
                 * 访问方法
                 * public JCTree.JCModifiers mods;                 访问标识符
                 * public Name name;                               方法名
                 * public JCTree.JCExpression restype;             返回参数类型
                 * public List<JCTree.JCTypeParameter> typarams;   泛型参数列表
                 * public JCTree.JCVariableDecl recvparam;         未知？？  值为null
                 * public List<JCTree.JCVariableDecl> params;      方法参数列表
                 * public List<JCTree.JCExpression> thrown;        异常抛出列表
                 * public JCTree.JCBlock body;                     方法体（包含方法上的注解）
                 * public JCTree.JCExpression defaultValue;        默认值（java8 接口默认方法）
                 * public MethodSymbol sym;                        方法签名
                 * @param jcMethodDecl
                 */
                @Override
                public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                    System.out.println("method start -------------");
                    System.out.println(jcMethodDecl.getModifiers().toString());
                    System.out.println(jcMethodDecl.getName().toString());
                    System.out.println(jcMethodDecl.getReturnType().toString());
                    System.out.println(jcMethodDecl.getTypeParameters().toString());
                    System.out.println(jcMethodDecl.getParameters().toString());
                    System.out.println(jcMethodDecl.getThrows().toString());
                    System.out.println(jcMethodDecl.sym.toString());
                    System.out.println(jcMethodDecl.getBody().toString());

                    if (jcMethodDecl.getDefaultValue() != null) {
                        System.out.println(jcMethodDecl.getDefaultValue().toString());
                    }else {
                        System.out.println("jcMethodDecl.getDefaultValue() is null");
                    }
                    if (jcMethodDecl.getReceiverParameter() != null) {
                        System.out.println(jcMethodDecl.getReceiverParameter());
                    }else {
                        System.out.println("jcMethodDecl.getReceiverParameter() is null");
                    }
                    System.out.println("method end -------------");
                    super.visitMethodDef(jcMethodDecl);
                }

                /**
                 * 访问变量和静态变量
                 * public JCTree.JCModifiers mods;        访问标识符(注解也是访问标识符)
                 * public Name name;                      方法名
                 * public JCTree.JCExpression nameexpr;
                 * public JCTree.JCExpression vartype;    变量类型
                 * public JCTree.JCExpression init;
                 * public VarSymbol sym;
                 * @param jcVariableDecl
                 */
                @Override
                public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                    System.out.println("varDef start -----------------");
                    System.out.println("mods: " + jcVariableDecl.mods.toString());
                    System.out.println("mods: " + jcVariableDecl.mods.getAnnotations().toString());
                    System.out.println("name: " + jcVariableDecl.name.toString());
                    if (jcVariableDecl.nameexpr != null) {
                        System.out.println("nameexpr: " + jcVariableDecl.nameexpr.toString());
                    }
                    if (jcVariableDecl.nameexpr != null) {
                        System.out.println("vartype: " + jcVariableDecl.vartype.toString());
                    }
                    System.out.println("init: " + jcVariableDecl.init.toString());
                    System.out.println("sym: " + jcVariableDecl.sym.toString());
                    System.out.println("varDef end -----------------");
                    super.visitVarDef(jcVariableDecl);
                }
            });
        });

        return true;
    }

    private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {

        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName())));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                getNewMethodName(jcVariableDecl.getName()),
                jcVariableDecl.vartype,
                List.nil(), List.nil(), List.nil(), body, null);
    }

    private Name getNewMethodName(Name name) {
        String s = name.toString();
        return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
    }
}
