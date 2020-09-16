cd ../../../../../../
pwd
mvn clean install

cd target
java -cp java-core-1.0.jar com.bytecode.asm.ChangeClassTest
java -cp java-core-1.0.jar com.bytecode.asm.BeforeAsmClass

#rm -rf target