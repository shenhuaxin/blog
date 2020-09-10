package com.processor;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
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
                            jcClassDecl.defs = jcClassDecl.defs.append(generateSetterMethod(var));
                        }
                    }
                    jcClassDecl.defs = jcClassDecl.defs.append(generateHashCodeMethod(jcClassDecl));
                }
            });
        }
        return true;
    }

    /**
     * 生成Getter方法
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

    public JCTree.JCMethodDecl generateSetterMethod(JCTree.JCVariableDecl variable) {
        JCTree.JCModifiers publicModifier = treeMaker.Modifiers(Flags.PUBLIC);     // 访问类型
        JCTree.JCExpression vartype = treeMaker.TypeIdent(TypeTag.VOID);           // 返回类型
        Name methodName = names.fromString("set" + variable.name);             // 方法名

        JCTree.JCVariableDecl inParam = treeMaker.VarDef(                          // 形参
                treeMaker.Modifiers(Flags.PARAMETER),
                names.fromString(variable.name.toString()),
                variable.vartype, null);
        inParam.pos = variable.pos;
        List<JCTree.JCVariableDecl> parameters = List.of(inParam);                 // 形参列表
        // this.name = name;
        JCTree.JCExpressionStatement expressionStatement = treeMaker
                .Exec(treeMaker.Assign(
                        treeMaker.Select(treeMaker.Ident(names.fromString("this")), variable.name),
                        treeMaker.Ident(variable.name)));
        List<JCTree.JCStatement> statementList = List.of(expressionStatement);
        JCTree.JCBlock block = treeMaker.Block(0, statementList);

        //
        List<JCTree.JCTypeParameter> genericParam = List.nil();
        List<JCTree.JCExpression> expressionList = List.nil();
        JCTree.JCExpression defaultValue = null;

        JCTree.JCMethodDecl getter = treeMaker.MethodDef(publicModifier,
                methodName,
                vartype,
                genericParam,
                parameters,
                expressionList,
                block,
                defaultValue);
        return getter;
    }


    public JCTree.JCMethodDecl generateHashCodeMethod(JCTree.JCClassDecl classDecl) {
        List<JCTree.JCVariableDecl> fields = List.nil();
        List<JCTree.JCExpression> fieldTypes = List.nil();
        List<JCTree.JCExpression> fieldNames = List.nil();
        for (JCTree def : classDecl.defs) {
            if (def instanceof JCTree.JCVariableDecl) {
                JCTree.JCVariableDecl field = ((JCTree.JCVariableDecl) def);
                fields.append(field);
                System.out.println(field.vartype.type);
                fieldTypes = fieldTypes.append(memberAccess(field.vartype.type.toString()));
                fieldNames = fieldNames.append(treeMaker.Ident(field.name));
            }
        }
        JCTree.JCModifiers publicModifier = treeMaker.Modifiers(Flags.PUBLIC);
        JCTree.JCExpression returnType = treeMaker.TypeIdent(TypeTag.INT);
        Name method = names.fromString("hashCode");

        // java.util.Objects.hash
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString("java"));
        expr = treeMaker.Select(expr, names.fromString("util"));
        expr = treeMaker.Select(expr, names.fromString("Objects"));
        expr = treeMaker.Select(expr, names.fromString("hash"));

        JCTree.JCStatement aReturn = treeMaker.Return(treeMaker.Apply(fieldTypes, expr, fieldNames));
        List<JCTree.JCStatement> statementList = List.of(aReturn);
        JCTree.JCBlock block = treeMaker.Block(0, statementList);
        JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(publicModifier, method, returnType, List.nil(), List.nil(), List.nil(), block, null);
        System.out.println(methodDecl.toString());
        return methodDecl;
    }

    public JCTree.JCMethodDecl generateEqualMethod(JCTree.JCVariableDecl variable) {
        JCTree.JCModifiers publicModifier = treeMaker.Modifiers(Flags.PUBLIC);
        JCTree.JCExpression returnType = treeMaker.TypeIdent(TypeTag.BOOLEAN);
        Name method = names.fromString("equal");
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER),
                names.fromString("object"),
                treeMaker.TypeIdent(TypeTag.valueOf("Object")),
                null);
        param.pos = variable.pos;
        List<JCTree.JCVariableDecl> params = List.of(param);




        return null;
    }


    //传入一个类的全路径名，获取对应类的JCIdent
    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
    }

}
