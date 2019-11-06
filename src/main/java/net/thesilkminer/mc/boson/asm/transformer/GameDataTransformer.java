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

public final class GameDataTransformer extends TargetMethodTransformer {

    private static final Log LOGGER = Log.of("Game Data");

    private static final MethodDescriptor FIRE_CREATE_REGISTRY_EVENTS = MethodDescriptor.of(
            "fireCreateRegistryEvents",
            ImmutableList.of(),
            ClassDescriptor.of(void.class)
    );

    private static final MethodDescriptor FIRE_REGISTRY_EVENTS = MethodDescriptor.of(
            "fireRegistryEvents",
            ImmutableList.of(ClassDescriptor.of("java.util.function.Predicate")),
            ClassDescriptor.of(void.class)
    );

    public GameDataTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("game_data")
                        .setDescription("Transforms GameData so that the fired events are shown on the ProgressBar")
                        .build(),
                ClassDescriptor.of("net.minecraftforge.registries.GameData"),
                FIRE_CREATE_REGISTRY_EVENTS,
                FIRE_REGISTRY_EVENTS
        );
    }

    @Nonnull
    @Override
    @SuppressWarnings("SpellCheckingInspection")
    protected Map<MethodDescriptor, BiFunction<MethodDescriptor, Pair<Integer, MethodVisitor>, MethodVisitor>> getMethodVisitorCreators() {
        return ImmutableMap.of(
                FIRE_CREATE_REGISTRY_EVENTS, (desc, pair) -> new MethodVisitor(pair.getLeft(), pair.getRight()) {
                    @Override
                    public void visitInsn(final int opcode) {
                        if (opcode != Opcodes.RETURN) {
                            super.visitInsn(opcode);
                            return;
                        }

                        LOGGER.i("Found RETURN instruction for method '" + desc + "': injecting progress bar code now");

                        final Label l0 = new Label();
                        super.visitLabel(l0);
                        super.visitLineNumber(8 * 100 + 2 * 10 + 6, l0);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/hook/GameDataHook", "showPreInitializationCreationBar", "()V", false);

                        super.visitInsn(opcode);
                    }
                },
                FIRE_REGISTRY_EVENTS, (desc, pair) -> {
                    final MethodVisitor parent = pair.getRight();
                    return new MethodVisitor(pair.getLeft(), null) {
                        // NOTE: The given bytecode represents the end state of the method: this method is being overwritten
                        // by Boson, but this process shouldn't cause any issues (hopefully)

                        //  // access flags 0x9
                        //  // signature (Ljava/util/function/Predicate<Lnet/minecraft/util/ResourceLocation;>;)V
                        //  // declaration: void fireRegistryEvents(java.util.function.Predicate<net.minecraft.util.ResourceLocation>)
                        //  public static fireRegistryEvents(Ljava/util/function/Predicate;)V
                        //   L0
                        //    LINENUMBER 835 L0
                        //    INVOKESTATIC net/thesilkminer/mc/boson/hook/GameDataHook.stepPopulatingRegistryBar ()V
                        //   L1
                        //    LINENUMBER 836 L1
                        //    GETSTATIC net/minecraftforge/registries/RegistryManager.ACTIVE : Lnet/minecraftforge/registries/RegistryManager;
                        //    GETFIELD net/minecraftforge/registries/RegistryManager.registries : Lcom/google/common/collect/BiMap;
                        //    INVOKEINTERFACE com/google/common/collect/BiMap.keySet ()Ljava/util/Set; (itf)
                        //    INVOKESTATIC com/google/common/collect/Lists.newArrayList (Ljava/lang/Iterable;)Ljava/util/ArrayList;
                        //    ASTORE 1
                        //   L2
                        //    LINENUMBER 837 L2
                        //    LDC "RegistryEvent.Register"
                        //    ALOAD 1
                        //    INVOKEINTERFACE java/util/List.size ()I (itf)
                        //    INVOKESTATIC net/minecraftforge/fml/common/ProgressManager.push (Ljava/lang/String;I)Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;
                        //    ASTORE 2
                        //   L3
                        //    LINENUMBER 838 L3
                        //    ALOAD 1
                        //    INVOKESTATIC net/thesilkminer/mc/boson/hook/GameDataHook.getSortingComparator ()Ljava/util/Comparator;
                        //    INVOKESTATIC java/util/Collections.sort (Ljava/util/List;Ljava/util/Comparator;)V
                        //   L4
                        //    LINENUMBER 844 L4
                        //    ALOAD 2
                        //    LDC "minecraft:blocks"
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
                        //   L5
                        //    LINENUMBER 845 L5
                        //    ALOAD 0
                        //    GETSTATIC net/minecraftforge/registries/GameData.BLOCKS : Lnet/minecraft/util/ResourceLocation;
                        //    INVOKEINTERFACE java/util/function/Predicate.test (Ljava/lang/Object;)Z (itf)
                        //    IFEQ L6
                        //   L7
                        //    LINENUMBER 847 L7
                        //    GETSTATIC net/minecraftforge/common/MinecraftForge.EVENT_BUS : Lnet/minecraftforge/fml/common/eventhandler/EventBus;
                        //    GETSTATIC net/minecraftforge/registries/RegistryManager.ACTIVE : Lnet/minecraftforge/registries/RegistryManager;
                        //    GETSTATIC net/minecraftforge/registries/GameData.BLOCKS : Lnet/minecraft/util/ResourceLocation;
                        //    INVOKEVIRTUAL net/minecraftforge/registries/RegistryManager.getRegistry (Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;
                        //    GETSTATIC net/minecraftforge/registries/GameData.BLOCKS : Lnet/minecraft/util/ResourceLocation;
                        //    INVOKEVIRTUAL net/minecraftforge/registries/ForgeRegistry.getRegisterEvent (Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/eventhandler/EventBus.post (Lnet/minecraftforge/fml/common/eventhandler/Event;)Z
                        //    POP
                        //   L8
                        //    LINENUMBER 848 L8
                        //    GETSTATIC net/minecraftforge/registries/ObjectHolderRegistry.INSTANCE : Lnet/minecraftforge/registries/ObjectHolderRegistry;
                        //    INVOKEVIRTUAL net/minecraftforge/registries/ObjectHolderRegistry.applyObjectHolders ()V
                        //   L6
                        //    LINENUMBER 849 L6
                        //   FRAME APPEND [java/util/List net/minecraftforge/fml/common/ProgressManager$ProgressBar]
                        //    ALOAD 2
                        //    LDC "minecraft:items"
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
                        //   L9
                        //    LINENUMBER 850 L9
                        //    ALOAD 0
                        //    GETSTATIC net/minecraftforge/registries/GameData.ITEMS : Lnet/minecraft/util/ResourceLocation;
                        //    INVOKEINTERFACE java/util/function/Predicate.test (Ljava/lang/Object;)Z (itf)
                        //    IFEQ L10
                        //   L11
                        //    LINENUMBER 852 L11
                        //    GETSTATIC net/minecraftforge/common/MinecraftForge.EVENT_BUS : Lnet/minecraftforge/fml/common/eventhandler/EventBus;
                        //    GETSTATIC net/minecraftforge/registries/RegistryManager.ACTIVE : Lnet/minecraftforge/registries/RegistryManager;
                        //    GETSTATIC net/minecraftforge/registries/GameData.ITEMS : Lnet/minecraft/util/ResourceLocation;
                        //    INVOKEVIRTUAL net/minecraftforge/registries/RegistryManager.getRegistry (Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;
                        //    GETSTATIC net/minecraftforge/registries/GameData.ITEMS : Lnet/minecraft/util/ResourceLocation;
                        //    INVOKEVIRTUAL net/minecraftforge/registries/ForgeRegistry.getRegisterEvent (Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/eventhandler/EventBus.post (Lnet/minecraftforge/fml/common/eventhandler/Event;)Z
                        //    POP
                        //   L12
                        //    LINENUMBER 853 L12
                        //    GETSTATIC net/minecraftforge/registries/ObjectHolderRegistry.INSTANCE : Lnet/minecraftforge/registries/ObjectHolderRegistry;
                        //    INVOKEVIRTUAL net/minecraftforge/registries/ObjectHolderRegistry.applyObjectHolders ()V
                        //   L10
                        //    LINENUMBER 855 L10
                        //   FRAME SAME
                        //    ALOAD 1
                        //    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator; (itf)
                        //    ASTORE 3
                        //   L13
                        //   FRAME APPEND [java/util/Iterator]
                        //    ALOAD 3
                        //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
                        //    IFEQ L14
                        //    ALOAD 3
                        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
                        //    CHECKCAST net/minecraft/util/ResourceLocation
                        //    ASTORE 4
                        //   L15
                        //    LINENUMBER 857 L15
                        //    ALOAD 0
                        //    ALOAD 4
                        //    INVOKEINTERFACE java/util/function/Predicate.test (Ljava/lang/Object;)Z (itf)
                        //    IFNE L16
                        //    GOTO L13
                        //   L16
                        //    LINENUMBER 858 L16
                        //   FRAME APPEND [net/minecraft/util/ResourceLocation]
                        //    ALOAD 4
                        //    GETSTATIC net/minecraftforge/registries/GameData.BLOCKS : Lnet/minecraft/util/ResourceLocation;
                        //    IF_ACMPEQ L13
                        //    ALOAD 4
                        //    GETSTATIC net/minecraftforge/registries/GameData.ITEMS : Lnet/minecraft/util/ResourceLocation;
                        //    IF_ACMPNE L17
                        //    GOTO L13
                        //   L17
                        //    LINENUMBER 859 L17
                        //   FRAME SAME
                        //    ALOAD 2
                        //    ALOAD 4
                        //    INVOKEVIRTUAL net/minecraft/util/ResourceLocation.toString ()Ljava/lang/String;
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
                        //   L18
                        //    LINENUMBER 860 L18
                        //    GETSTATIC net/minecraftforge/common/MinecraftForge.EVENT_BUS : Lnet/minecraftforge/fml/common/eventhandler/EventBus;
                        //    GETSTATIC net/minecraftforge/registries/RegistryManager.ACTIVE : Lnet/minecraftforge/registries/RegistryManager;
                        //    ALOAD 4
                        //    INVOKEVIRTUAL net/minecraftforge/registries/RegistryManager.getRegistry (Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;
                        //    ALOAD 4
                        //    INVOKEVIRTUAL net/minecraftforge/registries/ForgeRegistry.getRegisterEvent (Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/eventhandler/EventBus.post (Lnet/minecraftforge/fml/common/eventhandler/Event;)Z
                        //    POP
                        //   L19
                        //    GOTO L13
                        //   L14
                        //    LINENUMBER 862 L14
                        //   FRAME CHOP 2
                        //    GETSTATIC net/minecraftforge/registries/ObjectHolderRegistry.INSTANCE : Lnet/minecraftforge/registries/ObjectHolderRegistry;
                        //    INVOKEVIRTUAL net/minecraftforge/registries/ObjectHolderRegistry.applyObjectHolders ()V
                        //   L20
                        //    LINENUMBER 863 L20
                        //   FRAME SAME
                        //    ALOAD 2
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.getStep ()I
                        //    ALOAD 2
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.getSteps ()I
                        //    IF_ICMPGE L21
                        //   L22
                        //    LINENUMBER 863 L22
                        //    ALOAD 2
                        //    LDC "forge:unknown"
                        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
                        //    GOTO L20
                        //   L21
                        //    LINENUMBER 865 L21
                        //   FRAME SAME
                        //    ALOAD 2
                        //    INVOKESTATIC net/minecraftforge/fml/common/ProgressManager.pop (Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;)V
                        //   L23
                        //    LINENUMBER 866 L23
                        //    RETURN
                        //   L24
                        //    LOCALVARIABLE boson$r Lnet/minecraft/util/ResourceLocation; L13 L17 4
                        //    LOCALVARIABLE boson$p Ljava/util/function/Predicate; L0 L24 0
                        //    // signature: Ljava/util/function/Predicate<Lnet/minecraft/util/ResourceLocation;>;
                        //    // declaration: boson$p extends java.util.function.Predicate<net.minecraft.util.ResourceLocation>
                        //    LOCALVARIABLE boson$l Ljava/util/List; L2 L24 1
                        //    // signature Ljava/util/List<Lnet/minecraft/util/ResourceLocation;>;
                        //    // declaration: boson$l extends java.util.List<net.minecraft.util.ResourceLocation>
                        //    LOCALVARIABLE boson$p Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar; L3 L24 2
                        //    MAXSTACK = 4
                        //    MAXLOCALS = 5

                        @Override
                        public void visitCode() {
                            LOGGER.i("Overwriting method with our own implementation");
                            LOGGER.i("   Why aren't we injecting? Because to use frames we'd have to intercept every Label and... no");

                            parent.visitCode();

                            final Label l0 = new Label();
                            parent.visitLabel(l0);
                            parent.visitLineNumber(8 * 100 + 3 * 10 + 5, l0);
                            parent.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/hook/GameDataHook", "stepPopulatingRegistryBar", "()V", false);

                            final Label l1 = new Label();
                            parent.visitLabel(l1);
                            parent.visitLineNumber(8 * 100 + 3 * 10 + 6, l1);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
                            parent.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/registries/RegistryManager", "registries", "Lcom/google/common/collect/BiMap;");
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/google/common/collect/BiMap", "keySet", "()Ljava/util/Set;", true);
                            parent.visitMethodInsn(Opcodes.INVOKESTATIC, "com/google/common/collect/Lists", "newArrayList", "(Ljava/lang/Iterable;)Ljava/util/ArrayList;", false);
                            parent.visitVarInsn(Opcodes.ASTORE, 1);

                            final Label l2 = new Label();
                            parent.visitLabel(l2);
                            parent.visitLineNumber(8 * 100 + 3 * 10 + 7, l2);
                            parent.visitLdcInsn("RegistryEvent$Register");
                            parent.visitVarInsn(Opcodes.ALOAD, 1);
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I", true);
                            parent.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/ProgressManager", "push", "(Ljava/lang/String;I)Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;", false);
                            parent.visitVarInsn(Opcodes.ASTORE, 2);

                            final Label l3 = new Label();
                            parent.visitLabel(l3);
                            parent.visitLineNumber(8 * 100 + 3 * 10 + 8, l3);
                            parent.visitVarInsn(Opcodes.ALOAD, 1);
                            parent.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/hook/GameDataHook", "getSortingComparator", "()Ljava/util/Comparator;", false);
                            parent.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Collections", "sort", "(Ljava/util/List;Ljava/util/Comparator;)V", false);

                            final Label l4 = new Label();
                            parent.visitLabel(l4);
                            parent.visitLineNumber(8 * 100 + 4 * 10 + 4, l4);
                            parent.visitVarInsn(Opcodes.ALOAD, 2);
                            parent.visitLdcInsn("minecraft:blocks");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

                            final Label l5 = new Label();
                            final Label l6 = new Label();
                            parent.visitLabel(l5);
                            parent.visitLineNumber(8 * 100 + 4 * 10 + 5, l5);
                            parent.visitVarInsn(Opcodes.ALOAD, 0);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Predicate", "test", "(Ljava/lang/Object;)Z", true);
                            parent.visitJumpInsn(Opcodes.IFEQ, l6);

                            final Label l7 = new Label();
                            parent.visitLabel(l7);
                            parent.visitLineNumber(8 * 100 + 4 * 10 + 7, l7);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/RegistryManager", "getRegistry", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;", false);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ForgeRegistry", "getRegisterEvent", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;", false);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
                            parent.visitInsn(Opcodes.POP);

                            final Label l8 = new Label();
                            parent.visitLabel(l8);
                            parent.visitLineNumber(8 * 100 + 4 * 10 + 8, l8);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/ObjectHolderRegistry", "INSTANCE", "Lnet/minecraftforge/registries/ObjectHolderRegistry;");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ObjectHolderRegistry", "applyObjectHolders", "()V", false);

                            parent.visitLabel(l6);
                            parent.visitLineNumber(8 * 100 + 4 * 10 + 9, l6);
                            parent.visitFrame(Opcodes.F_APPEND, 2, new Object[] { "java/util/List", "net/minecraftforge/fml/common/ProgressManager$ProgressBar" }, 0, null);
                            parent.visitVarInsn(Opcodes.ALOAD, 2);
                            parent.visitLdcInsn("minecraft:items");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

                            final Label l9 = new Label();
                            final Label l10 = new Label();
                            parent.visitLabel(l9);
                            parent.visitLineNumber(8 * 100 + 5 * 10, l9);
                            parent.visitVarInsn(Opcodes.ALOAD, 0);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Predicate", "test", "(Ljava/lang/Object;)Z", true);
                            parent.visitJumpInsn(Opcodes.IFEQ, l10);

                            final Label l11 = new Label();
                            parent.visitLabel(l11);
                            parent.visitLineNumber(8 * 100 + 5 * 10 + 2, l11);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/RegistryManager", "getRegistry", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;", false);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ForgeRegistry", "getRegisterEvent", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;", false);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
                            parent.visitInsn(Opcodes.POP);

                            final Label l12 = new Label();
                            parent.visitLabel(l12);
                            parent.visitLineNumber(8 * 100 + 5 * 10 + 3, l12);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/ObjectHolderRegistry", "INSTANCE", "Lnet/minecraftforge/registries/ObjectHolderRegistry;");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ObjectHolderRegistry", "applyObjectHolders", "()V", false);

                            parent.visitLabel(l10);
                            parent.visitLineNumber(8 * 100 + 5 * 10 + 5, l10);
                            parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            parent.visitVarInsn(Opcodes.ALOAD, 1);
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
                            parent.visitVarInsn(Opcodes.ASTORE, 3);

                            final Label l13 = new Label();
                            final Label l14 = new Label();
                            parent.visitLabel(l13);
                            parent.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/Iterator" }, 0, null);
                            parent.visitVarInsn(Opcodes.ALOAD, 3);
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                            parent.visitJumpInsn(Opcodes.IFEQ, l14);
                            parent.visitVarInsn(Opcodes.ALOAD, 3);
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
                            parent.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/util/ResourceLocation");
                            parent.visitVarInsn(Opcodes.ASTORE, 4);

                            final Label l15 = new Label();
                            final Label l16 = new Label();
                            parent.visitLabel(l15);
                            parent.visitLineNumber(8 * 100 + 5 * 10 + 7, l15);
                            parent.visitVarInsn(Opcodes.ALOAD, 0);
                            parent.visitVarInsn(Opcodes.ALOAD, 4);
                            parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Predicate", "test", "(Ljava/lang/Object;)Z", true);
                            parent.visitJumpInsn(Opcodes.IFNE, l16);
                            parent.visitJumpInsn(Opcodes.GOTO, l13);

                            final Label l17 = new Label();
                            parent.visitLabel(l16);
                            parent.visitLineNumber(8 * 100 + 5 * 10 + 8, l16);
                            parent.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "net/minecraft/util/ResourceLocation" }, 0, null);
                            parent.visitVarInsn(Opcodes.ALOAD, 4);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitJumpInsn(Opcodes.IF_ACMPEQ, l13);
                            parent.visitVarInsn(Opcodes.ALOAD, 4);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
                            parent.visitJumpInsn(Opcodes.IF_ACMPNE, l17);
                            parent.visitJumpInsn(Opcodes.GOTO, l13);

                            parent.visitLabel(l17);
                            parent.visitLineNumber(8 * 100 + 5 * 10 + 9, l17);
                            parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            parent.visitVarInsn(Opcodes.ALOAD, 2);
                            parent.visitVarInsn(Opcodes.ALOAD, 4);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

                            final Label l18 = new Label();
                            parent.visitLabel(l18);
                            parent.visitLineNumber(8 * 100 + 6 * 10, l18);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
                            parent.visitVarInsn(Opcodes.ALOAD, 4);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/RegistryManager", "getRegistry", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;", false);
                            parent.visitVarInsn(Opcodes.ALOAD, 4);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ForgeRegistry", "getRegisterEvent", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;", false);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
                            parent.visitInsn(Opcodes.POP);

                            final Label l19 = new Label();
                            parent.visitLabel(l19);
                            parent.visitJumpInsn(Opcodes.GOTO, l13);

                            parent.visitLabel(l14);
                            parent.visitLineNumber(8 * 100 + 6 * 10 + 2, l14);
                            parent.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
                            parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/ObjectHolderRegistry", "INSTANCE", "Lnet/minecraftforge/registries/ObjectHolderRegistry;");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ObjectHolderRegistry", "applyObjectHolders", "()V", false);

                            final Label l20 = new Label();
                            final Label l21 = new Label();
                            parent.visitLabel(l20);
                            parent.visitLineNumber(8 * 100 + 6 * 10 + 3, l20);
                            parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            parent.visitVarInsn(Opcodes.ALOAD, 2);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "getStep", "()I", false);
                            parent.visitVarInsn(Opcodes.ALOAD, 2);
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "getSteps", "()I", false);
                            parent.visitJumpInsn(Opcodes.IF_ICMPGE, l21);

                            final Label l22 = new Label();
                            parent.visitLabel(l22);
                            parent.visitLineNumber(8 * 100 + 6 * 10 + 3, l22);
                            parent.visitVarInsn(Opcodes.ALOAD, 2);
                            parent.visitLdcInsn("forge:unknown");
                            parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);
                            parent.visitJumpInsn(Opcodes.GOTO, l20);

                            parent.visitLabel(l21);
                            parent.visitLineNumber(8 * 100 + 6 * 10 + 5, l21);
                            parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            parent.visitVarInsn(Opcodes.ALOAD, 2);
                            parent.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/ProgressManager", "pop", "(Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;)V", false);

                            final Label l23 = new Label();
                            parent.visitLabel(l23);
                            parent.visitLineNumber(8 * 100 + 6 * 10 + 6, l23);
                            parent.visitInsn(Opcodes.RETURN);

                            final Label l24 = new Label();
                            parent.visitLabel(l24);
                            parent.visitLocalVariable("boson$r", "Lnet/minecraft/util/ResourceLocation;", null, l13, l17, 4);
                            parent.visitLocalVariable("boson$p", "Ljava/util/funcion/Predicate;", "Ljava/util/function/Predicate<Lnet/minecraft/util/ResourceLocation;>;", l0, l24, 0);
                            parent.visitLocalVariable("boson$l", "Ljava/util/List;", "Ljava/util/List<Lnet/minecraft/util/ResourceLocation;>;", l2, l24, 1);
                            parent.visitLocalVariable("boson$p", "Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;", null, l3, l24, 2);

                            parent.visitMaxs(4, 5);
                            parent.visitEnd();
                        }
                    };
                }
        );
    }
}
