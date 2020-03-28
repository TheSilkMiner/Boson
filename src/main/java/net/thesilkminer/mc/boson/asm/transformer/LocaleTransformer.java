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
import net.thesilkminer.mc.fermion.asm.api.MappingUtilities;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public final class LocaleTransformer extends SingleTargetMethodTransformer {

    public LocaleTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("locale")
                        .setDescription("Edits the Locale class so that it supports loading of JSON lang files")
                        .build(),
                ClassDescriptor.of("net.minecraft.client.resources.Locale"),
                MethodDescriptor.of(
                        "func_135022_a",
                        ImmutableList.of(
                                ClassDescriptor.of("net.minecraft.client.resources.IResourceManager"),
                                ClassDescriptor.of("java.util.List")
                        ),
                        ClassDescriptor.of(void.class)
                )
        );
    }

    @Nonnull
    @Override
    @SuppressWarnings("SpellCheckingInspection")
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            //  // access flags 0x21
            //  // signature (Lnet/minecraft/client/resources/IResourceManager;Ljava/util/List<Ljava/lang/String;>;)V
            //  // declaration: void loadLocaleDataFiles(net.minecraft.client.resources.IResourceManager, java.util.List<java.lang.String>)
            //  public synchronized loadLocaleDataFiles(Lnet/minecraft/client/resources/IResourceManager;Ljava/util/List;)V
            //    TRYCATCHBLOCK L0 L1 L2 java/io/IOException
            //   L3
            //    LINENUMBER 32 L3
            //    ALOAD 0
            //    GETFIELD net/minecraft/client/resources/Locale.properties : Ljava/util/Map;
            //    INVOKEINTERFACE java/util/Map.clear ()V (itf)
            //   L4
            //    LINENUMBER 34 L4
            //    ALOAD 2
            //    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator; (itf)
            //    ASTORE 3
            //   L5
            //   FRAME APPEND [java/util/Iterator]
            //    ALOAD 3
            //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
            //    IFEQ L6
            //    ALOAD 3
            //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
            //    CHECKCAST java/lang/String
            //    ASTORE 4
            //   L7
            //    LINENUMBER 36 L7
            //    LDC "lang/%s.lang"
            //    ICONST_1
            //    ANEWARRAY java/lang/Object
            //    DUP
            //    ICONST_0
            //    ALOAD 4
            //    AASTORE
            //    INVOKESTATIC java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
            //    ASTORE 5
            //   L8
            //    LINENUMBER 38 L8
            //    ALOAD 1
            //    INVOKEINTERFACE net/minecraft/client/resources/IResourceManager.getResourceDomains ()Ljava/util/Set; (itf)
            //    INVOKEINTERFACE java/util/Set.iterator ()Ljava/util/Iterator; (itf)
            //    ASTORE 6
            //   L9
            //   FRAME APPEND [java/lang/String java/lang/String java/util/Iterator]
            //    ALOAD 6
            //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
            //    IFEQ L10
            //    ALOAD 6
            //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
            //    CHECKCAST java/lang/String
            //    ASTORE 7
            //   L0
            //    LINENUMBER 42 L0
            // <<< INJECTION BEGIN
            //    LDC "Really didn't want to hook but here we are"
            //    POP
            //    ALOAD 7
            //    ALOAD 4
            //    ALOAD 1
            //    ALOAD 0
            //    GETFIELD net/minecraft/client/resources/Locale.<fermion-remap:field_135032_a> : Ljava/util/Map;
            //    INVOKESTATIC net/thesilkminer/mc/boson/hook/LocaleHook.hookJsonLocale (Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V
            //   L800
            //    LINENUMBER 43 L800
            // >>> INJECTION END
            //    ALOAD 0
            //    ALOAD 1
            //    NEW net/minecraft/util/ResourceLocation
            //    DUP
            //    ALOAD 7
            //    ALOAD 5
            //    INVOKESPECIAL net/minecraft/util/ResourceLocation.<init> (Ljava/lang/String;Ljava/lang/String;)V
            //    INVOKEINTERFACE net/minecraft/client/resources/IResourceManager.getAllResources (Lnet/minecraft/util/ResourceLocation;)Ljava/util/List; (itf)
            //    INVOKESPECIAL net/minecraft/client/resources/Locale.loadLocaleData (Ljava/util/List;)V
            //   L1
            //    LINENUMBER 47 L1
            //    GOTO L11
            //   L2
            //    LINENUMBER 44 L2
            //   FRAME FULL [net/minecraft/client/resources/Locale net/minecraft/client/resources/IResourceManager java/util/List java/util/Iterator java/lang/String java/lang/String java/util/Iterator java/lang/String] [java/io/IOException]
            //    ASTORE 8
            //   L11
            //    LINENUMBER 48 L11
            //   FRAME CHOP 1
            //    GOTO L9
            //   L10
            //    LINENUMBER 49 L10
            //   FRAME CHOP 3
            //    GOTO L5
            //   L6
            //    LINENUMBER 51 L6
            //   FRAME CHOP 1
            //    ALOAD 0
            //    INVOKESPECIAL net/minecraft/client/resources/Locale.checkUnicode ()V
            //   L12
            //    LINENUMBER 52 L12
            //    RETURN
            //   L13
            //    LOCALVARIABLE s2 Ljava/lang/String; L0 L11 7
            //    LOCALVARIABLE s1 Ljava/lang/String; L8 L10 5
            //    LOCALVARIABLE s Ljava/lang/String; L7 L10 4
            //    LOCALVARIABLE this Lnet/minecraft/client/resources/Locale; L3 L13 0
            //    LOCALVARIABLE resourceManager Lnet/minecraft/client/resources/IResourceManager; L3 L13 1
            //    LOCALVARIABLE languageList Ljava/util/List; L3 L13 2
            //    // signature Ljava/util/List<Ljava/lang/String;>;
            //    // declaration: languageList extends java.util.List<java.lang.String>
            //    MAXSTACK = 6
            //    MAXLOCALS = 9

            private boolean hasAStoreSeven;
            private boolean hasPatched;

            @Override
            public void visitVarInsn(final int opcode, final int var) {
                super.visitVarInsn(opcode, var);
                if (!this.hasAStoreSeven && opcode == Opcodes.ASTORE && var == 7 && !this.hasPatched) {
                    this.hasAStoreSeven = true;
                }
            }

            @Override
            public void visitLineNumber(final int line, @Nonnull final Label start) {
                super.visitLineNumber(line, start);
                if (!this.hasPatched && this.hasAStoreSeven) {
                    this.hasAStoreSeven = false;
                    this.hasPatched = true;

                    super.visitLdcInsn("Really didn't want to hook but here we are");
                    super.visitInsn(Opcodes.POP);
                    super.visitVarInsn(Opcodes.ALOAD, 7);
                    super.visitVarInsn(Opcodes.ALOAD, 4);
                    super.visitVarInsn(Opcodes.ALOAD, 1);
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/resources/Locale",
                            MappingUtilities.INSTANCE.mapField("field_135032_a"), "Ljava/util/Map;");
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/hook/LocaleHook",
                            "hookJsonLocale", "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V", false);

                    final Label l800 = new Label();
                    super.visitLabel(l800);
                    super.visitLineNumber(4 * 10 + 3, l800);
                }
            }
        };
    }
}
