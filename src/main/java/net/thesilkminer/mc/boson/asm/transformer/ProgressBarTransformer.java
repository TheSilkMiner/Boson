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

public final class ProgressBarTransformer extends AbstractTransformer {
    private static final class StepMethodVisitor extends MethodVisitor {
        private StepMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/ProgressManager$ProgressBar",
                    HOOK_NAME, "(Ljava/lang/String;)Ljava/lang/String;", false);
            super.visitVarInsn(Opcodes.ASTORE, 1);

            LOGGER.i("Successfully added checking code");
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class HookMethodVisitor extends MethodVisitor {
        //  private static <fermion-inject:checkForRegistryCreationMessage>(Ljava/lang/String;)Ljava/lang/String;
        //   L0
        //    LINENUMBER 176 L0
        //    LDC "Initializing mods Phase 1"
        //    ALOAD 0
        //    INVOKEVIRTUAL java/lang/String.equals (Ljava/lang/Object;)Z
        //    IFEQ L1
        //    LDC "Creating Registries"
        //    GOTO L2
        //   L1
        //   FRAME SAME
        //    LDC "$Boson$marker$UsePreInit"
        //    ALOAD 0
        //    INVOKEVIRTUAL java/lang/String.equals (Ljava/lang/Object;)Z
        //    IFEQ L3
        //    LDC "Initializing mods Phase 1"
        //    GOTO L2
        //   L3
        //   FRAME SAME
        //    ALOAD 0
        //   L2
        //   FRAME SAME1 java/lang/String
        //    ARETURN
        //   L4
        //    LOCALVARIABLE string Ljava/lang/String; L0 L4 0
        //    MAXSTACK = 2
        //    MAXLOCALS = 1
        private HookMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(100 + 7 * 10 + 6, l0);
            super.visitLdcInsn("Initializing mods Phase 1");
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l1);
            super.visitLdcInsn("Creating Registries");
            super.visitJumpInsn(Opcodes.GOTO, l2);

            final Label l3 = new Label();
            super.visitLabel(l1);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitLdcInsn("$Boson$marker$UsePreInit");
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l3);
            super.visitLdcInsn("Initializing mods Phase 1");
            super.visitJumpInsn(Opcodes.GOTO, l2);

            super.visitLabel(l3);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 0);

            super.visitLabel(l2);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/String" });
            super.visitInsn(Opcodes.ARETURN);

            final Label l4 = new Label();
            super.visitLabel(l4);

            super.visitLocalVariable("string", "Ljava/lang/String;", null, l0, l4, 0);

            super.visitMaxs(2, 1);
            super.visitEnd();
        }
    }

    private static final Log LOGGER = Log.of("ProgressManager$ProgressBar");
    private static final String HOOK_NAME = "fermion$$injected$$checkForRegistryCreationMessage$$generated$$00_93_1122";

    public ProgressBarTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("progress_bar")
                        .setDescription("Companion to the ProgressManager plugin that fixes wrong messages being displayed on the ProgressBar")
                        .build(),
                ClassDescriptor.of("net.minecraftforge.fml.common.ProgressManager$ProgressBar")
        );
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            public MethodVisitor visitMethod(final int access, @Nonnull final String name, @Nonnull final String desc, @Nullable final String signature, @Nullable final String[] exceptions) {
                final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
                if ("step".equals(name) && "(Ljava/lang/String;)V".equals(desc)) {
                    return new StepMethodVisitor(v, parent);
                }
                return parent;
            }

            @Override
            public void visitEnd() {
                final MethodVisitor checkForRegistryCreationMessageVisitor = new HookMethodVisitor(v,
                        super.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, HOOK_NAME, "(Ljava/lang/String;)Ljava/lang/String;", null, null)
                );
                checkForRegistryCreationMessageVisitor.visitCode();

                super.visitEnd();
            }
        };
    }
}
