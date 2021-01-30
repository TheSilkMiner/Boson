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

import com.google.common.collect.ImmutableList;
import net.thesilkminer.mc.boson.asm.utility.Log;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public final class ObjectHolderRegistryTransformer extends SingleTargetMethodTransformer {

    private static final Log L = Log.of("Object Holder Registry");

    public ObjectHolderRegistryTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("object_holder_registry")
                        .setDescription("Transforms the ObjectHolderRegistry so that it fires an event whenever an Object Holder gets populated")
                        .build(),
                ClassDescriptor.of("net.minecraftforge.registries.ObjectHolderRegistry"),
                MethodDescriptor.of("applyObjectHolders", ImmutableList.of(), ClassDescriptor.of(void.class))
        );
    }

    @Nonnull
    @Override
    @SuppressWarnings("SpellCheckingInspection")
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            //  // access flags 0x1
            //  public applyObjectHolders()V
            //   L0
            //    LINENUMBER 167 L0
            //    GETSTATIC net/minecraftforge/fml/common/FMLLog.log : Lorg/apache/logging/log4j/Logger;
            //    LDC "Applying holder lookups"
            //    INVOKEINTERFACE org/apache/logging/log4j/Logger.info (Ljava/lang/String;)V (itf)
            //   L1
            //    LINENUMBER 168 L1
            //    ALOAD 0
            //    GETFIELD net/minecraftforge/registries/ObjectHolderRegistry.objectHolders : Ljava/util/List;
            //    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator; (itf)
            //    ASTORE 1
            //   L2
            //   FRAME APPEND [java/util/Iterator]
            //    ALOAD 1
            //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
            //    IFEQ L3
            //    ALOAD 1
            //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
            //    CHECKCAST net/minecraftforge/registries/ObjectHolderRef
            //    ASTORE 2
            //   L4
            //    LINENUMBER 170 L4
            //    ALOAD 2
            //    INVOKEVIRTUAL net/minecraftforge/registries/ObjectHolderRef.apply ()V
            //   L5
            //    LINENUMBER 171 L5
            //    GOTO L2
            //   L3
            //    LINENUMBER 172 L3
            //   FRAME CHOP 1
            // <<< INJECTION START
            //    GETSTATIC net/minecraftforge/fml/common/FMLLog.log : Lorg/apache/logging/log4j/Logger;
            //    LDC "Firing ObjectHoldersApplied Event"
            //    INVOKEINTERFACE org/apache/logging/log4j/Logger.info (Ljava/lang/String;)V (itf)
            //   L800
            //    LINENUMBER 800 L800
            //    GETSTATIC net/minecraftforge/common/MinecraftForge.EVENT_BUS : Lnet/minecraftforge/fml/common/eventhandler/EventBus;
            //    NEW net/thesilkminer/mc/boson/api/event/ObjectHoldersAppliedEvent
            //    DUP
            //    INVOKESPECIAL net/thesilkminer/mc/boson/api/event/ObjectHoldersAppliedEvent.<init> ()V;
            //    INVOKEVIRTUAL net/minecraftforge/fml/common/eventhandler/EventBus.post (Lnet/minecraftforge/fml/common/eventhandler/Event;)Z
            //    POP
            //   L801
            //    LINENUMBER 172 L801
            // >>> INJECTION END
            //    GETSTATIC net/minecraftforge/fml/common/FMLLog.log : Lorg/apache/logging/log4j/Logger;
            //    LDC "Holder lookups applied"
            //    INVOKEINTERFACE org/apache/logging/log4j/Logger.info (Ljava/lang/String;)V (itf)
            //   L6
            //    LINENUMBER 173 L6
            //    RETURN
            //   L7
            //    LOCALVARIABLE ohr Lnet/minecraftforge/registries/ObjectHolderRef; L4 L5 2
            //    LOCALVARIABLE this Lnet/minecraftforge/registries/ObjectHolderRegistry; L0 L7 0
            // <<< OVERWRITE BEGIN
            //    MAXSTACK = 2
            // === OVERWRITE WITH
            //    MAXSTACK = 3
            // >>> OVERWRITE END
            //    MAXLOCALS = 3

            @Override
            public void visitFrame(final int type, final int nLocal, @Nullable final Object[] local, final int nStack, @Nullable final Object[] stack) {
                super.visitFrame(type, nLocal, local, nStack, stack);

                if (type == Opcodes.F_CHOP) {
                    L.i("Found 'FRAME CHOP 1': injecting now");

                    super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/fml/common/FMLLog", "log", "Lorg/apache/logging/log4j/Logger;");
                    super.visitLdcInsn("Firing ObjectHoldersApplied Event");
                    super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "info", "(Ljava/lang/String;)V", true);

                    final Label l800 = new Label();
                    super.visitLabel(l800);
                    super.visitLineNumber(8 * 100, l800);
                    super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
                    super.visitTypeInsn(Opcodes.NEW, "net/thesilkminer/mc/boson/api/event/ObjectHoldersAppliedEvent");
                    super.visitInsn(Opcodes.DUP);
                    super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/thesilkminer/mc/boson/api/event/ObjectHoldersAppliedEvent", "<init>", "()V", false);
                    super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
                    super.visitInsn(Opcodes.POP);

                    final Label l801 = new Label();
                    super.visitLabel(l801);
                    super.visitLineNumber(100 + 7 * 10 + 2, l801);

                    L.i("Injected event firing");
                }
            }

            @Override
            public void visitMaxs(final int maxStack, final int maxLocals) {
                L.i("Augmenting maxStack by 1");
                super.visitMaxs(maxStack + 1, maxLocals);
            }
        };
    }
}
