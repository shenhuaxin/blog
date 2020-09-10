package com.processor;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * 创建方法的Processor
 * @author shenhuaxin
 * @date 2020/9/10
 */
@SuppressWarnings("Duplicates")
@SupportedAnnotationTypes("com.processor.GetterAnno")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class BuildMethodProcessor extends AbstractProcessor {

    private Messager messager;

    private JavacTrees trees;

    private TreeMaker treeMaker;

    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {

        System.out.println("init start --------------------------");
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        System.out.println("init end ---------------------------");
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("processor -------------------");
        Set<? extends Element> setterAnno = roundEnv.getElementsAnnotatedWith(GetterAnno.class);
        for (Element element : setterAnno) {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    super.visitClassDef(jcClassDecl);
                    List<JCTree> defs = jcClassDecl.defs;
                    for (JCTree def : defs) {
                        if (def instanceof JCTree.JCVariableDecl) {
                            JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) def;
                            jcClassDecl.defs = jcClassDecl.defs.append(generateGetterMethod(var));
                        }
                    }
                }
            });
        }
        return true;
    }

    /**
     * 生成setter方法
     * @param variableDecl
     * @return
     */
    public JCTree.JCMethodDecl generateGetterMethod(JCTree.JCVariableDecl variableDecl) {
        JCTree.JCModifiers publicModifier = treeMaker.Modifiers(Flags.PUBLIC);
        JCTree.JCExpression returnType = variableDecl.vartype;
        Name name = names.fromString("get" + variableDecl.name.toString());
        JCTree.JCReturn returnState = treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), variableDecl.name));
        List<JCTree.JCStatement> statementList = List.nil();
        statementList = statementList.append(returnState);
        JCTree.JCBlock block = treeMaker.Block(0, statementList);

        List<JCTree.JCTypeParameter> genericParam = List.nil();
        List<JCTree.JCExpression> expressionList = List.nil();
        List<JCTree.JCVariableDecl> variableDeclList = List.nil();

        JCTree.JCExpression defalutValue = null;
        JCTree.JCMethodDecl getter = treeMaker.MethodDef(publicModifier, name, returnType, genericParam, variableDeclList, expressionList, block, defalutValue);

        System.out.println(getter.toString());
        return getter;
    }
}
