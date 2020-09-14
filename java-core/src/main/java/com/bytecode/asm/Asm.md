Asm CoreApi visit 方法访问顺序
```
visit
[visitSource]
[visitOuterClass] 
(visitAnnotation | visitAttribute)*
(visitInnerClass | visitField | visitMethod)* 
visitEnd
```