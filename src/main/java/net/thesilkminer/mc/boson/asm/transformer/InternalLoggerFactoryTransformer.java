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

package net.thesilkminer.mc.boson.asm.transformer;

import com.google.common.collect.ImmutableList;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public final class InternalLoggerFactoryTransformer extends SingleTargetMethodTransformer {

    public InternalLoggerFactoryTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("internal_logger_factory")
                        .setDescription("Fixes Netty logging framework attempting to use SLF4J for logging, since Boson is shading it due to dependencies")
                        .build(),
                ClassDescriptor.of("io.netty.util.internal.logging.InternalLoggerFactory"),
                MethodDescriptor.of(
                        "newDefaultFactory",
                        ImmutableList.of(ClassDescriptor.of("java.lang.String")),
                        ClassDescriptor.of("io.netty.util.internal.logging.InternalLoggerFactory")
                )
        );
    }

    @Nonnull
    @Override
    @SuppressWarnings("SpellCheckingInspection")
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            //  // access flags 0xA
            //  private static newDefaultFactory(Ljava/lang/String;)Lio/netty/util/internal/logging/InternalLoggerFactory;
            //    TRYCATCHBLOCK L0 L1 L2 java/lang/Throwable
            //    TRYCATCHBLOCK L3 L4 L5 java/lang/Throwable
            //   L0
            //    LINENUMBER 42 L0
            //    NEW io/netty/util/internal/logging/Slf4JLoggerFactory
            //    DUP
            //    ICONST_1
            //    INVOKESPECIAL io/netty/util/internal/logging/Slf4JLoggerFactory.<init> (Z)V
            //    ASTORE 1
            // <<< INJECTION BEGIN
            //    LDC "No you won't ASTORE that"
            //    POP
            //    NOP
            //    NEW java/lang/Throwable
            //    DUP
            //    INVOKESPECIAL java/lang/Throwable.<init> ()V
            //    ATHROW
            // >>> INJECTION END
            //   L6
            //    LINENUMBER 43 L6
            // <<< INJECTION BEGIN
            //   FRAME APPEND [io/netty/util/internal/logging/InternalLoggerFactory]
            // >>> INJECTION END
            //    ALOAD 1
            //    ALOAD 0
            //    INVOKEVIRTUAL io/netty/util/internal/logging/InternalLoggerFactory.newInstance (Ljava/lang/String;)Lio/netty/util/internal/logging/InternalLogger;
            //    LDC "Using SLF4J as the default logging framework"
            //    INVOKEINTERFACE io/netty/util/internal/logging/InternalLogger.debug (Ljava/lang/String;)V (itf)
            //   L1
            //    LINENUMBER 52 L1
            //    GOTO L7
            //   L2
            //    LINENUMBER 44 L2
            //   FRAME SAME1 java/lang/Throwable
            //    ASTORE 2
            //   L3
            //    LINENUMBER 46 L3
            //    GETSTATIC io/netty/util/internal/logging/Log4JLoggerFactory.INSTANCE : Lio/netty/util/internal/logging/InternalLoggerFactory;
            //    ASTORE 1
            //   L8
            //    LINENUMBER 47 L8
            //    ALOAD 1
            //    ALOAD 0
            //    INVOKEVIRTUAL io/netty/util/internal/logging/InternalLoggerFactory.newInstance (Ljava/lang/String;)Lio/netty/util/internal/logging/InternalLogger;
            //    LDC "Using Log4J as the default logging framework"
            //    INVOKEINTERFACE io/netty/util/internal/logging/InternalLogger.debug (Ljava/lang/String;)V (itf)
            //   L4
            //    LINENUMBER 51 L4
            //    GOTO L7
            //   L5
            //    LINENUMBER 48 L5
            //   FRAME FULL [java/lang/String T java/lang/Throwable] [java/lang/Throwable]
            //    ASTORE 3
            //   L9
            //    LINENUMBER 49 L9
            //    GETSTATIC io/netty/util/internal/logging/JdkLoggerFactory.INSTANCE : Lio/netty/util/internal/logging/InternalLoggerFactory;
            //    ASTORE 1
            //   L10
            //    LINENUMBER 50 L10
            //    ALOAD 1
            //    ALOAD 0
            //    INVOKEVIRTUAL io/netty/util/internal/logging/InternalLoggerFactory.newInstance (Ljava/lang/String;)Lio/netty/util/internal/logging/InternalLogger;
            //    LDC "Using java.util.logging as the default logging framework"
            //    INVOKEINTERFACE io/netty/util/internal/logging/InternalLogger.debug (Ljava/lang/String;)V (itf)
            //   L7
            //    LINENUMBER 53 L7
            //   FRAME FULL [java/lang/String io/netty/util/internal/logging/InternalLoggerFactory] []
            //    ALOAD 1
            //    ARETURN
            //   L11
            //    LOCALVARIABLE f Lio/netty/util/internal/logging/InternalLoggerFactory; L6 L2 1
            //    LOCALVARIABLE f Lio/netty/util/internal/logging/InternalLoggerFactory; L8 L5 1
            //    LOCALVARIABLE t2 Ljava/lang/Throwable; L9 L7 3
            //    LOCALVARIABLE t1 Ljava/lang/Throwable; L3 L7 2
            //    LOCALVARIABLE name Ljava/lang/String; L0 L11 0
            //    LOCALVARIABLE f Lio/netty/util/internal/logging/InternalLoggerFactory; L10 L11 1
            //    MAXSTACK = 3
            //    MAXLOCALS = 4

            private boolean hasVisitedFirstAStoreOne;
            private boolean hasVisitedFirstALoadOne;

            @Override
            public void visitVarInsn(final int opcode, final int var) {
                if (!this.hasVisitedFirstALoadOne && opcode == Opcodes.ALOAD && var == 1) {
                    this.hasVisitedFirstALoadOne = true;
                    super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "io/netty/util/internal/logging/InternalLoggerFactory" }, 0, null);
                }
                super.visitVarInsn(opcode, var);
                if (!this.hasVisitedFirstAStoreOne && opcode == Opcodes.ASTORE && var == 1) {
                    this.hasVisitedFirstAStoreOne = true;
                    super.visitLdcInsn("No you won't ASTORE that");
                    super.visitInsn(Opcodes.POP);
                    super.visitInsn(Opcodes.NOP);
                    super.visitTypeInsn(Opcodes.NEW, "java/lang/Throwable");
                    super.visitInsn(Opcodes.DUP);
                    super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false);
                    super.visitInsn(Opcodes.ATHROW);
                }
            }
        };
    }
}
