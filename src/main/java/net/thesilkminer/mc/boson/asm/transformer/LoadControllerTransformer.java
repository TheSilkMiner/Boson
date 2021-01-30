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
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public final class LoadControllerTransformer extends AbstractTransformer {
    @SuppressWarnings("SpellCheckingInspection")
    private static final class DistributeStateMessageMethodVisitor extends MethodVisitor {
        // <<< INJECTION BEGIN
        //   L800
        //    LINENUMBER 132 L800
        //    ALOAD 1
        //    GETSTATIC net/minecraftforge/fml/common/LoaderState.AVAILABLE : Lnet/minecraftforge/fml/common/LoaderState;
        //    IF_ACMPNE L801
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/fml/common/LoadController.masterChannel : Lcom/google/common/eventbus/EventBus;
        //    NEW net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent
        //    DUP
        //    ALOAD 2
        //    INVOKESPECIAL net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent.<init> ([Ljava/lang/Object;)V
        //    INVOKEVIRTUAL com/google/common/eventbus/EventBus.post (Ljava/lang/Object;)V
        //    INVOKESTATIC net/minecraftforge/fml/common/LoadController.<fermion-inject:stepBosonAvailableBar> ()V
        //   L801
        //    LINENUMBER 132 L801
        //   FRAME SAME
        // >>> INJECTION END
        //   L0
        //    LINENUMBER 134 L0
        //    ALOAD 1
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/LoaderState.hasEvent ()Z
        //    IFEQ L1
        //   L2
        //    LINENUMBER 136 L2
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/fml/common/LoadController.masterChannel : Lcom/google/common/eventbus/EventBus;
        //    ALOAD 1
        //    ALOAD 2
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/LoaderState.getEvent ([Ljava/lang/Object;)Lnet/minecraftforge/fml/common/event/FMLStateEvent;
        //    INVOKEVIRTUAL com/google/common/eventbus/EventBus.post (Ljava/lang/Object;)V
        //   L1
        //    LINENUMBER 138 L1
        //   FRAME SAME
        //    RETURN
        //   L3
        //    LOCALVARIABLE this Lnet/minecraftforge/fml/common/LoadController; L0 L3 0
        //    LOCALVARIABLE state Lnet/minecraftforge/fml/common/LoaderState; L0 L3 1
        //    LOCALVARIABLE eventData [Ljava/lang/Object; L0 L3 2
        // <<< OVERWRITE BEGIN
        //    MAXSTACK = 3
        // === OVERWRITE WITH
        //    MAXSTACK = 4
        // >>> OVERWRITE END
        //    MAXLOCALS = 3

        private DistributeStateMessageMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            L.i("Code visiting has started: it's time to inject");

            final Label l800 = new Label();
            final Label l801 = new Label();
            super.visitLabel(l800);
            super.visitLineNumber(100 + 3 * 10 + 2, l800);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/fml/common/LoaderState", "AVAILABLE", "Lnet/minecraftforge/fml/common/LoaderState;");
            super.visitJumpInsn(Opcodes.IF_ACMPNE, l801);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/fml/common/LoadController", "masterChannel", "Lcom/google/common/eventbus/EventBus;");
            super.visitTypeInsn(Opcodes.NEW, "net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent");
            super.visitInsn(Opcodes.DUP);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent", "<init>", "([Ljava/lang/Object;)V", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/common/eventbus/EventBus", "post", "(Ljava/lang/Object;)V", false);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, STEP_BOSON_AVAILABLE_BAR, "()V", false);

            super.visitLabel(l801);
            super.visitLineNumber(100 + 3 * 10 + 3, l801);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

            L.i("Injected entirety of method code");
        }

        @Override
        public void visitMaxs(final int maxStack, final int maxLocals) {
            L.i("Replacing maxStack with " + (maxStack + 1));
            super.visitMaxs(maxStack + 1, maxLocals);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class PropagateStateMessageMethodVisitor extends MethodVisitor {
        //  // access flags 0x1
        //  public propogateStateMessage(Lnet/minecraftforge/fml/common/event/FMLEvent;)V
        //  @Lcom/google/common/eventbus/Subscribe;()
        //   L0
        //    LINENUMBER 189 L0
        //    ALOAD 1
        //    INSTANCEOF net/minecraftforge/fml/common/event/FMLPreInitializationEvent
        //    IFEQ L1
        //   L2
        //    LINENUMBER 191 L2
        //    ALOAD 0
        //    ALOAD 0
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/LoadController.buildModObjectList ()Lcom/google/common/collect/ImmutableBiMap;
        //    PUTFIELD net/minecraftforge/fml/common/LoadController.modObjectList : Lcom/google/common/collect/BiMap;
        //   L1
        //    LINENUMBER 193 L1
        //   FRAME SAME
        //    ALOAD 1
        // <<< OVERWRITE BEGIN
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/event/FMLEvent.description ()Ljava/lang/String;
        // === OVERWRITE WITH
        //    INVOKESTATIC net/minecraftforge/fml/common/LoadController.<fermion-inject:getDescription> (Lnet/minecraftforge/fml/common/event/FMLEvent;)Ljava/lang/String;
        // >>> OVERWRITE END
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/fml/common/LoadController.activeModList : Ljava/util/List;
        //    INVOKEINTERFACE java/util/List.size ()I (itf)
        //    ICONST_1
        //    INVOKESTATIC net/minecraftforge/fml/common/ProgressManager.push (Ljava/lang/String;IZ)Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;
        //    ASTORE 2
        //   L3
        //    LINENUMBER 194 L3
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/fml/common/LoadController.activeModList : Ljava/util/List;
        //    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator; (itf)
        //    ASTORE 3
        //   L4
        //   FRAME APPEND [net/minecraftforge/fml/common/ProgressManager$ProgressBar java/util/Iterator]
        //    ALOAD 3
        //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
        //    IFEQ L5
        //    ALOAD 3
        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
        //    CHECKCAST net/minecraftforge/fml/common/ModContainer
        //    ASTORE 4
        //   L6
        //    LINENUMBER 196 L6
        //    ALOAD 2
        //    ALOAD 4
        //    INVOKEINTERFACE net/minecraftforge/fml/common/ModContainer.getName ()Ljava/lang/String; (itf)
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
        //   L7
        //    LINENUMBER 197 L7
        //    ALOAD 0
        //    ALOAD 1
        //    ALOAD 4
        //    INVOKESPECIAL net/minecraftforge/fml/common/LoadController.sendEventToModContainer (Lnet/minecraftforge/fml/common/event/FMLEvent;Lnet/minecraftforge/fml/common/ModContainer;)V
        //   L8
        //    LINENUMBER 198 L8
        //    GOTO L4
        //   L5
        //    LINENUMBER 199 L5
        //   FRAME CHOP 1
        //    ALOAD 2
        //    INVOKESTATIC net/minecraftforge/fml/common/ProgressManager.pop (Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;)V
        //   L9
        //    LINENUMBER 200 L9
        //    RETURN
        //   L10
        //    LOCALVARIABLE mc Lnet/minecraftforge/fml/common/ModContainer; L6 L8 4
        //    LOCALVARIABLE this Lnet/minecraftforge/fml/common/LoadController; L0 L10 0
        //    LOCALVARIABLE stateEvent Lnet/minecraftforge/fml/common/event/FMLEvent; L0 L10 1
        //    LOCALVARIABLE bar Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar; L3 L10 2
        //    MAXSTACK = 3
        //    MAXLOCALS = 5

        private PropagateStateMessageMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitMethodInsn(final int opcode, @Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc, final boolean itf) {
            if ("description".equals(name) && "net/minecraftforge/fml/common/event/FMLEvent".equals(owner)) {
                L.i("Found 'INVOKEVIRTUAL net/minecraftforge/fml/common/event/FMLEvent.description ()Ljava/lang/String;'");
                L.i("Replacing with hook");
                super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, GET_DESCRIPTION, "(Lnet/minecraftforge/fml/common/event/FMLEvent;)Ljava/lang/String;", false);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class ReflectProgressBarFromLoaderMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private final static reflectProgressBarFromLoader()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;
        //    TRYCATCHBLOCK L0 L1 L2 java/lang/ReflectiveOperationException
        //   L0
        //    LINENUMBER 437 L0
        //    NOP
        //   L3
        //    LINENUMBER 438 L3
        //    LDC Lnet/minecraftforge/fml/common/Loader;.class
        //    LDC "progressBar"
        //    INVOKEVIRTUAL java/lang/Class.getDeclaredField (Ljava/lang/String;)Ljava/lang/reflect/Field;
        //    ASTORE 0
        //   L4
        //    LINENUMBER 439 L4
        //    ALOAD 0
        //    ICONST_1
        //    INVOKEVIRTUAL java/lang/reflect/Field.setAccessible (Z)V
        //   L5
        //    LINENUMBER 440 L5
        //    ALOAD 0
        //    INVOKESTATIC net/minecraftforge/fml/common/Loader.instance ()Lnet/minecraftforge/fml/common/Loader;
        //    INVOKEVIRTUAL java/lang/reflect/Field.get (Ljava/lang/Object;)Ljava/lang/Object;
        //    CHECKCAST net/minecraftforge/fml/common/ProgressManager$ProgressBar
        //   L1
        //    ARETURN
        //   L2
        //    LINENUMBER 441 L2
        //   FRAME FULL [] [java/lang/ReflectiveOperationException]
        //    ASTORE 0
        //   L6
        //    LINENUMBER 442 L6
        //    NEW java/lang/RuntimeException
        //    DUP
        //    ALOAD 0
        //    CHECKCAST java/lang/Throwable
        //    INVOKESPECIAL java/lang/RuntimeException.<init> (Ljava/lang/Throwable;)V
        //    CHECKCAST java/lang/Throwable
        //    ATHROW
        //   L7
        //    LOCALVARIABLE field Ljava/lang/reflect/Field; L3 L1 0
        //    LOCALVARIABLE e Ljava/lang/ReflectiveOperationException; L6 L7 0
        //    MAXSTACK = 4
        //    MAXLOCALS = 1

        private ReflectProgressBarFromLoaderMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitTryCatchBlock(l0, l1, l2, "java/lang/ReflectiveOperationException");

            super.visitLabel(l0);
            super.visitLineNumber(4 * 100 + 3 * 10 + 7, l0);
            super.visitInsn(Opcodes.NOP);

            final Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(4 * 100 + 3 * 10 + 8, l3);
            super.visitLdcInsn(Type.getType("Lnet/minecraftforge/fml/common/Loader;"));
            super.visitLdcInsn("progressBar");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            super.visitVarInsn(Opcodes.ASTORE, 0);

            final Label l4 = new Label();
            super.visitLabel(l4);
            super.visitLineNumber(4 * 100 + 3 * 10 + 9, l4);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitInsn(Opcodes.ICONST_1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);

            final Label l5 = new Label();
            super.visitLabel(l5);
            super.visitLineNumber(4 * 100 + 4 * 10, l5);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/Loader", "instance", "()Lnet/minecraftforge/fml/common/Loader;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraftforge/fml/common/ProgressManager$ProgressBar");

            super.visitLabel(l1);
            super.visitInsn(Opcodes.ARETURN);

            super.visitLabel(l2);
            super.visitLineNumber(4 * 100 + 4 * 10 + 1, l2);
            super.visitFrame(Opcodes.F_FULL, 0, new Object[] {}, 1, new Object[] { "java/lang/ReflectiveOperationException" });
            super.visitVarInsn(Opcodes.ASTORE, 0);

            final Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLineNumber(4 * 100 + 4 * 10 + 2, l6);
            super.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException");
            super.visitInsn(Opcodes.DUP);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Throwable");
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Throwable");
            super.visitInsn(Opcodes.ATHROW);

            final Label l7 = new Label();
            super.visitLabel(l7);

            super.visitLocalVariable("field", "Ljava/lang/reflect/Field;", null, l3, l1, 0);
            super.visitLocalVariable("e", "Ljava/lang/ReflectiveOperationException;", null, l6, l7, 0);

            super.visitMaxs(4, 1);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class StepBosonAvailableBarMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private final static stepBosonAvailableBar()V
        //   L0
        //    LINENUMBER 458 L0
        //    INVOKESTATIC net/minecraftforge/fml/common/LoadController.<fermion-inject:reflectProgressBarFromLoader> ()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;
        //    LDC "Propagating Loading Completion"
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
        //   L1
        //    LINENUMBER 459 L1
        //    RETURN
        //   L2
        //    MAXSTACK = 2
        //    MAXLOCALS = 0

        private StepBosonAvailableBarMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(4 * 100 + 5 * 10 + 8, l0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, REFLECT_PROGRESS_BAR_FROM_LOADER, "()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;", false);
            super.visitLdcInsn("Propagating Loading Completion");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

            final Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(4 * 100 + 5 * 10 + 9, l1);
            super.visitInsn(Opcodes.RETURN);

            final Label l2 = new Label();
            super.visitLabel(l2);

            super.visitMaxs(2, 0);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class GetDescriptionMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private final static getDescription(Lnet/minecraftforge/fml/common/event/FMLEvent;)Ljava/lang/String;
        //   L0
        //    LINENUMBER 462 L0
        //    ALOAD 0
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/event/FMLEvent.description ()Ljava/lang/String;
        //    ASTORE 1
        //   L1
        //    LDC "onPreAvailable"
        //    ALOAD 1
        //    INVOKEVIRTUAL java/lang/String.equals (Ljava/lang/Object;)Z
        //    IFEQ L2
        //    LDC "BosonPreAvailable"
        //    GOTO L3
        //   L2
        //   FRAME APPEND [java/lang/String]
        //    ALOAD 1
        //   L3
        //   FRAME FULL [net/minecraftforge/fml/common/event/FMLEvent java/lang/String] [java/lang/String]
        //    ARETURN
        //   L4
        //    LOCALVARIABLE event Lnet/minecraftforge/fml/common/event/FMLEvent; L0 L4 0
        //    LOCALVARIABLE desc Ljava/lang/String; L1 L4 1
        //    MAXSTACK = 2
        //    MAXLOCALS = 2

        private GetDescriptionMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(4 * 100 + 6 * 10 + 2, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/event/FMLEvent", "description", "()Ljava/lang/String;", false);
            super.visitVarInsn(Opcodes.ASTORE, 1);

            final Label l1 = new Label();
            final Label l2 = new Label();
            final Label l3 = new Label();
            super.visitLabel(l1);
            super.visitLdcInsn("onPreAvailable");
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l2);
            super.visitLdcInsn("BosonPreAvailable");
            super.visitJumpInsn(Opcodes.GOTO, l3);

            super.visitLabel(l2);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/String" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 1);

            super.visitLabel(l3);
            super.visitFrame(Opcodes.F_FULL, 2, new Object[] { "net/minecraftforge/fml/common/event/FMLEvent", "java/lang/String" }, 1, new Object[] { "java/lang/String" });
            super.visitInsn(Opcodes.ARETURN);

            final Label l4 = new Label();
            super.visitLabel(l4);

            super.visitLocalVariable("event", "Lnet/minecraftforge/fml/common/event/FMLEvent;", null, l0, l4, 0);
            super.visitLocalVariable("desc", "Ljava/lang/String;", null, l1, l4, 1);

            super.visitMaxs(2, 2);
            super.visitEnd();
        }
    }

    private static final Log L = Log.of("Load Controller");

    private static final String THIS = "net/minecraftforge/fml/common/LoadController";
    private static final String REFLECT_PROGRESS_BAR_FROM_LOADER = "fermion$$injected$$reflectProgressBarFromLoader$$generated$$00_01_1122";
    private static final String STEP_BOSON_AVAILABLE_BAR = "fermion$$injected$$stepBosonAvailableBar$$generated$$00_01_1122";
    private static final String GET_DESCRIPTION = "fermion$$injected$$getDescription$$generated$$00_01_1122";

    public LoadControllerTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("load_controller")
                        .setDescription("This edits the LoadController so that Boson can fire its LoadComplete event before other mods get a chance to")
                        .build(),
                ClassDescriptor.of(THIS)
        );
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            public MethodVisitor visitMethod(final int access, @Nonnull final String name, @Nonnull final String desc, @Nullable final String signature, @Nullable final String[] exceptions) {
                final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
                if ("distributeStateMessage".equals(name) && "(Lnet/minecraftforge/fml/common/LoaderState;[Ljava/lang/Object;)V".equals(desc)) {
                    return new DistributeStateMessageMethodVisitor(v, parent);
                }
                if ("propogateStateMessage".equals(name) && "(Lnet/minecraftforge/fml/common/event/FMLEvent;)V".equals(desc)) {
                    return new PropagateStateMessageMethodVisitor(v, parent);
                }
                return parent;
            }

            @Override
            public void visitEnd() {
                new ReflectProgressBarFromLoaderMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                REFLECT_PROGRESS_BAR_FROM_LOADER,
                                "()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;",
                                null,
                                null
                        )
                ).visitCode();

                new StepBosonAvailableBarMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                STEP_BOSON_AVAILABLE_BAR,
                                "()V",
                                null,
                                null
                        )
                ).visitCode();

                new GetDescriptionMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                GET_DESCRIPTION,
                                "(Lnet/minecraftforge/fml/common/event/FMLEvent;)Ljava/lang/String;",
                                null,
                                null
                        )
                ).visitCode();

                super.visitEnd();
            }
        };
    }
}
