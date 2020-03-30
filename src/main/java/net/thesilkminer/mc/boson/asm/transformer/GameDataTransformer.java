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

import net.thesilkminer.mc.boson.asm.utility.Log;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public final class GameDataTransformer extends AbstractTransformer {
    private static final class FireCreateRegistryEventsMethodVisitor extends MethodVisitor {
        private FireCreateRegistryEventsMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitInsn(final int opcode) {
            if (opcode != Opcodes.RETURN) {
                super.visitInsn(opcode);
                return;
            }

            LOGGER.i("Found RETURN instruction for method 'fireCreateRegistryEvents': injecting progress bar code now");

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(8 * 100 + 2 * 10 + 6, l0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, SHOW_PRE_INITIALIZATION_CREATION_BAR, "()V", false);

            super.visitInsn(opcode);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class FireRegistryEventsMethodVisitor extends MethodVisitor {
        // NOTE: The given bytecode represents the end state of the method: this method is being overwritten
        // by Boson, but this process shouldn't cause any issues (hopefully)

        //  // access flags 0x9
        //  // signature (Ljava/util/function/Predicate<Lnet/minecraft/util/ResourceLocation;>;)V
        //  // declaration: void fireRegistryEvents(java.util.function.Predicate<net.minecraft.util.ResourceLocation>)
        //  public static fireRegistryEvents(Ljava/util/function/Predicate;)V
        //   L0
        //    LINENUMBER 835 L0
        //    INVOKESTATIC net/minecraftforge/registries/GameData.<fermion-inject:stepPopulatingRegistryBar> ()V
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
        //    INVOKEDYNAMIC compare()Ljava/util/Comparator; [
        //      // handle kind 0x6 : INVOKESTATIC
        //      java/lang/invoke/LambdaMetafactory.metafactory(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
        //      // arguments:
        //      (Ljava/lang/Object;Ljava/lang/Object;)I,
        //      // handle kind 0x6 : INVOKESTATIC
        //      net/minecraftforge/registries/GameData.lambda$fireRegistryEvents$31(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/util/ResourceLocation;)I,
        //      (Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/util/ResourceLocation;)I
        //    ]
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

        private final MethodVisitor parent;

        private FireRegistryEventsMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, null);
            this.parent = parent;
        }

        @Override
        public void visitEnd() {
            LOGGER.i("Overwriting method with our own implementation");
            LOGGER.i("   Why aren't we injecting? Because to use frames we'd have to intercept every Label and... no");

            this.parent.visitCode();

            final Label l0 = new Label();
            this.parent.visitLabel(l0);
            this.parent.visitLineNumber(8 * 100 + 3 * 10 + 5, l0);
            this.parent.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, STEP_POPULATING_REGISTRY_BAR, "()V", false);

            final Label l1 = new Label();
            this.parent.visitLabel(l1);
            this.parent.visitLineNumber(8 * 100 + 3 * 10 + 6, l1);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
            this.parent.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/registries/RegistryManager", "registries", "Lcom/google/common/collect/BiMap;");
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/google/common/collect/BiMap", "keySet", "()Ljava/util/Set;", true);
            this.parent.visitMethodInsn(Opcodes.INVOKESTATIC, "com/google/common/collect/Lists", "newArrayList", "(Ljava/lang/Iterable;)Ljava/util/ArrayList;", false);
            this.parent.visitVarInsn(Opcodes.ASTORE, 1);

            final Label l2 = new Label();
            this.parent.visitLabel(l2);
            this.parent.visitLineNumber(8 * 100 + 3 * 10 + 7, l2);
            this.parent.visitLdcInsn("RegistryEvent$Register");
            this.parent.visitVarInsn(Opcodes.ALOAD, 1);
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I", true);
            this.parent.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/ProgressManager", "push", "(Ljava/lang/String;I)Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;", false);
            this.parent.visitVarInsn(Opcodes.ASTORE, 2);

            final Label l3 = new Label();
            this.parent.visitLabel(l3);
            this.parent.visitLineNumber(8 * 100 + 3 * 10 + 8, l3);
            this.parent.visitVarInsn(Opcodes.ALOAD, 1);
            this.parent.visitInvokeDynamicInsn("compare", "()Ljava/util/Comparator;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false), Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)I"), new Handle(Opcodes.H_INVOKESTATIC, "net/minecraftforge/registries/GameData", "lambda$fireRegistryEvents$31", "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/util/ResourceLocation;)I", false), Type.getType("(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/util/ResourceLocation;)I"));
            this.parent.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Collections", "sort", "(Ljava/util/List;Ljava/util/Comparator;)V", false);

            final Label l4 = new Label();
            this.parent.visitLabel(l4);
            this.parent.visitLineNumber(8 * 100 + 4 * 10 + 4, l4);
            this.parent.visitVarInsn(Opcodes.ALOAD, 2);
            this.parent.visitLdcInsn("minecraft:blocks");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

            final Label l5 = new Label();
            final Label l6 = new Label();
            this.parent.visitLabel(l5);
            this.parent.visitLineNumber(8 * 100 + 4 * 10 + 5, l5);
            this.parent.visitVarInsn(Opcodes.ALOAD, 0);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Predicate", "test", "(Ljava/lang/Object;)Z", true);
            this.parent.visitJumpInsn(Opcodes.IFEQ, l6);

            final Label l7 = new Label();
            this.parent.visitLabel(l7);
            this.parent.visitLineNumber(8 * 100 + 4 * 10 + 7, l7);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/RegistryManager", "getRegistry", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;", false);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ForgeRegistry", "getRegisterEvent", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;", false);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
            this.parent.visitInsn(Opcodes.POP);

            final Label l8 = new Label();
            this.parent.visitLabel(l8);
            this.parent.visitLineNumber(8 * 100 + 4 * 10 + 8, l8);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/ObjectHolderRegistry", "INSTANCE", "Lnet/minecraftforge/registries/ObjectHolderRegistry;");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ObjectHolderRegistry", "applyObjectHolders", "()V", false);

            this.parent.visitLabel(l6);
            this.parent.visitLineNumber(8 * 100 + 4 * 10 + 9, l6);
            this.parent.visitFrame(Opcodes.F_APPEND, 2, new Object[] { "java/util/List", "net/minecraftforge/fml/common/ProgressManager$ProgressBar" }, 0, null);
            this.parent.visitVarInsn(Opcodes.ALOAD, 2);
            this.parent.visitLdcInsn("minecraft:items");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

            final Label l9 = new Label();
            final Label l10 = new Label();
            this.parent.visitLabel(l9);
            this.parent.visitLineNumber(8 * 100 + 5 * 10, l9);
            this.parent.visitVarInsn(Opcodes.ALOAD, 0);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Predicate", "test", "(Ljava/lang/Object;)Z", true);
            this.parent.visitJumpInsn(Opcodes.IFEQ, l10);

            final Label l11 = new Label();
            this.parent.visitLabel(l11);
            this.parent.visitLineNumber(8 * 100 + 5 * 10 + 2, l11);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/RegistryManager", "getRegistry", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;", false);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ForgeRegistry", "getRegisterEvent", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;", false);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
            this.parent.visitInsn(Opcodes.POP);

            final Label l12 = new Label();
            this.parent.visitLabel(l12);
            this.parent.visitLineNumber(8 * 100 + 5 * 10 + 3, l12);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/ObjectHolderRegistry", "INSTANCE", "Lnet/minecraftforge/registries/ObjectHolderRegistry;");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ObjectHolderRegistry", "applyObjectHolders", "()V", false);

            this.parent.visitLabel(l10);
            this.parent.visitLineNumber(8 * 100 + 5 * 10 + 5, l10);
            this.parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            this.parent.visitVarInsn(Opcodes.ALOAD, 1);
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            this.parent.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l13 = new Label();
            final Label l14 = new Label();
            this.parent.visitLabel(l13);
            this.parent.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/Iterator" }, 0, null);
            this.parent.visitVarInsn(Opcodes.ALOAD, 3);
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            this.parent.visitJumpInsn(Opcodes.IFEQ, l14);
            this.parent.visitVarInsn(Opcodes.ALOAD, 3);
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            this.parent.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/util/ResourceLocation");
            this.parent.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l15 = new Label();
            final Label l16 = new Label();
            this.parent.visitLabel(l15);
            this.parent.visitLineNumber(8 * 100 + 5 * 10 + 7, l15);
            this.parent.visitVarInsn(Opcodes.ALOAD, 0);
            this.parent.visitVarInsn(Opcodes.ALOAD, 4);
            this.parent.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/Predicate", "test", "(Ljava/lang/Object;)Z", true);
            this.parent.visitJumpInsn(Opcodes.IFNE, l16);
            this.parent.visitJumpInsn(Opcodes.GOTO, l13);

            final Label l17 = new Label();
            this.parent.visitLabel(l16);
            this.parent.visitLineNumber(8 * 100 + 5 * 10 + 8, l16);
            this.parent.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "net/minecraft/util/ResourceLocation" }, 0, null);
            this.parent.visitVarInsn(Opcodes.ALOAD, 4);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "BLOCKS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitJumpInsn(Opcodes.IF_ACMPEQ, l13);
            this.parent.visitVarInsn(Opcodes.ALOAD, 4);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/GameData", "ITEMS", "Lnet/minecraft/util/ResourceLocation;");
            this.parent.visitJumpInsn(Opcodes.IF_ACMPNE, l17);
            this.parent.visitJumpInsn(Opcodes.GOTO, l13);

            this.parent.visitLabel(l17);
            this.parent.visitLineNumber(8 * 100 + 5 * 10 + 9, l17);
            this.parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            this.parent.visitVarInsn(Opcodes.ALOAD, 2);
            this.parent.visitVarInsn(Opcodes.ALOAD, 4);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/util/ResourceLocation", "toString", "()Ljava/lang/String;", false);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

            final Label l18 = new Label();
            this.parent.visitLabel(l18);
            this.parent.visitLineNumber(8 * 100 + 6 * 10, l18);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/RegistryManager", "ACTIVE", "Lnet/minecraftforge/registries/RegistryManager;");
            this.parent.visitVarInsn(Opcodes.ALOAD, 4);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/RegistryManager", "getRegistry", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/registries/ForgeRegistry;", false);
            this.parent.visitVarInsn(Opcodes.ALOAD, 4);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ForgeRegistry", "getRegisterEvent", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/event/RegistryEvent$Register;", false);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
            this.parent.visitInsn(Opcodes.POP);

            final Label l19 = new Label();
            this.parent.visitLabel(l19);
            this.parent.visitJumpInsn(Opcodes.GOTO, l13);

            this.parent.visitLabel(l14);
            this.parent.visitLineNumber(8 * 100 + 6 * 10 + 2, l14);
            this.parent.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
            this.parent.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/registries/ObjectHolderRegistry", "INSTANCE", "Lnet/minecraftforge/registries/ObjectHolderRegistry;");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/registries/ObjectHolderRegistry", "applyObjectHolders", "()V", false);

            final Label l20 = new Label();
            final Label l21 = new Label();
            this.parent.visitLabel(l20);
            this.parent.visitLineNumber(8 * 100 + 6 * 10 + 3, l20);
            this.parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            this.parent.visitVarInsn(Opcodes.ALOAD, 2);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "getStep", "()I", false);
            this.parent.visitVarInsn(Opcodes.ALOAD, 2);
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "getSteps", "()I", false);
            this.parent.visitJumpInsn(Opcodes.IF_ICMPGE, l21);

            final Label l22 = new Label();
            this.parent.visitLabel(l22);
            this.parent.visitLineNumber(8 * 100 + 6 * 10 + 3, l22);
            this.parent.visitVarInsn(Opcodes.ALOAD, 2);
            this.parent.visitLdcInsn("forge:unknown");
            this.parent.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);
            this.parent.visitJumpInsn(Opcodes.GOTO, l20);

            this.parent.visitLabel(l21);
            this.parent.visitLineNumber(8 * 100 + 6 * 10 + 5, l21);
            this.parent.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            this.parent.visitVarInsn(Opcodes.ALOAD, 2);
            this.parent.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/ProgressManager", "pop", "(Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;)V", false);

            final Label l23 = new Label();
            this.parent.visitLabel(l23);
            this.parent.visitLineNumber(8 * 100 + 6 * 10 + 6, l23);
            this.parent.visitInsn(Opcodes.RETURN);

            final Label l24 = new Label();
            this.parent.visitLabel(l24);
            this.parent.visitLocalVariable("boson$r", "Lnet/minecraft/util/ResourceLocation;", null, l13, l17, 4);
            this.parent.visitLocalVariable("boson$p", "Ljava/util/funcion/Predicate;", "Ljava/util/function/Predicate<Lnet/minecraft/util/ResourceLocation;>;", l0, l24, 0);
            this.parent.visitLocalVariable("boson$l", "Ljava/util/List;", "Ljava/util/List<Lnet/minecraft/util/ResourceLocation;>;", l2, l24, 1);
            this.parent.visitLocalVariable("boson$p", "Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;", null, l3, l24, 2);

            this.parent.visitMaxs(4, 5);
            this.parent.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class ReflectProgressBarFromLoaderMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private final static reflectProgressBarFromLoader()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;
        //    TRYCATCHBLOCK L0 L1 L2 java/lang/ReflectiveOperationException
        //   L0
        //    LINENUMBER 937 L0
        //    NOP
        //   L3
        //    LINENUMBER 938 L3
        //    LDC Lnet/minecraftforge/fml/common/Loader;.class
        //    LDC "progressBar"
        //    INVOKEVIRTUAL java/lang/Class.getDeclaredField (Ljava/lang/String;)Ljava/lang/reflect/Field;
        //    ASTORE 0
        //   L4
        //    LINENUMBER 939 L4
        //    ALOAD 0
        //    ICONST_1
        //    INVOKEVIRTUAL java/lang/reflect/Field.setAccessible (Z)V
        //   L5
        //    LINENUMBER 940 L5
        //    ALOAD 0
        //    INVOKESTATIC net/minecraftforge/fml/common/Loader.instance ()Lnet/minecraftforge/fml/common/Loader;
        //    INVOKEVIRTUAL java/lang/reflect/Field.get (Ljava/lang/Object;)Ljava/lang/Object;
        //    CHECKCAST net/minecraftforge/fml/common/ProgressManager$ProgressBar
        //   L1
        //    ARETURN
        //   L2
        //    LINENUMBER 941 L2
        //   FRAME FULL [] [java/lang/ReflectiveOperationException]
        //    ASTORE 0
        //   L6
        //    LINENUMBER 942 L6
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
            super.visitLineNumber(9 * 100 + 3 * 10 + 7, l0);
            super.visitInsn(Opcodes.NOP);

            final Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(9 * 100 + 3 * 10 + 8, l3);
            super.visitLdcInsn(Type.getType("Lnet/minecraftforge/fml/common/Loader;"));
            super.visitLdcInsn("progressBar");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            super.visitVarInsn(Opcodes.ASTORE, 0);

            final Label l4 = new Label();
            super.visitLabel(l4);
            super.visitLineNumber(9 * 100 + 3 * 10 + 9, l4);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitInsn(Opcodes.ICONST_1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);

            final Label l5 = new Label();
            super.visitLabel(l5);
            super.visitLineNumber(9 * 100 + 4 * 10, l5);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraftforge/fml/common/Loader", "instance", "()Lnet/minecraftforge/fml/common/Loader;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraftforge/fml/common/ProgressManager$ProgressBar");

            super.visitLabel(l1);
            super.visitInsn(Opcodes.ARETURN);

            super.visitLabel(l2);
            super.visitLineNumber(9 * 100 + 4 * 10 + 1, l2);
            super.visitFrame(Opcodes.F_FULL, 0, new Object[] {}, 1, new Object[] { "java/lang/ReflectiveOperationException" });
            super.visitVarInsn(Opcodes.ASTORE, 0);

            final Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLineNumber(9 * 100 + 4 * 10 + 2, l6);
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
    private static final class ShowPreInitializationCreationBarMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private final static showPreInitializationCreationBar()V
        //   L0
        //    LINENUMBER 948 L0
        //    INVOKESTATIC net/minecraftforge/registries/GameData.<fermion-inject:reflectProgressBarFromLoader> ()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;
        //    LDC "$Boson$marker$UsePreInit"
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
        //   L1
        //    LINENUMBER 949 L1
        //    RETURN
        //   L2
        //    MAXSTACK = 2
        //    MAXLOCALS = 0

        private ShowPreInitializationCreationBarMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(9 * 100 + 4 * 10 + 8, l0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, REFLECT_PROGRESS_BAR_FROM_LOADER, "()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;", false);
            super.visitLdcInsn("$Boson$marker$UsePreInit");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

            final Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(9 * 100 + 4 * 10 + 9, l1);
            super.visitInsn(Opcodes.RETURN);

            final Label l2 = new Label();
            super.visitLabel(l2);

            super.visitMaxs(2, 0);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class StepPopulatingRegistryBarMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private final static stepPopulatingRegistryBar()V
        //   L0
        //    LINENUMBER 953 L0
        //    INVOKESTATIC net/minecraftforge/registries/GameData.<fermion-inject:reflectProgressBarFromLoader> ()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;
        //    LDC "Populating Registries"
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/ProgressManager$ProgressBar.step (Ljava/lang/String;)V
        //   L1
        //    LINENUMBER 954 L1
        //    RETURN
        //   L2
        //    MAXSTACK = 2
        //    MAXLOCALS = 0

        private StepPopulatingRegistryBarMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(9 * 100 + 5 * 10 + 3, l0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, REFLECT_PROGRESS_BAR_FROM_LOADER, "()Lnet/minecraftforge/fml/common/ProgressManager$ProgressBar;", false);
            super.visitLdcInsn("Populating Registries");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/ProgressManager$ProgressBar", "step", "(Ljava/lang/String;)V", false);

            final Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(9 * 100 + 5 * 10 + 4, l1);
            super.visitInsn(Opcodes.RETURN);

            final Label l2 = new Label();
            super.visitLabel(l2);

            super.visitMaxs(2, 0);
            super.visitEnd();
        }
    }

    private static final Log LOGGER = Log.of("Game Data");

    private static final String THIS = "net/minecraftforge/registries/GameData";
    private static final String REFLECT_PROGRESS_BAR_FROM_LOADER = "fermion$$injected$$reflectProgressBarFromLoader$$generated$$00_23_1122";
    private static final String SHOW_PRE_INITIALIZATION_CREATION_BAR = "fermion$$injected$$showPreInitializationCreationBar$$generated$$00_23_1122";
    private static final String STEP_POPULATING_REGISTRY_BAR = "fermion$$injected$$stepPopulatingRegistryBar$$generated$$00_23_1122";

    public GameDataTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("game_data")
                        .setDescription("Transforms GameData so that the fired events are shown on the ProgressBar")
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
                if ("fireCreateRegistryEvents".equals(name) && "()V".equals(desc)) {
                    return new FireCreateRegistryEventsMethodVisitor(v, parent);
                }
                if ("fireRegistryEvents".equals(name) && "(Ljava/util/function/Predicate;)V".equals(desc)) {
                    return new FireRegistryEventsMethodVisitor(v, parent);
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

                new ShowPreInitializationCreationBarMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                SHOW_PRE_INITIALIZATION_CREATION_BAR,
                                "()V",
                                null,
                                null
                        )
                ).visitCode();

                new StepPopulatingRegistryBarMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                STEP_POPULATING_REGISTRY_BAR,
                                "()V",
                                null,
                                null
                        )
                ).visitCode();

                super.visitEnd();
            }
        };
    }
}
