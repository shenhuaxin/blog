package com.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
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
 *
 * @author shenhuaxin
 * @date 2020/9/10
 */
@SuppressWarnings("all")
@SupportedAnnotationTypes("com.processor.GetterAnno")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DataAnnoProcessor extends AbstractProcessor {

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
        Set<? extends Element> setterAnno = roundEnv.getElementsAnnotatedWith(Data.class);
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
                    jcClassDecl.defs = jcClassDecl.defs.append(generateEqualMethod(jcClassDecl));
                }
            });
        }
        return true;
    }

    /**
     * 生成Getter方法
     *
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

        JCTree.JCMethodDecl getter = treeMaker.MethodDef(
                publicModifier,
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
                fieldTypes = fieldTypes.append(field.vartype);
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

    /**
     * @Override public boolean equals(Object o) {
     * if (this == o) return true;
     * if (o == null || getClass() != o.getClass()) return false;
     * GetterProcessorTest that = (GetterProcessorTest) o;
     * return Objects.equals(name, that.name);
     * }
     */
    public JCTree.JCMethodDecl generateEqualMethod(JCTree.JCClassDecl classDecl) {
        // public boolean equals(Object o)
        JCTree.JCModifiers publicModifier = treeMaker.Modifiers(Flags.PUBLIC);
        JCTree.JCExpression returnType = treeMaker.TypeIdent(TypeTag.BOOLEAN);
        Name method = names.fromString("equals");
        JCTree.JCExpression ObjectExpr = treeMaker.Ident(names.fromString("java"));
        ObjectExpr = treeMaker.Select(ObjectExpr, names.fromString("lang"));
        ObjectExpr = treeMaker.Select(ObjectExpr, names.fromString("Object"));
        JCTree.JCVariableDecl param = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("o"), ObjectExpr, null);
        param.pos = classDecl.pos;
        List<JCTree.JCVariableDecl> params = List.of(param);

        List<JCTree.JCStatement> statement = List.nil();
        // if (this == o) return true;
        JCTree.JCBinary thisEqualOIf = treeMaker.Binary(JCTree.Tag.EQ, treeMaker.Ident(names.fromString("this")), treeMaker.Ident(names.fromString("o")));
        JCTree.JCReturn thisEqualOTrue = treeMaker.Return(treeMaker.Literal(true));
        JCTree.JCIf thisequaloif = treeMaker.If(thisEqualOIf, thisEqualOTrue, null);
//        System.out.println(thisequaloif);
        // if (o == null || getClass() != o.getClass()) return false;
        JCTree.JCBinary oequalnull = treeMaker.Binary(JCTree.Tag.EQ, treeMaker.Ident(names.fromString("o")), treeMaker.Literal(TypeTag.BOT, null));
        JCTree.JCExpression getClass = treeMaker.Ident(names.fromString("this"));
        getClass = treeMaker.Apply(List.nil(), treeMaker.Select(getClass, names.fromString("getClass")), List.nil());
        JCTree.JCExpression oGetClass = treeMaker.Ident(names.fromString("o"));
        oGetClass = treeMaker.Apply(List.nil(), treeMaker.Select(oGetClass, names.fromString("getClass")), List.nil());
        JCTree.JCBinary classequal = treeMaker.Binary(JCTree.Tag.NE, getClass, oGetClass);
        JCTree.JCBinary classequalStatement = treeMaker.Binary(JCTree.Tag.OR, oequalnull, classequal);
        JCTree.JCIf classIf = treeMaker.If(classequalStatement, treeMaker.Return(treeMaker.Literal(false)), null);
        // GetterProcessorTest that = (GetterProcessorTest) o;
        JCTree.JCExpression castexpr = treeMaker.TypeCast(treeMaker.Ident(classDecl.name), treeMaker.Ident(names.fromString("o")));
        JCTree.JCVariableDecl that = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), names.fromString("that"), treeMaker.Ident(classDecl.name), castexpr);

        //  return Objects.equals(name, that.name);
        List<JCTree.JCExpression> typelist = List.nil();
        List<JCTree.JCExpression> namelist = List.nil();
        typelist = typelist.append(treeMaker.Ident(classDecl.name));
        typelist = typelist.append(treeMaker.Ident(classDecl.name));

        JCTree.JCIdent name = treeMaker.Ident(names.fromString("name"));
        JCTree.JCIdent that1 = treeMaker.Ident(names.fromString("that"));
        JCTree.JCFieldAccess name1 = treeMaker.Select(that1, names.fromString("name"));

        namelist = namelist.append(name);
        namelist = namelist.append(name1);

        JCTree.JCExpression returnExpr = treeMaker.Ident(names.fromString("java"));
        returnExpr = treeMaker.Select(returnExpr, names.fromString("util"));
        returnExpr = treeMaker.Select(returnExpr, names.fromString("Objects"));
        returnExpr = treeMaker.Select(returnExpr, names.fromString("equals"));
        JCTree.JCStatement aReturn = treeMaker.Return(treeMaker.Apply(typelist, returnExpr,namelist));

        statement = statement.append(thisequaloif);
        statement = statement.append(classIf);
        statement = statement.append(that);

        statement = statement.append(aReturn);
        JCTree.JCBlock block = treeMaker.Block(0, statement);
        JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(publicModifier, method, returnType, List.nil(), params, List.nil(), block, null);
        System.out.println(methodDecl);
        return methodDecl;
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
