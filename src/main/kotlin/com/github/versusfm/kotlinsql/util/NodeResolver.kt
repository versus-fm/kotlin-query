package com.github.versusfm.kotlinsql.util

import org.apache.bcel.classfile.*
import org.apache.bcel.classfile.Deprecated
import java.util.Stack

class NodeResolver : Visitor {
    val nodeStack: Stack<Node> = Stack()
    override fun visitAnnotation(obj: Annotations?) {
        nodeStack.push(obj)
    }

    override fun visitAnnotationDefault(obj: AnnotationDefault?) {
        nodeStack.push(obj)
    }

    override fun visitAnnotationEntry(obj: AnnotationEntry?) {
        nodeStack.push(obj)
    }

    override fun visitBootstrapMethods(obj: BootstrapMethods?) {
        nodeStack.push(obj)
    }

    override fun visitCode(obj: Code?) {
        nodeStack.push(obj)
    }

    override fun visitCodeException(obj: CodeException?) {
        nodeStack.push(obj)
    }

    override fun visitConstantClass(obj: ConstantClass?) {
        nodeStack.push(obj)
    }

    override fun visitConstantDouble(obj: ConstantDouble?) {
        nodeStack.push(obj)
    }

    override fun visitConstantFieldref(obj: ConstantFieldref?) {
        nodeStack.push(obj)
    }

    override fun visitConstantFloat(obj: ConstantFloat?) {
        nodeStack.push(obj)
    }

    override fun visitConstantInteger(obj: ConstantInteger?) {
        nodeStack.push(obj)
    }

    override fun visitConstantInterfaceMethodref(obj: ConstantInterfaceMethodref?) {
        nodeStack.push(obj)
    }

    override fun visitConstantInvokeDynamic(obj: ConstantInvokeDynamic?) {
        nodeStack.push(obj)
    }

    override fun visitConstantLong(obj: ConstantLong?) {
        nodeStack.push(obj)
    }

    override fun visitConstantMethodHandle(obj: ConstantMethodHandle?) {
        nodeStack.push(obj)
    }

    override fun visitConstantMethodref(obj: ConstantMethodref?) {
        nodeStack.push(obj)
    }

    override fun visitConstantMethodType(obj: ConstantMethodType?) {
        nodeStack.push(obj)
    }

    override fun visitConstantModule(constantModule: ConstantModule?) {
        nodeStack.push(constantModule)
    }

    override fun visitConstantNameAndType(obj: ConstantNameAndType?) {
        nodeStack.push(obj)
    }

    override fun visitConstantPackage(constantPackage: ConstantPackage?) {
        nodeStack.push(constantPackage)
    }

    override fun visitConstantPool(obj: ConstantPool?) {
        nodeStack.push(obj)
    }

    override fun visitConstantString(obj: ConstantString?) {
        nodeStack.push(obj)
    }

    override fun visitConstantUtf8(obj: ConstantUtf8?) {
        nodeStack.push(obj)
    }

    override fun visitConstantValue(obj: ConstantValue?) {
        nodeStack.push(obj)
    }

    override fun visitDeprecated(obj: Deprecated?) {
        nodeStack.push(obj)
    }

    override fun visitEnclosingMethod(obj: EnclosingMethod?) {
        nodeStack.push(obj)
    }

    override fun visitExceptionTable(obj: ExceptionTable?) {
        nodeStack.push(obj)
    }

    override fun visitField(obj: Field?) {
        nodeStack.push(obj)
    }

    override fun visitInnerClass(obj: InnerClass?) {
        nodeStack.push(obj)
    }

    override fun visitInnerClasses(obj: InnerClasses?) {
        nodeStack.push(obj)
    }

    override fun visitJavaClass(obj: JavaClass?) {
        nodeStack.push(obj)
    }

    override fun visitLineNumber(obj: LineNumber?) {
        nodeStack.push(obj)
    }

    override fun visitLineNumberTable(obj: LineNumberTable?) {
        nodeStack.push(obj)
    }

    override fun visitLocalVariable(obj: LocalVariable?) {
        nodeStack.push(obj)
    }

    override fun visitLocalVariableTable(obj: LocalVariableTable?) {
        nodeStack.push(obj)
    }

    override fun visitLocalVariableTypeTable(obj: LocalVariableTypeTable?) {
        nodeStack.push(obj)
    }

    override fun visitMethod(obj: Method?) {
        nodeStack.push(obj)
        obj?.code?.accept(this)
    }

    override fun visitMethodParameters(obj: MethodParameters?) {
        nodeStack.push(obj)
    }

    override fun visitParameterAnnotation(obj: ParameterAnnotations?) {
        nodeStack.push(obj)
    }

    override fun visitParameterAnnotationEntry(obj: ParameterAnnotationEntry?) {
        nodeStack.push(obj)
    }

    override fun visitSignature(obj: Signature?) {
        nodeStack.push(obj)
    }

    override fun visitSourceFile(obj: SourceFile?) {
        nodeStack.push(obj)
    }

    override fun visitStackMap(obj: StackMap?) {
        nodeStack.push(obj)
    }

    override fun visitStackMapEntry(obj: StackMapEntry?) {
        nodeStack.push(obj)
    }

    override fun visitSynthetic(obj: Synthetic?) {
        nodeStack.push(obj)
    }

    override fun visitUnknown(obj: Unknown?) {
        nodeStack.push(obj)
    }
}