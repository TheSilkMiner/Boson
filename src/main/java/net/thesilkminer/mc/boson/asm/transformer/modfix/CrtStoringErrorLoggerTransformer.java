/*
 * Copyright (C) 2020  TheSilkMiner
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

package net.thesilkminer.mc.boson.asm.transformer.modfix;

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

public final class CrtStoringErrorLoggerTransformer extends AbstractTransformer {

    @SuppressWarnings("SpellCheckingInspection")
    private static final class NullCheckerAdderMethodVisitor extends MethodVisitor {

        //  // access flags 0x1
        //  public <default:method-name>(Lstanhebben/zenscript/util/ZenPosition;Ljava/lang/String;)V
        //   L0
        //    LINENUMBER 22 L0
        //    ALOAD 0
        //    ALOAD 1
        //    ALOAD 2
        //    INVOKESPECIAL crafttweaker/zenscript/CrTErrorLogger.error (Lstanhebben/zenscript/util/ZenPosition;Ljava/lang/String;)V
        //   L1
        //    LINENUMBER 23 L1
        //    ALOAD 0
        //    GETFIELD crafttweaker/zenscript/CrtStoringErrorLogger.errors : Ljava/util/List;
        //    NEW crafttweaker/socket/SingleError
        //    DUP
        // <<< OVERWRITE BEGIN
        //    ALOAD 1
        //    INVOKEVIRTUAL stanhebben/zenscript/util/ZenPosition.getFileName ()Ljava/lang/String;
        //    ALOAD 1
        //    INVOKEVIRTUAL stanhebben/zenscript/util/ZenPosition.getLine ()I
        //    ALOAD 1
        //    INVOKEVIRTUAL stanhebben/zenscript/util/ZenPosition.getLineOffset ()I
        // === OVERWRITE WITH
        //    ALOAD 0
        //    ALOAD 1
        //    LDC "system"
        //    INVOKESPECIAL crafttweaker/zenscript/CrtStoringErrorLogger.<fermion-inject:getFileNameOrDefault> (Lstanhebben/zenscript/util/ZenPosition;Ljava/lang/String;)Ljava/lang/String;
        //    ALOAD 0
        //    ALOAD 1
        //    ICONST_M1
        //    INVOKESPECIAL crafttweaker/zenscript/CrtStoringErrorLogger.<fermion-inject:getLineOrDefault> (Lstanhebben/zenscript/util/ZenPosition;I)I
        //    ALOAD 0
        //    ALOAD 1
        //    ICONST_M1
        //    INVOKESPECIAL crafttweaker/zenscript/CrtStoringErrorLogger.<fermion-inject:getLineOffsetOrDefault> (Lstanhebben/zenscript/util/ZenPosition;I)I
        // >>> OVERWRITE END
        //    ALOAD 2
        //    GETSTATIC crafttweaker/socket/SingleError$Level.<default:impl-dependent> : Lcrafttweaker/socket/SingleError$Level;
        //    INVOKESPECIAL crafttweaker/socket/SingleError.<init> (Ljava/lang/String;IILjava/lang/String;Lcrafttweaker/socket/SingleError$Level;)V
        //    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
        //    POP
        //   L2
        //    LINENUMBER 24 L2
        //    RETURN
        //   L3
        //    LOCALVARIABLE this Lcrafttweaker/zenscript/CrtStoringErrorLogger; L0 L3 0
        //    LOCALVARIABLE position Lstanhebben/zenscript/util/ZenPosition; L0 L3 1
        //    LOCALVARIABLE message Ljava/lang/String; L0 L3 2
        //    MAXSTACK = 8
        //    MAXLOCALS = 3

        private boolean newSocket;
        private boolean stop;

        NullCheckerAdderMethodVisitor(final int api, @Nonnull final MethodVisitor mv) {
            super(api, mv);
            this.newSocket = false;
        }

        @Override
        public void visitVarInsn(final int opcode, final int var) {
            if (this.stop) {
                if (opcode == Opcodes.ALOAD && var == 2) {
                    L.d("Found 'ALOAD 2': stopping opcode eating");
                    this.stop = false;
                    super.visitVarInsn(opcode, var);
                    return;
                }
                if (opcode == Opcodes.ALOAD) {
                    L.d("Eaten opcode 'ALOAD " + var + "'");
                    return;
                }
                L.e("Eaten opcode '" + opcode + "' that shouldn't have existed! This is a serious error! Who else is overwriting this?");
                return;
            }
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitMethodInsn(final int opcode, @Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc, final boolean itf) {
            if (this.stop) {
                final String opcodeDesc = owner + "." + name + " " + desc + (itf? " (itf)" : "");
                if (opcode == Opcodes.INVOKEVIRTUAL) {
                    L.d("Eaten opcode 'INVOKEVIRTUAL " + opcodeDesc + "'");
                    return;
                }
                L.e("Eaten opcode '" + opcode + " " + opcodeDesc + "' that shouldn't have existed! This is a serious error! Who else is overwriting this?");
                return;
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

        @Override
        public void visitTypeInsn(final int opcode, @Nonnull final String type) {
            super.visitTypeInsn(opcode, type);

            if (opcode == Opcodes.NEW && "crafttweaker/socket/SingleError".equals(type)) {
                this.newSocket = true;
                L.i("Found target instruction 'NEW crafttweaker/socket/SingleError': marking it as found");
            }
        }

        @Override
        public void visitInsn(final int opcode) {
            super.visitInsn(opcode);

            if (!(this.newSocket && opcode == Opcodes.DUP)) return;

            this.newSocket = false;
            L.i("Found target instruction 'DUP': performing injection now");

            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitLdcInsn("system");
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, THIS_CLASS_NAME, GET_FILE_NAME_OR_DEFAULT_METHOD_NAME,
                    "(Lstanhebben/zenscript/util/ZenPosition;Ljava/lang/String;)Ljava/lang/String;", false);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitInsn(Opcodes.ICONST_M1);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, THIS_CLASS_NAME, GET_LINE_OR_DEFAULT_METHOD_NAME,
                    "(Lstanhebben/zenscript/util/ZenPosition;I)I", false);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitInsn(Opcodes.ICONST_M1);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, THIS_CLASS_NAME, GET_LINE_OFFSET_OR_DEFAULT_METHOD_NAME,
                    "(Lstanhebben/zenscript/util/ZenPosition;I)I", false);

            L.i("Injection completed: stopping future calls until 'ALOAD 2'");
            this.stop = true;
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class GetOrDefaultMethodVisitor extends MethodVisitor {

        //  // access flags 0x2
        //  private <fermion-param:method-name>(Lstanhebben/zenscript/util/ZenPosition;<fermion-param:return-type>)<fermion-param:return-type>
        //   L0
        //    LINENUMBER 40 L0
        //    ALOAD 1
        //    IFNONNULL L1
        //    <fermion-calc:xLOAD-for-type> 2
        //    <fermion-calc:xRETURN-for-type>
        //   L1
        //    LINENUMBER 41 L1
        //   FRAME SAME
        //    ALOAD 1
        //    INVOKEVIRTUAL stanhebben/zenscript/util/ZenPosition.<fermion-param:target-method> ()<fermion-param:return-type>
        //    <fermion-calc:xRETURN-for-type>
        //   L2
        //    LOCALVARIABLE this Lcrafttweaker/zenscript/CrtStoringErrorLogger; L0 L2 0
        //    LOCALVARIABLE zenPos Lstanhebben/zenscript/util/ZenPosition; L0 L2 1
        //    LOCALVARIABLE default <fermion-param:return-type> L0 L2 2
        //    MAXSTACK = 1
        //    MAXLOCALS = 3

        private final String targetMethod;
        private final String returnType;

        GetOrDefaultMethodVisitor(final int api, @Nonnull final MethodVisitor mv, @Nonnull final String targetMethod, @Nonnull final String returnType) {
            super(api, mv);
            this.targetMethod = targetMethod;
            this.returnType = returnType;
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            final Label l1 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(4 * 10, l0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitJumpInsn(Opcodes.IFNONNULL, l1);
            super.visitVarInsn(this.identifyLoadOpcodeFromReturnType(), 2);
            super.visitInsn(this.identifyReturnOpcodeFromReturnType());

            super.visitLabel(l1);
            super.visitLineNumber(4 * 10 + 1, l1);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "stanhebben/zenscript/util/ZenPosition", this.targetMethod, "()" + this.returnType, false);
            super.visitInsn(this.identifyReturnOpcodeFromReturnType());

            final Label l2 = new Label();
            super.visitLabel(l2);

            super.visitLocalVariable("this", "Lcrafttweaker/zenscript/CrtStoringErrorLogger;", null, l0, l2, 0);
            super.visitLocalVariable("zenPos", "Lstanhebben/zenscript/util/ZenPosition;", null, l0, l2, 1);
            super.visitLocalVariable("default", this.returnType, null, l0, l2, 2);

            super.visitMaxs(1, 3);

            super.visitEnd();
        }

        private int identifyLoadOpcodeFromReturnType() {
            switch (this.returnType) {
                case "Ljava/lang/String;": return Opcodes.ALOAD;
                case "I": return Opcodes.ILOAD;
                default: throw new IllegalArgumentException(this.returnType + " isn't known");
            }
        }

        private int identifyReturnOpcodeFromReturnType() {
            switch (this.returnType) {
                case "Ljava/lang/String;": return Opcodes.ARETURN;
                case "I": return Opcodes.IRETURN;
                default: throw new IllegalArgumentException(this.returnType + " isn't known");
            }
        }
    }

    private static final Log L = Log.of("CrtStoringErrorLogger");
    private static final String THIS_CLASS_NAME = "crafttweaker/zenscript/CrtStoringErrorLogger";
    private static final String GET_FILE_NAME_OR_DEFAULT_METHOD_NAME = "fermion$$injected$$getFileNameOrDefault$$generated$$00_53_1122";
    private static final String GET_LINE_OR_DEFAULT_METHOD_NAME = "fermion$$injected$$getLineOrDefault$$generated$$00_53_1122";
    private static final String GET_LINE_OFFSET_OR_DEFAULT_METHOD_NAME = "fermion$$injected$$getLineOffsetOrDefault$$generated$$00_53_1122";

    public CrtStoringErrorLoggerTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("crt_storing_error_logger")
                        .setDescription("Removes a spam of NPE in the logs that occurs when registering the same ZenScript class twice, which happens in-dev when depending on API only")
                        .build(),
                ClassDescriptor.of(THIS_CLASS_NAME)
        );
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            @SuppressWarnings("SpellCheckingInspection")
            public MethodVisitor visitMethod(final int access, @Nonnull final String name, @Nonnull final String desc,
                                             @Nullable final String signature, @Nullable final String[] exceptions) {
                final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);

                if (("error".equals(name) || "warning".equals(name) || "info".equals(name))
                        && "(Lstanhebben/zenscript/util/ZenPosition;Ljava/lang/String;)V".equals(desc)) {
                    L.i("Found target method '" + name + " " + desc + "': beginning patching");
                    return new NullCheckerAdderMethodVisitor(v, parent);
                }

                return parent;
            }

            @Override
            public void visitEnd() {
                L.i("Generating method '" + GET_FILE_NAME_OR_DEFAULT_METHOD_NAME + "' at the end of class");
                new GetOrDefaultMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE,
                                GET_FILE_NAME_OR_DEFAULT_METHOD_NAME,
                                "(Lstanhebben/zenscript/util/ZenPosition;Ljava/lang/String;)Ljava/lang/String;",
                                null,
                                null
                        ),
                        "getFileName",
                        "Ljava/lang/String;"
                ).visitCode();

                L.i("Generating method '" + GET_LINE_OR_DEFAULT_METHOD_NAME + "' at the end of class");
                new GetOrDefaultMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE,
                                GET_LINE_OR_DEFAULT_METHOD_NAME,
                                "(Lstanhebben/zenscript/util/ZenPosition;I)I",
                                null,
                                null
                        ),
                        "getLine",
                        "I"
                ).visitCode();

                L.i("Generating method '" + GET_LINE_OFFSET_OR_DEFAULT_METHOD_NAME + "' at the end of class");
                new GetOrDefaultMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE,
                                GET_LINE_OFFSET_OR_DEFAULT_METHOD_NAME,
                                "(Lstanhebben/zenscript/util/ZenPosition;I)I",
                                null,
                                null
                        ),
                        "getLineOffset",
                        "I"
                ).visitCode();

                L.i("Method generation completed: patching successful");

                super.visitEnd();
            }
        };
    }
}
