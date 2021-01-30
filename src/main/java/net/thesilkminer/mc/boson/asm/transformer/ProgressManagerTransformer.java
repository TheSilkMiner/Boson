/*
 * Copyright (C) 2021  TheSilkMiner
 *
 * This file is part of Boson.
 *
 * Boson is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Boson is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Boson.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Contact information:
 * E-mail: thesilkminer <at> outlook <dot> com
 */

package net.thesilkminer.mc.boson.asm.transformer;

import net.thesilkminer.mc.boson.asm.utility.Log;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public final class ProgressManagerTransformer extends AbstractTransformer {
    private static final class PushMethodVisitor extends MethodVisitor {
        private PushMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            LOGGER.i("Reached beginning of method: preparing to inject our hook");

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(4 * 10 + 8, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ILOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "net/minecraftforge/fml/common/ProgressManager",
                    HOOK_NAME,
                    "(Ljava/lang/String;I)I",
                    false);
            super.visitVarInsn(Opcodes.ISTORE, 1);

            LOGGER.i("Successfully injected hook: now calling static method to edit steps");
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class HookMethodVisitor extends MethodVisitor {
        // private static <fermion-inject:hookFmlProgressBarCreation>(Ljava/lang/String;I)I
        //   L0
        //    LINENUMBER 165 L0
        //    NEW java/lang/Throwable
        //    DUP
        //    INVOKESPECIAL java/lang/Throwable.<init> ()V
        //    INVOKEVIRTUAL java/lang/Throwable.fillInStackTrace ()Ljava/lang/Throwable;
        //    INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;
        //    ASTORE 2
        //   L1
        //    LINENUMBER 166 L1
        //    LDC "net.minecraftforge.fml.common.Loader"
        //    ALOAD 2
        //    ICONST_3
        //    AALOAD
        //    INVOKEVIRTUAL java/lang/StackTraceElement.getClassName ()Ljava/lang/String;
        //    INVOKEVIRTUAL java/lang/String.equals (Ljava/lang/Object;)Z
        //    IFEQ L2
        //    LDC "Loading"
        //    ALOAD 0
        //    INVOKEVIRTUAL java/lang/String.equals (Ljava/lang/Object;)Z
        //    IFEQ L2
        //   L3
        //    LINENUMBER 167 L3
        //    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
        //    LDC "Found the ProgressBar creation for Loader: replacing steps"
        //    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
        //   L4
        //    LINENUMBER 168 L4
        //    LDC "Registry creation, registry firing (happens twice), post post initialization"
        //    POP
        //    ILOAD 1
        //    ICONST_4
        //    IADD
        //   L5
        //    GOTO L6
        //   L2
        //    LINENUMBER 170 L2
        //   FRAME APPEND [[Ljava/lang/StackTraceElement;]
        //    ILOAD 1
        //   L7
        //    LINENUMBER 166 L7
        //   L6
        //   FRAME SAME1 I
        //    IRETURN
        //   L8
        //    LOCALVARIABLE string Ljava/lang/String; L0 L8 0
        //    LOCALVARIABLE int I L0 L8 1
        //    LOCALVARIABLE stackTraceElementArray [Ljava/lang/StackTraceElement; L1 L8 2
        //    MAXSTACK = 3
        //    MAXLOCALS = 3
        private HookMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(100 + 6 * 10 + 5, l0);
            super.visitTypeInsn(Opcodes.NEW, "java/lang/Throwable");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "fillInStackTrace", "()Ljava/lang/Throwable;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            super.visitVarInsn(Opcodes.ASTORE, 2);

            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(100 + 6 * 10 + 6, l1);
            super.visitLdcInsn("net.minecraftforge.fml.common.Loader");
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitInsn(Opcodes.ICONST_3);
            super.visitInsn(Opcodes.AALOAD);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l2);
            super.visitLdcInsn("Loading");
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l2);

            final Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(100 + 6 * 10 + 7, l3);
            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitLdcInsn("Found the ProgressBar creation for Loader: replacing steps");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            final Label l4 = new Label();
            super.visitLabel(l4);
            super.visitLineNumber(100 + 6 * 10 + 8, l4);
            super.visitLdcInsn("Registry creation, registry firing (happens twice), post post initialization");
            super.visitInsn(Opcodes.POP);
            super.visitVarInsn(Opcodes.ILOAD, 1);
            super.visitInsn(Opcodes.ICONST_4);
            super.visitInsn(Opcodes.IADD);

            final Label l5 = new Label();
            final Label l6 = new Label();
            super.visitLabel(l5);
            super.visitJumpInsn(Opcodes.GOTO, l6);

            super.visitLabel(l2);
            super.visitLineNumber(100 + 7 * 10, l2);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "[Ljava/lang/StackTraceElement;" }, 0, null);
            super.visitVarInsn(Opcodes.ILOAD, 1);

            final Label l7 = new Label();
            super.visitLineNumber(100 + 6 * 10 + 6, l7);

            super.visitLabel(l6);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { Opcodes.INTEGER });
            super.visitInsn(Opcodes.IRETURN);

            final Label l8 = new Label();
            super.visitLabel(l8);

            super.visitLocalVariable("string", "Ljava/lang/String;", null, l0, l8, 0);
            super.visitLocalVariable("int", "I", null, l0, l8, 1);
            super.visitLocalVariable("stackTraceElementArray", "[Ljava/lang/StackTraceElement;", null, l1, l8, 2);

            super.visitMaxs(3, 3);
            super.visitEnd();

            LOGGER.i("Static method successfully injected");
        }
    }

    private static final Log LOGGER = Log.of("Progress Manager");
    private static final String HOOK_NAME = "fermion$$injected$$hookFmlProgressBarCreation$$generated$$00_28_1122";

    public ProgressManagerTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("progress_manager")
                        .setDescription("This modifies the Progress Bar screen so that Boson can show a higher resolution and better refined Progress Bar while loading")
                        .build(),
                ClassDescriptor.of("net.minecraftforge.fml.common.ProgressManager")
        );
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            public MethodVisitor visitMethod(final int access, @Nonnull final String name, @Nonnull final String desc, @Nullable final String signature, @Nullable final String[] exceptions) {
                final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
                if ("push".equals(name) && "(Ljava/lang/String;IZ)Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;".equals(desc)) {
                    return new PushMethodVisitor(v, parent);
                }
                return parent;
            }

            @Override
            public void visitEnd() {
                final MethodVisitor hookFmlProgressBarCreationVisitor = new HookMethodVisitor(v,
                        super.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, HOOK_NAME, "(Ljava/lang/String;I)I", null, null)
                );
                hookFmlProgressBarCreationVisitor.visitCode();

                super.visitEnd();
            }
        };
    }
}
