package net.thesilkminer.mc.boson.asm.transformer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.thesilkminer.mc.boson.asm.utility.Log;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.TargetMethodTransformer;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiFunction;

public final class LoadControllerTransformer extends TargetMethodTransformer {

    private static final MethodDescriptor DISTRIBUTE_STATE_MESSAGE = MethodDescriptor.of(
            "distributeStateMessage",
            ImmutableList.of(
                    ClassDescriptor.of("net.minecraftforge.fml.common.LoaderState"),
                    ClassDescriptor.of(Object[].class)
            ),
            ClassDescriptor.of(void.class)
    );

    private static final MethodDescriptor PROPAGATE_STATE_MESSAGE = MethodDescriptor.of(
            "propogateStateMessage",
            ImmutableList.of(ClassDescriptor.of("net.minecraftforge.fml.common.event.FMLEvent")),
            ClassDescriptor.of(void.class)
    );

    private static final Log L = Log.of("Load Controller");

    public LoadControllerTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("load_controller")
                        .setDescription("This edits the LoadController so that Boson can fire its LoadComplete event before other mods get a chance to")
                        .build(),
                ClassDescriptor.of("net.minecraftforge.fml.common.LoadController"),
                DISTRIBUTE_STATE_MESSAGE,
                PROPAGATE_STATE_MESSAGE
        );
    }

    @Nonnull
    @Override
    @SuppressWarnings("SpellCheckingInspection")
    protected Map<MethodDescriptor, BiFunction<MethodDescriptor, Pair<Integer, MethodVisitor>, MethodVisitor>> getMethodVisitorCreators() {
        return ImmutableMap.of(
                DISTRIBUTE_STATE_MESSAGE, (desc, pair) -> new MethodVisitor(pair.getLeft(), pair.getRight()) {
                    // <<< INJECTION BEGIN
                    //   L800
                    //    LINENUMBER 132 L800
                    //    ALOAD 1
                    //    GETSTATIC net/minecraftforge/fml/common/LoaderState.AVAILABLE : Lnet/minecraftforge/fml/common/LoaderState;
                    //    IF_ACMPNE L801
                    //    INVOKESTATIC net/thesilkminer/mc/boson/hook/GameDataHook.stepBosonAvailableBar ()V
                    //    ALOAD 0
                    //    GETFIELD net/minecraftforge/fml/common/LoadController.masterChannel : Lcom/google/common/eventbus/EventBus;
                    //    NEW net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent
                    //    DUP
                    //    ALOAD 2
                    //    INVOKESPECIAL net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent.<init> ([Ljava/lang/Object;)V
                    //    INVOKEVIRTUAL com/google/common/eventbus/EventBus.post (Ljava/lang/Object;)V
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
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/hook/GameDataHook", "stepBosonAvailableBar", "()V", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/fml/common/LoadController", "masterChannel", "Lcom/google/common/eventbus/EventBus;");
                        super.visitTypeInsn(Opcodes.NEW, "net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent");
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 2);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/thesilkminer/mc/boson/api/event/BosonPreAvailableEvent", "<init>", "([Ljava/lang/Object;)V", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/common/eventbus/EventBus", "post", "(Ljava/lang/Object;)V", false);

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
                },
                PROPAGATE_STATE_MESSAGE, (desc, pair) -> new MethodVisitor(pair.getLeft(), pair.getRight()) {
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
                    //    INVOKESTATIC net/thesilkminer/mc/boson/hook/GameDataHook.getDescription (Lnet/minecraftforge/fml/common/event/FMLEvent;)Ljava/lang/String;
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

                    @Override
                    public void visitMethodInsn(final int opcode, @Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc, final boolean itf) {
                        if ("description".equals(name) && "net/minecraftforge/fml/common/event/FMLEvent".equals(owner)) {
                            L.i("Found 'INVOKEVIRTUAL net/minecraftforge/fml/common/event/FMLEvent.description ()Ljava/lang/String;'");
                            L.i("Replacing with hook");
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/hook/GameDataHook", "getDescription", "(Lnet/minecraftforge/fml/common/event/FMLEvent;)Ljava/lang/String;", false);
                            return;
                        }
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                    }
                }
        );
    }
}
