package com.processor;


import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("com.processor.Log")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LogAnnoProcessor extends AbstractProcessor {


    private TreeMaker treeMaker;

    private Names names;

    private JavacTrees javacTrees;



    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Log.class);
        for (Element element : elements) {
            JCTree tree = javacTrees.getTree(element);
            tree.accept(new TreeTranslator() {
                @Override
                public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                    super.visitMethodDef(jcMethodDecl);
                    jcMethodDecl = addPrintStatement(jcMethodDecl);
                }
            });
        }
        return false;
    }


    public JCTree.JCMethodDecl addPrintStatement(JCTree.JCMethodDecl jcMethodDecl) {
        for (JCTree.JCAnnotation annotation : jcMethodDecl.mods.annotations) {
            System.out.println("anno start ------");
            if (names.fromString("com.processor.Log").equals(annotation.type.tsym.getQualifiedName())) {
                for (Pair<Symbol.MethodSymbol, Attribute> value : annotation.attribute.values) {
                    if (names.fromString("value").equals(value.fst.name)) {
                        JCTree.JCFieldAccess select = treeMaker.Select(treeMaker.Ident(names.fromString("java")), names.fromString("lang"));
                        select = treeMaker.Select(select, names.fromString("System"));
                        select = treeMaker.Select(select, names.fromString("out"));

                        //.System.out.println
                        JCTree.JCExpression printExpr = treeMaker.Select(select, names.fromString("println"));
                        Object logStr = value.snd.getValue();

                        List<JCTree.JCExpression> typeList = List.nil();
                        List<JCTree.JCExpression> fieldList = List.nil();
                        JCTree.JCLiteral literal = treeMaker.Literal(logStr);
                        fieldList = fieldList.append(literal);

                        JCTree.JCExpressionStatement printStatement = treeMaker.Exec(treeMaker.Apply(typeList, printExpr, fieldList));

                        List<JCTree.JCStatement> statementList = List.nil();
                        statementList = statementList.append(printStatement);
                        statementList = statementList.appendList(jcMethodDecl.getBody().getStatements());

                        jcMethodDecl.body.stats = statementList;
                    }
                }
            }
            System.out.println(jcMethodDecl.getBody());
            System.out.println("anno end ------");
        }
        return jcMethodDecl;
    }
}
