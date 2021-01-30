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

import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.MappingUtilities;
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

public final class LocaleTransformer extends AbstractTransformer {
    @SuppressWarnings("SpellCheckingInspection")
    private static final class HookInjectorMethodVisitor extends MethodVisitor {
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
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:hookJsonLocale> (Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V
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
        private HookInjectorMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

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
                super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, HOOK_JSON_LOCALE,
                        "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V", false);

                final Label l800 = new Label();
                super.visitLabel(l800);
                super.visitLineNumber(4 * 10 + 3, l800);
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class HookJsonLocaleMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  // signature (Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V
        //  // declaration: void hookJsonLocale(java.lang.String, java.lang.String, net.minecraft.client.resources.IResourceManager, java.util.Map<java.lang.String, java.lang.String>)
        //  private final static hookJsonLocale(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V
        //   L0
        //    LINENUMBER 153 L0
        //    ALOAD 0
        //    LDC "resourceDomain"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/lang/String
        //    ALOAD 1
        //    LDC "language"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/lang/String
        //    ALOAD 2
        //    LDC "resourceManager"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST net/minecraft/client/resources/IResourceManager
        //    ALOAD 3
        //    LDC "properties"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/util/Map
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:runHook> (Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V
        //    RETURN
        //   L1
        //    LOCALVARIABLE resourceDomain Ljava/lang/String; L0 L1 0
        //    LOCALVARIABLE language Ljava/lang/String; L0 L1 1
        //    LOCALVARIABLE resourceManager Lnet/minecraft/client/resources/IResourceManager; L0 L1 2
        //    LOCALVARIABLE properties Ljava/util/Map; L0 L1 3
        //    // signature Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: properties extends java.util.Map<java.lang.String, java.lang.String>
        //    MAXSTACK = 5
        //    MAXLOCALS = 4

        private HookJsonLocaleMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(100 + 5 * 10 + 3, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitLdcInsn("resourceDomain");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitLdcInsn("language");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitLdcInsn("resourceManager");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/client/resources/IResourceManager");
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitLdcInsn("properties");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, RUN_HOOK,
                    "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V",false);
            super.visitInsn(Opcodes.RETURN);

            final Label l1 = new Label();
            super.visitLabel(l1);

            super.visitLocalVariable("resourceDomain", "Ljava/lang/String;", null, l0, l1, 0);
            super.visitLocalVariable("language", "Ljava/lang/String;", null, l0, l1, 1);
            super.visitLocalVariable("resourceManager", "Lnet/minecraft/client/resources/IResourceManager;", null, l0, l1, 2);
            super.visitLocalVariable("properties", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l0, l1, 3);

            super.visitMaxs(5, 4);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class RunHookMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  // signature (Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V
        //  // declaration: void runHook(java.lang.String, java.lang.String, net.minecraft.client.resources.IResourceManager, java.util.Map<java.lang.String, java.lang.String>)
        //  private final static runHook(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V
        //   L0
        //    LINENUMBER 156 L0
        //    NEW net/minecraft/util/ResourceLocation
        //    DUP
        //    ALOAD 0
        //    NEW java/lang/StringBuilder
        //    DUP
        //    LDC "lang/"
        //    INVOKESPECIAL java/lang/StringBuilder.<init> (Ljava/lang/String;)V
        //    ALOAD 1
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    LDC ".json"
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
        //    INVOKESPECIAL net/minecraft/util/ResourceLocation.<init> (Ljava/lang/String;Ljava/lang/String;)V
        //    ALOAD 2
        //    ALOAD 3
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:loadAllJsonFilesForLanguage> (Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V
        //    RETURN
        //   L1
        //    LOCALVARIABLE resourceDomain Ljava/lang/String; L0 L1 0
        //    LOCALVARIABLE language Ljava/lang/String; L0 L1 1
        //    LOCALVARIABLE resourceManager Lnet/minecraft/client/resources/IResourceManager; L0 L1 2
        //    LOCALVARIABLE properties Ljava/util/Map; L0 L1 3
        //    // signature Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: properties extends java.util.Map<java.lang.String, java.lang.String>
        //    MAXSTACK = 6
        //    MAXLOCALS = 4

        private RunHookMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(100 + 5 * 10 + 6, l0);
            super.visitTypeInsn(Opcodes.NEW, "net/minecraft/util/ResourceLocation");
            super.visitInsn(Opcodes.DUP);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitLdcInsn("lang/");
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitLdcInsn(".json");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/util/ResourceLocation", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, LOAD_ALL_JSON_FILES_FOR_LANGUAGE,
                    "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V", false);
            super.visitInsn(Opcodes.RETURN);

            final Label l1 = new Label();
            super.visitLabel(l1);

            super.visitLocalVariable("resourceDomain", "Ljava/lang/String;", null, l0, l1, 0);
            super.visitLocalVariable("language", "Ljava/lang/String;", null, l0, l1, 1);
            super.visitLocalVariable("resourceManager", "Lnet/minecraft/client/resources/IResourceManager;", null, l0, l1, 2);
            super.visitLocalVariable("properties", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l0, l1, 3);

            super.visitMaxs(6, 4);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class LoadAllJsonFilesForLanguageMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  // signature (Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V
        //  // declaration: void loadAllJsonFilesForLanguage(net.minecraft.util.ResourceLocation, net.minecraft.client.resources.IResourceManager, java.util.Map<java.lang.String, java.lang.String>)
        //  private final static loadAllJsonFilesForLanguage(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V
        //   L0
        //    LINENUMBER 160 L0
        //    ALOAD 0
        //    ALOAD 1
        //    ALOAD 0
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:safelyGetAllResources> (Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/util/ResourceLocation;)Ljava/util/List;
        //    ALOAD 2
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:loadJsonLocaleFiles> (Lnet/minecraft/util/ResourceLocation;Ljava/util/List;Ljava/util/Map;)V
        //    RETURN
        //   L1
        //    LOCALVARIABLE resourceName Lnet/minecraft/util/ResourceLocation; L0 L1 0
        //    LOCALVARIABLE resourceManager Lnet/minecraft/client/resources/IResourceManager; L0 L1 1
        //    LOCALVARIABLE properties Ljava/util/Map; L0 L1 2
        //    // signature Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: properties extends java.util.Map<java.lang.String, java.lang.String>
        //    MAXSTACK = 3
        //    MAXLOCALS = 3

        private LoadAllJsonFilesForLanguageMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(100 + 6 * 10, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, SAFELY_GET_ALL_RESOURCES,
                    "(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/util/ResourceLocation;)Ljava/util/List;", false);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, LOAD_JSON_LOCALE_FILES, "(Lnet/minecraft/util/ResourceLocation;Ljava/util/List;Ljava/util/Map;)V", false);
            super.visitInsn(Opcodes.RETURN);

            final Label l1 = new Label();
            super.visitLabel(l1);

            super.visitLocalVariable("resourceName", "Lnet/minecraft/util/ResourceLocation;", null, l0, l1, 0);
            super.visitLocalVariable("resourceManager", "Lnet/minecraft/client/resources/IResourceManager;", null, l0, l1, 1);
            super.visitLocalVariable("properties", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l0, l1, 2);

            super.visitMaxs(3, 3);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class LoadJsonLocaleFilesMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  // signature (Lnet/minecraft/util/ResourceLocation;Ljava/util/List<+Lnet/minecraft/client/resources/IResource;>;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V
        //  // declaration: void loadJsonLocaleFiles(net.minecraft.util.ResourceLocation, java.util.List<? extends net.minecraft.client.resources.IResource>, java.util.Map<java.lang.String, java.lang.String>)
        //  private final static loadJsonLocaleFiles(Lnet/minecraft/util/ResourceLocation;Ljava/util/List;Ljava/util/Map;)V
        //   L0
        //    LINENUMBER 164 L0
        //    ALOAD 1
        //    CHECKCAST java/lang/Iterable
        //    ASTORE 3
        //   L1
        //    LINENUMBER 166 L1
        //    ALOAD 3
        //    INVOKEINTERFACE java/lang/Iterable.iterator ()Ljava/util/Iterator; (itf)
        //    ASTORE 4
        //   L2
        //   FRAME APPEND [java/lang/Iterable java/util/Iterator]
        //    ALOAD 4
        //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
        //    IFEQ L3
        //    ALOAD 4
        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
        //    ASTORE 5
        //   L4
        //    ALOAD 5
        //    CHECKCAST net/minecraft/client/resources/IResource
        //    ASTORE 6
        //   L5
        //    ALOAD 6
        //    ASTORE 7
        //   L6
        //    LINENUMBER 164 L6
        //    ALOAD 0
        //    ALOAD 7
        //    ALOAD 2
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:loadJsonLocale> (Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResource;Ljava/util/Map;)V
        //   L7
        //    NOP
        //   L8
        //    GOTO L2
        //   L3
        //    LINENUMBER 167 L3
        //   FRAME SAME
        //    NOP
        //   L9
        //    LINENUMBER 164 L9
        //    RETURN
        //   L10
        //    LOCALVARIABLE name Lnet/minecraft/util/ResourceLocation; L0 L10 0
        //    LOCALVARIABLE jsonList Ljava/util/List; L0 L10 1
        //    // signature Ljava/util/List<+Lnet/minecraft/client/resources/IResource;>;
        //    // declaration: jsonList extends java.util.List<? extends net.minecraft.client.resources.IResource>
        //    LOCALVARIABLE properties Ljava/util/Map; L0 L10 2
        //    // signature Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: properties extends java.util.Map<java.lang.String, java.lang.String>
        //    LOCALVARIABLE $forEach$this Ljava/lang/Iterable; L1 L10 3
        //    // signature Ljava/lang/Iterable<+Lnet/minecraft/client/resources/IResource;>;
        //    // declaration: $forEach$this extends java.lang.Iterable<? extends net.minecraft.client.resources.IResource>
        //    LOCALVARIABLE $forEach$$1 Ljava/util/Iterator; L2 L10 4
        //    // signature Ljava/util/Iterator<+Lnet/minecraft/client/resources/IResource;>;
        //    // declaration: $forEach$this extends java.util.Iterator<? extends net.minecraft.client.resources.IResource>
        //    LOCALVARIABLE $forEach$element Ljava/lang/Object; L4 L3 5
        //    LOCALVARIABLE $forEach$it Lnet/minecraft/client/resources/IResource; L5 L3 6
        //    LOCALVARIABLE it Lnet/minecraft/client/resources/IResource; L6 L3 7
        //    MAXSTACK = 3
        //    MAXLOCALS = 8

        private LoadJsonLocaleFilesMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(100 + 6 * 10 + 4, l0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Iterable");
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(100 + 6 * 10 + 6, l1);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/Iterable","iterator", "()Ljava/util/Iterator;", true);
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l2 = new Label();
            final Label l3 = new Label();
            super.visitLabel(l2);
            super.visitFrame(Opcodes.F_APPEND, 2, new Object[] { "java/lang/Iterable", "java/util/Iterator" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            super.visitJumpInsn(Opcodes.IFEQ, l3);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            super.visitVarInsn(Opcodes.ASTORE, 5);

            final Label l4 = new Label();
            super.visitLabel(l4);
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/client/resources/IResource");
            super.visitVarInsn(Opcodes.ASTORE, 6);

            final Label l5 = new Label();
            super.visitLabel(l5);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitVarInsn(Opcodes.ASTORE, 7);

            final Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLineNumber(100 + 6 * 10 + 4, l6);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, LOAD_JSON_LOCALE,
                    "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResource;Ljava/util/Map;)V", false);

            final Label l7 = new Label();
            super.visitLabel(l7);
            super.visitInsn(Opcodes.NOP);

            final Label l8 = new Label();
            super.visitLabel(l8);
            super.visitJumpInsn(Opcodes.GOTO, l2);

            super.visitLabel(l3);
            super.visitLineNumber(100 + 6 * 10 + 7, l3);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitInsn(Opcodes.NOP);

            final Label l9 = new Label();
            super.visitLabel(l9);
            super.visitLineNumber(100 + 6 * 10 + 4, l9);
            super.visitInsn(Opcodes.RETURN);

            final Label l10 = new Label();
            super.visitLabel(l10);

            super.visitLocalVariable("name", "Lnet/minecraft/util/ResourceLocation;", null, l0, l10, 0);
            super.visitLocalVariable("jsonList", "Ljava/util/List;", "Ljava/util/List<+Lnet/minecraft/client/resources/IResource;>;", l0, l10, 1);
            super.visitLocalVariable("properties", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l0, l10, 2);
            super.visitLocalVariable("$forEach$this", "Ljava/lang/Iterable;", "Ljava/lang/Iterable<+Lnet/minecraft/client/resources/IResource;>;", l1, l10, 3);
            super.visitLocalVariable("$forEach$$1", "Ljava/util/Iterator;", "Ljava/util/Iterator<+Lnet/minecraft/client/resources/IResource;>;", l2, l10, 4);
            super.visitLocalVariable("$forEach$element", "Ljava/lang/Object;", null, l4, l3, 5);
            super.visitLocalVariable("$forEach$it", "Lnet/minecraft/client/resources/IResource;", null, l5, l3, 6);
            super.visitLocalVariable("it", "Lnet/minecraft/client/resources/IResource;", null, l5, l3, 7);

            super.visitMaxs(3, 8);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class LoadJsonLocaleMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  // signature (Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResource;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V
        //  // declaration: void loadJsonLocale(net.minecraft.util.ResourceLocation, net.minecraft.client.resources.IResource, java.util.Map<java.lang.String, java.lang.String>)
        //  private final static loadJsonLocale(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResource;Ljava/util/Map;)V
        //   L0
        //    LINENUMBER 168 L0
        //    ALOAD 2
        //    ALOAD 0
        //    ALOAD 1
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:readJsonFile> (Lnet/minecraft/client/resources/IResource;)Ljava/lang/String;
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:tryParseFile> (Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Ljava/util/Map;
        //    INVOKEINTERFACE java/util/Map.putAll (Ljava/util/Map;)V (itf)
        //    RETURN
        //   L1
        //    LOCALVARIABLE name Lnet/minecraft/util/ResourceLocation; L0 L1 0
        //    LOCALVARIABLE resource Lnet/minecraft/client/resources/IResource; L0 L1 1
        //    LOCALVARIABLE properties Ljava/util/Map; L0 L1 2
        //    // signature Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: properties extends java.util.Map<java.lang.String, java.lang.String>
        //    MAXSTACK = 3
        //    MAXLOCALS = 3

        private LoadJsonLocaleMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(100 + 6 * 10 + 8, l0);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, READ_JSON_FILE, "(Lnet/minecraft/client/resources/IResource;)Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, TRY_PARSE_FILE, "(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Ljava/util/Map;", false);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", "putAll", "(Ljava/util/Map;)V", true);
            super.visitInsn(Opcodes.RETURN);

            final Label l1 = new Label();
            super.visitLabel(l1);

            super.visitLocalVariable("name", "Lnet/minecraft/util/ResourceLocation;", null, l0, l1, 0);
            super.visitLocalVariable("resource", "Lnet/minecraft/client/resources/IResource;", null, l0, l1, 1);
            super.visitLocalVariable("properties", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l0, l1, 2);

            super.visitMaxs(3, 3);
            super.visitEnd();
        }
    }

    @SuppressWarnings({"GrazieInspection", "SpellCheckingInspection"})
    private static final class ReadJsonFileMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private final static readJsonFile(Lnet/minecraft/client/resources/IResource;)Ljava/lang/String;
        //    TRYCATCHBLOCK L0 L1 L2 java/lang/Throwable
        //    TRYCATCHBLOCK L3 L4 L5 java/lang/Throwable
        //    TRYCATCHBLOCK L3 L4 L6 null
        //    TRYCATCHBLOCK L7 L8 L9 java/lang/Throwable
        //    TRYCATCHBLOCK L5 L10 L6 null
        //    TRYCATCHBLOCK L11 L12 L13 java/io/IOException
        //    TRYCATCHBLOCK L5 L13 L13 java/io/IOException
        //   L11
        //    LINENUMBER 170 L11
        //    LDC "This bytecode was generated and not written by hand: I have no idea how to make a try-with-resources and... this is super complicated OMG"
        //    POP
        //    NEW java/io/BufferedReader
        //    DUP
        //    NEW java/io/InputStreamReader
        //    DUP
        //    ALOAD 0
        //    INVOKEINTERFACE net/minecraft/client/resources/IResource.<fermion-remap:func_110527_b> ()Ljava/io/InputStream; (itf)
        //    INVOKESPECIAL java/io/InputStreamReader.<init> (Ljava/io/InputStream;)V
        //    CHECKCAST java/io/Reader
        //    INVOKESPECIAL java/io/BufferedReader.<init> (Ljava/io/Reader;)V
        //    ASTORE 1
        //   L14
        //    ACONST_NULL
        //    ASTORE 2
        //   L3
        //    LINENUMBER 171 L3
        //    ALOAD 1
        //    INVOKEVIRTUAL java/io/BufferedReader.lines ()Ljava/util/stream/Stream;
        //    LDC "\n"
        //    CHECKCAST java/lang/CharSequence
        //    INVOKESTATIC java/util/stream/Collectors.joining (Ljava/lang/CharSequence;)Ljava/util/stream/Collector;
        //    INVOKEINTERFACE java/util/stream/Stream.collect (Ljava/util/stream/Collector;)Ljava/lang/Object; (itf)
        //    LDC "reader.lines().collect(Collectors.joining((CharSequence) "\\n"))"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/lang/String
        //    ASTORE 3
        //   L4
        //    LINENUMBER 172 L4
        //    ALOAD 1
        //    IFNULL L12
        //    ALOAD 2
        //    IFNULL L15
        //   L0
        //    ALOAD 1
        //    INVOKEVIRTUAL java/io/BufferedReader.close ()V
        //   L1
        //    GOTO L12
        //   L2
        //   FRAME FULL [net/minecraft/client/resources/IResource java/io/BufferedReader java/lang/Throwable java/lang/String] [java/lang/Throwable]
        //    ASTORE 4
        //    ALOAD 2
        //    ALOAD 4
        //    INVOKEVIRTUAL java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
        //    GOTO L12
        //   L15
        //   FRAME SAME
        //    ALOAD 1
        //    INVOKEVIRTUAL java/io/BufferedReader.close ()V
        //   L12
        //    LINENUMBER 171 L12
        //   FRAME SAME
        //    ALOAD 3
        //    ARETURN
        //   L5
        //    LINENUMBER 170 L5
        //   FRAME FULL [net/minecraft/client/resources/IResource java/io/BufferedReader java/lang/Throwable] [java/lang/Throwable]
        //    ASTORE 3
        //    ALOAD 3
        //    ASTORE 2
        //    ALOAD 3
        //    ATHROW
        //   L6
        //    LINENUMBER 172 L6
        //   FRAME SAME1 java/lang/Throwable
        //    ASTORE 5
        //   L10
        //    ALOAD 1
        //    IFNULL L16
        //    ALOAD 2
        //    IFNULL L17
        //   L7
        //    ALOAD 1
        //    INVOKEVIRTUAL java/io/BufferedReader.close ()V
        //   L8
        //    GOTO L16
        //   L9
        //   FRAME FULL [net/minecraft/client/resources/IResource java/io/BufferedReader java/lang/Throwable T T java/lang/Throwable] [java/lang/Throwable]
        //    ASTORE 6
        //    ALOAD 2
        //    ALOAD 6
        //    INVOKEVIRTUAL java/lang/Throwable.addSuppressed (Ljava/lang/Throwable;)V
        //    GOTO L16
        //   L17
        //   FRAME SAME
        //    ALOAD 1
        //    INVOKEVIRTUAL java/io/BufferedReader.close ()V
        //   L16
        //   FRAME SAME
        //    ALOAD 5
        //    ATHROW
        //   L13
        //   FRAME FULL [net/minecraft/client/resources/IResource] [java/io/IOException]
        //    ASTORE 1
        //   L18
        //    LINENUMBER 173 L18
        //    LDC ""
        //    ARETURN
        //   L19
        //    LOCALVARIABLE resource Lnet/minecraft/client/resources/IResource; L11 L19 0
        //    LOCALVARIABLE reader Ljava/io/BufferedReader; L14 L13 1
        //    LOCALVARIABLE e Ljava/io/IOException; L18 L19 1
        //    MAXSTACK = 6
        //    MAXLOCALS = 7

        private ReadJsonFileMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            final Label l1 = new Label();
            final Label l2 = new Label();
            final Label l3 = new Label();
            final Label l4 = new Label();
            final Label l5 = new Label();
            final Label l6 = new Label();
            final Label l7 = new Label();
            final Label l8 = new Label();
            final Label l9 = new Label();
            final Label l10 = new Label();
            final Label l11 = new Label();
            final Label l12 = new Label();
            final Label l13 = new Label();
            super.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            super.visitTryCatchBlock(l3, l4, l5, "java/lang/Throwable");
            super.visitTryCatchBlock(l3, l4, l6, null);
            super.visitTryCatchBlock(l7, l8, l9, "java/lang/Throwable");
            super.visitTryCatchBlock(l5, l10, l6, null);
            super.visitTryCatchBlock(l11, l12, l13, "java/io/IOException");
            super.visitTryCatchBlock(l5, l13, l13, "java/io/IOException");

            super.visitLabel(l11);
            super.visitLineNumber(100 + 7 * 10, l11);
            super.visitLdcInsn("This bytecode was generated and not written by hand: I have no idea how to make a try-with-resources and... this is super complicated OMG");
            super.visitInsn(Opcodes.POP);
            super.visitTypeInsn(Opcodes.NEW, "java/io/BufferedReader");
            super.visitInsn(Opcodes.DUP);
            super.visitTypeInsn(Opcodes.NEW, "java/io/InputStreamReader");
            super.visitInsn(Opcodes.DUP);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "net/minecraft/client/resources/IResource", MappingUtilities.INSTANCE.mapMethod("func_110527_b"), "()Ljava/io/InputStream;", true);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/io/InputStreamReader", "<init>", "(Ljava/io/InputStream;)V", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/io/Reader");
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/io/BufferedReader", "<init>", "(Ljava/io/Reader;)V", false);
            super.visitVarInsn(Opcodes.ASTORE, 1);

            final Label l14 = new Label();
            super.visitLabel(l14);
            super.visitInsn(Opcodes.ACONST_NULL);
            super.visitVarInsn(Opcodes.ASTORE, 2);

            super.visitLabel(l3);
            super.visitLineNumber(100 + 7 * 10 + 1, l3);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/BufferedReader", "lines", "()Ljava/util/stream/Stream;", false);
            super.visitLdcInsn("\n");
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/CharSequence");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/stream/Collectors", "joining", "(Ljava/lang/CharSequence;)Ljava/util/stream/Collector;", false);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/stream/Stream", "collect", "(Ljava/util/stream/Collector;)Ljava/lang/Object;", true);
            super.visitLdcInsn("reader.lines().collect(Collectors.joining((CharSequence) \"\\n\"))");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l15 = new Label();
            super.visitLabel(l4);
            super.visitLineNumber(100 + 7 * 10 + 2, l4);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitJumpInsn(Opcodes.IFNULL, l12);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitJumpInsn(Opcodes.IFNULL, l15);

            super.visitLabel(l0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/BufferedReader", "close", "()V", false);

            super.visitLabel(l1);
            super.visitJumpInsn(Opcodes.GOTO, l12);

            super.visitLabel(l2);
            super.visitFrame(Opcodes.F_FULL, 4, new Object[] { "net/minecraft/client/resources/IResource", "java/io/BufferedReader", "java/lang/Throwable", "java/lang/String" }, 1, new Object[] { "java/lang/Throwable" });
            super.visitVarInsn(Opcodes.ASTORE, 4);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "addSuppressed", "(Ljava/lang/Throwable;)V", false);
            super.visitJumpInsn(Opcodes.GOTO, l12);

            super.visitLabel(l15);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/BufferedReader", "close", "()V", false);

            super.visitLabel(l12);
            super.visitLineNumber(100 + 7 * 10 + 1, l12);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitInsn(Opcodes.ARETURN);

            super.visitLabel(l5);
            super.visitLineNumber(100 + 7 * 10, l5);
            super.visitFrame(Opcodes.F_FULL, 3, new Object[] { "net/minecraft/client/resources/IResource", "java/io/BufferedReader", "java/lang/Throwable" }, 1, new Object[] { "java/lang/Throwable" });
            super.visitVarInsn(Opcodes.ASTORE, 3);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitVarInsn(Opcodes.ASTORE, 2);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitInsn(Opcodes.ATHROW);

            super.visitLabel(l6);
            super.visitLineNumber(100 + 7 * 10 + 2, l6);
            super.visitFrame(Opcodes.F_SAME1, 0, null,1, new Object[] { "java/lang/Throwable" });
            super.visitVarInsn(Opcodes.ASTORE, 5);

            final Label l16 = new Label();
            final Label l17 = new Label();
            super.visitLabel(l10);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitJumpInsn(Opcodes.IFNULL, l16);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitJumpInsn(Opcodes.IFNULL, l17);

            super.visitLabel(l7);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/BufferedReader", "close", "()V", false);

            super.visitLabel(l8);
            super.visitJumpInsn(Opcodes.GOTO, l16);

            super.visitLabel(l9);
            super.visitFrame(Opcodes.F_FULL, 6, new Object[] { "net/minecraft/client/resources/IResource", "java/io/BufferedReader", "java/lang/Throwable", Opcodes.TOP, Opcodes.TOP, "java/lang/Throwable" }, 1, new Object[] { "java/lang/Throwable" });
            super.visitVarInsn(Opcodes.ASTORE, 6);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "addSuppressed", "(Ljava/lang/Throwable;)V", false);
            super.visitJumpInsn(Opcodes.GOTO, l16);

            super.visitLabel(l17);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/BufferedReader", "close", "()V", false);

            super.visitLabel(l16);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitInsn(Opcodes.ATHROW);

            super.visitLabel(l13);
            super.visitFrame(Opcodes.F_FULL, 1, new Object[] { "net/minecraft/client/resources/IResource" }, 1, new Object[] { "java/io/IOException" });
            super.visitVarInsn(Opcodes.ASTORE, 1);

            final Label l18 = new Label();
            super.visitLabel(l18);
            super.visitLineNumber(100 + 7 * 10 + 3, l18);
            super.visitLdcInsn("");
            super.visitInsn(Opcodes.ARETURN);

            final Label l19 = new Label();
            super.visitLabel(l19);

            super.visitLocalVariable("resource", "Lnet/minecraft/client/resources/IResource;", null, l11, l19, 0);
            super.visitLocalVariable("reader", "Ljava/io/BufferedReader;", null, l14, l13, 1);
            super.visitLocalVariable("e", "Ljava/io/IOException;", null, l18, l19, 1);

            super.visitMaxs(6, 7);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class TryParseFileMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  // signature (Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //  // declaration: java.util.Map<java.lang.String, java.lang.String> tryParseFile(net.minecraft.util.ResourceLocation, java.lang.String)
        //  private final static tryParseFile(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Ljava/util/Map;
        //    TRYCATCHBLOCK L0 L1 L2 com/google/gson/JsonParseException
        //   L0
        //    LINENUMBER 178 L0
        //    NOP
        //   L3
        //    LINENUMBER 179 L3
        //    ALOAD 1
        //    CHECKCAST java/lang/CharSequence
        //    ASTORE 2
        //   L4
        //    ALOAD 2
        //    INVOKEINTERFACE java/lang/CharSequence.length ()I (itf)
        //    ICONST_0
        //    IF_ICMPEQ L5
        //    ICONST_0
        //    GOTO L6
        //   L5
        //   FRAME APPEND [java/lang/CharSequence]
        //    ICONST_1
        //   L6
        //   FRAME SAME1 I
        //    IFEQ L7
        //   L8
        //    LINENUMBER 180 L8
        //    INVOKESTATIC java/util/Collections.emptyMap ()Ljava/util/Map;
        //    GOTO L9
        //   L7
        //    LINENUMBER 182 L7
        //   FRAME SAME
        //    NEW com/google/gson/GsonBuilder
        //    DUP
        //    INVOKESPECIAL com/google/gson/GsonBuilder.<init> ()V
        //    INVOKEVIRTUAL com/google/gson/GsonBuilder.setPrettyPrinting ()Lcom/google/gson/GsonBuilder;
        //    INVOKEVIRTUAL com/google/gson/GsonBuilder.serializeNulls ()Lcom/google/gson/GsonBuilder;
        //    INVOKEVIRTUAL com/google/gson/GsonBuilder.disableHtmlEscaping ()Lcom/google/gson/GsonBuilder;
        //    INVOKEVIRTUAL com/google/gson/GsonBuilder.create ()Lcom/google/gson/Gson;
        //    CHECKCAST java/lang/Object
        //    LDC "new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST com/google/gson/Gson
        //    ALOAD 1
        //    LDC Lcom/google/gson/JsonObject;.class
        //    INVOKEVIRTUAL com/google/gson/Gson.fromJson (Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;
        //    LDC "new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().fromJson(content, JsonObject.class)"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST com/google/gson/JsonObject
        //    ASTORE 2
        //   L10
        //    LINENUMBER 183 L10
        //    NEW java/util/LinkedHashMap
        //    DUP
        //    INVOKESPECIAL java/util/LinkedHashMap.<init> ()V
        //    CHECKCAST java/util/Map
        //    ASTORE 3
        //   L11
        //    LINENUMBER 184 L11
        //    ALOAD 2
        //    INVOKEVIRTUAL com/google/gson/JsonObject.entrySet ()Ljava/util/Set;
        //    CHECKCAST java/lang/Iterable
        //    CHECKCAST java/lang/Object
        //    LDC "jsonObject.entrySet()"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/util/Set
        //    CHECKCAST java/lang/Iterable
        //    ASTORE 4
        //   L12
        //    ALOAD 4
        //    INVOKEINTERFACE java/lang/Iterable.iterator ()Ljava/util/Iterator; (itf)
        //    ASTORE 5
        //   L13
        //   FRAME FULL [net/minecraft/util/ResourceLocation java/lang/String com/google/gson/JsonObject java/util/Map java/lang/Iterable java/util/Iterator] []
        //    ALOAD 5
        //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
        //    IFEQ L14
        //    ALOAD 5
        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
        //    ASTORE 6
        //   L15
        //    ALOAD 6
        //    CHECKCAST java/util/Map$Entry
        //    ASTORE 7
        //   L16
        //    ALOAD 7
        //    ASTORE 8
        //   L17
        //    ALOAD 3
        //    ALOAD 8
        //    INVOKEINTERFACE java/util/Map$Entry.getKey ()Ljava/lang/Object; (itf)
        //    LDC "it.getKey()"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/lang/String
        //    ALOAD 8
        //    INVOKEINTERFACE java/util/Map$Entry.getValue ()Ljava/lang/Object; (itf)
        //    LDC "it.getValue()"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST com/google/gson/JsonElement
        //    ALOAD 8
        //    INVOKEINTERFACE java/util/Map$Entry.getKey ()Ljava/lang/Object; (itf)
        //    LDC "it.getKey()"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/lang/String
        //    INVOKESTATIC net/minecraft/client/resources/Locale.<fermion-inject:attemptAsString> (Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;
        //    LDC "attemptAsString(it.getValue(), it.getKey())"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/lang/String
        //    INVOKEINTERFACE java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; (itf)
        //    POP
        //   L18
        //    GOTO L13
        //   L14
        //    LINENUMBER 210 L14
        //   FRAME SAME
        //    NOP
        //   L19
        //    LINENUMBER 185 L19
        //    NEW java/util/LinkedHashMap
        //    DUP
        //    ALOAD 3
        //    INVOKESPECIAL java/util/LinkedHashMap.<init> (Ljava/util/Map;)V
        //    CHECKCAST java/util/Map
        //   L9
        //   FRAME FULL [net/minecraft/util/ResourceLocation java/lang/String] [java/util/Map]
        //    ASTORE 2
        //   L1
        //    NOP
        //    GOTO L20
        //   L2
        //    LINENUMBER 187 L2
        //   FRAME SAME1 com/google/gson/JsonParseException
        //    ASTORE 3
        //   L21
        //    LINENUMBER 188 L21
        //    ALOAD 3
        //    INVOKEVIRTUAL com/google/gson/JsonParseException.getMessage ()Ljava/lang/String;
        //    ASTORE 4
        //   L22
        //    LINENUMBER 189 L22
        //    ALOAD 3
        //    INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;
        //    INVOKEVIRTUAL java/lang/Class.getSimpleName ()Ljava/lang/String;
        //    ASTORE 5
        //   L23
        //    LINENUMBER 190 L23
        //    LDC "Boson ASM/JSON Locale"
        //    INVOKESTATIC org/apache/logging/log4j/LogManager.getLogger (Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;
        //    LDC "LogManager.getLogger("Boson ASM/JSON Locale")"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST org/apache/logging/log4j/Logger
        //    ASTORE 6
        //   L24
        //    LINENUMBER 191 L24
        //    ALOAD 6
        //    LDC "********************************************************************************"
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;)V (itf)
        //   L25
        //    LINENUMBER 192 L25
        //    ALOAD 6
        //    LDC "An error has occurred while attempting to load the language file '{}'"
        //    ALOAD 0
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;Ljava/lang/Object;)V (itf)
        //   L26
        //    LINENUMBER 193 L26
        //    ALOAD 6
        //    LDC "Loading will now be skipped and the file will be considered effectively empty!"
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;)V (itf)
        //   L27
        //    LINENUMBER 194 L27
        //    ALOAD 6
        //    LDC ""
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;)V (itf)
        //   L28
        //    LINENUMBER 195 L28
        //    ALOAD 6
        //    LDC "Error message: {}"
        //    ALOAD 4
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;Ljava/lang/Object;)V (itf)
        //   L29
        //    LINENUMBER 196 L29
        //    ALOAD 6
        //    LDC "Exception type: {}"
        //    ALOAD 5
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;Ljava/lang/Object;)V (itf)
        //   L30
        //    LINENUMBER 197 L30
        //    ALOAD 6
        //    LDC "Name of the file that caused the error: {}"
        //    ALOAD 0
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;Ljava/lang/Object;)V (itf)
        //   L31
        //    LINENUMBER 198 L31
        //    ALOAD 6
        //    LDC "********************************************************************************"
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;)V (itf)
        //   L32
        //    LINENUMBER 199 L32
        //    INVOKESTATIC java/util/Collections.emptyMap ()Ljava/util/Map;
        //    ASTORE 2
        //   L20
        //    LINENUMBER 200 L20
        //   FRAME APPEND [java/util/Map]
        //    ALOAD 2
        //    ARETURN
        //   L33
        //    LOCALVARIABLE name Lnet/minecraft/util/ResourceLocation; L1 L33 0
        //    LOCALVARIABLE content Ljava/lang/String; L1 L33 1
        //    LOCALVARIABLE $isEmpty$this Ljava/lang/CharSequence; L4 L7 2
        //    LOCALVARIABLE jsonObject Lcom/google/gson/JsonObject; L10 L9 2
        //    LOCALVARIABLE returningMap Ljava/util/Map; L1 L33 2
        //    // signature Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: returningMap extends java.util.Map<java.lang.String, java.lang.String>
        //    LOCALVARIABLE targetMap Ljava/util/Map; L11 L9 3
        //    // signature Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: targetMap extends java.util.Map<java.lang.String, java.lang.String>
        //    LOCALVARIABLE e Lcom/google/gson/JsonParseException; L21 L20 3
        //    LOCALVARIABLE $forEach$this Ljava/lang/Iterable; L12 L9 4
        //    // signature Ljava/lang/Iterable<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;>;
        //    // declaration: $forEach$this extends java.lang.Iterable<java.util.Map.Entry<java.lang.String, java.lang.String>>
        //    LOCALVARIABLE errorMessage Ljava/lang/String; L22 L20 4
        //    LOCALVARIABLE $forEach$$1 Ljava/util/Iterator; L13 L14 5
        //    // signature Ljava/util/Iterator<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;>;
        //    // declaration: $forEach$$1 extends java.util.Iterator<java.util.Map.Entry<java.lang.String, java.lang.String>>
        //    LOCALVARIABLE exceptionType Ljava/lang/Class; L23 L20 5
        //    // signature Ljava/lang/Class<+Ljava/lang/Throwable;>;
        //    // declaration: exceptionType extends java.lang.Class<? extends java.lang.Throwable>
        //    LOCALVARIABLE $forEach$element Ljava/lang/Object; L15 L14 6
        //    LOCALVARIABLE logger Lorg/apache/logging/log4j/Logger; L24 L20 6
        //    LOCALVARIABLE $forEach$it Ljava/util/Map$Entry; L16 L14 7
        //    // signature Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: $forEach$it extends java.util.Map.Entry<java.lang.String, java.lang.String>
        //    LOCALVARIABLE it Ljava/util/Map$Entry; L17 L14 8
        //    // signature Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;
        //    // declaration: it extends java.util.Map.Entry<java.lang.String, java.lang.String>
        //    MAXSTACK = 6
        //    MAXLOCALS = 9

        private TryParseFileMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitTryCatchBlock(l0, l1, l2, "com/google/gson/JsonParseException");

            super.visitLabel(l0);
            super.visitLineNumber(100 + 7 * 10 + 8, l0);
            super.visitInsn(Opcodes.NOP);

            final Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(100 + 7 * 10 + 9, l3);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/CharSequence");
            super.visitVarInsn(Opcodes.ASTORE, 2);

            final Label l4 = new Label();
            final Label l5 = new Label();
            final Label l6 = new Label();
            super.visitLabel(l4);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/CharSequence", "length", "()I", true);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitJumpInsn(Opcodes.IF_ICMPEQ, l5);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitJumpInsn(Opcodes.GOTO, l6);

            super.visitLabel(l5);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/CharSequence" }, 0, null);
            super.visitInsn(Opcodes.ICONST_1);

            final Label l7 = new Label();
            super.visitLabel(l6);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { Opcodes.INTEGER });
            super.visitJumpInsn(Opcodes.IFEQ, l7);

            final Label l8 = new Label();
            final Label l9 = new Label();
            super.visitLabel(l8);
            super.visitLineNumber(100 + 8 * 10, l8);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Collections", "emptyMap", "()Ljava/util/Map;", false);
            super.visitJumpInsn(Opcodes.GOTO, l9);

            super.visitLabel(l7);
            super.visitLineNumber(100 + 8 * 10 + 2, l7);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitTypeInsn(Opcodes.NEW, "com/google/gson/GsonBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/google/gson/GsonBuilder", "<init>", "()V", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/gson/GsonBuilder", "setPrettyPrinting", "()Lcom/google/gson/GsonBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/gson/GsonBuilder", "serializeNulls", "()Lcom/google/gson/GsonBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/gson/GsonBuilder", "disableHtmlEscaping", "()Lcom/google/gson/GsonBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/gson/GsonBuilder", "create", "()Lcom/google/gson/Gson;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
            super.visitLdcInsn("new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create()");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "com/google/gson/Gson");
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitLdcInsn(Type.getType("Lcom/google/gson/JsonObject;"));
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/gson/Gson", "fromJson", "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;", false);
            super.visitLdcInsn("new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create().fromJson(content, JsonObject.class)");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "com/google/gson/JsonObject");
            super.visitVarInsn(Opcodes.ASTORE, 2);

            final Label l10 = new Label();
            super.visitLabel(l10);
            super.visitLineNumber(100 + 8 * 10 + 3, l10);
            super.visitTypeInsn(Opcodes.NEW, "java/util/LinkedHashMap");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map");
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l11 = new Label();
            super.visitLabel(l11);
            super.visitLineNumber(100 + 8 * 10 + 4, l11);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/gson/JsonObject", "entrySet", "()Ljava/util/Set;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Iterable");
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
            super.visitLdcInsn("jsonObject.entrySet()");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Set");
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Iterable");
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l12 = new Label();
            super.visitLabel(l12);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/Iterable", "iterator", "()Ljava/util/Iterator;", true);
            super.visitVarInsn(Opcodes.ASTORE, 5);

            final Label l13 = new Label();
            final Label l14 = new Label();
            super.visitLabel(l13);
            super.visitFrame(Opcodes.F_FULL, 6, new Object[] { "net/minecraft/util/ResourceLocation", "java/lang/String", "com/google/gson/JsonObject", "java/util/Map", "java/lang/Iterable", "java/util/Iterator" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            super.visitJumpInsn(Opcodes.IFEQ, l14);
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            super.visitVarInsn(Opcodes.ASTORE, 6);

            final Label l15 = new Label();
            super.visitLabel(l15);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map$Entry");
            super.visitVarInsn(Opcodes.ASTORE, 7);

            final Label l16 = new Label();
            super.visitLabel(l16);
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitVarInsn(Opcodes.ASTORE, 8);

            final Label l17 = new Label();
            super.visitLabel(l17);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitVarInsn(Opcodes.ALOAD, 8);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true);
            super.visitLdcInsn("it.getKey()");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            super.visitVarInsn(Opcodes.ALOAD, 8);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true);
            super.visitLdcInsn("it.getValue()");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "com/google/gson/JsonElement");
            super.visitVarInsn(Opcodes.ALOAD, 8);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true);
            super.visitLdcInsn("it.getKey()");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, THIS, ATTEMPT_AS_STRING, "(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;", false);
            super.visitLdcInsn("attemptAsString(it.getValue(), it.getKey())");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            super.visitInsn(Opcodes.POP);

            final Label l18 = new Label();
            super.visitLabel(l18);
            super.visitJumpInsn(Opcodes.GOTO, l13);

            super.visitLabel(l14);
            super.visitLineNumber(2 * 100 + 10, l14);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitInsn(Opcodes.NOP);

            final Label l19 = new Label();
            super.visitLabel(l19);
            super.visitLineNumber(100 + 8 * 10 + 5, l19);
            super.visitTypeInsn(Opcodes.NEW, "java/util/LinkedHashMap");
            super.visitInsn(Opcodes.DUP);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "(Ljava/util/Map;)V", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map");

            super.visitLabel(l9);
            super.visitFrame(Opcodes.F_FULL, 2, new Object[] { "net/minecraft/util/ResourceLocation", "java/lang/String" }, 1, new Object[] { "java/util/Map" });
            super.visitVarInsn(Opcodes.ASTORE, 2);

            final Label l20 = new Label();
            super.visitLabel(l1);
            super.visitInsn(Opcodes.NOP);
            super.visitJumpInsn(Opcodes.GOTO, l20);

            super.visitLabel(l2);
            super.visitLineNumber(100 + 8 * 10 + 7, l2);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "com/google/gson/JsonParseException" });
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l21 = new Label();
            super.visitLabel(l21);
            super.visitLineNumber(100 + 8 * 10 + 8, l21);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/google/gson/JsonParseException", "getMessage", "()Ljava/lang/String;", false);
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l22 = new Label();
            super.visitLabel(l22);
            super.visitLineNumber(100 + 8 * 10 + 9, l22);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getSimpleName", "()Ljava/lang/String;", false);
            super.visitVarInsn(Opcodes.ASTORE, 5);

            final Label l23 = new Label();
            super.visitLabel(l23);
            super.visitLineNumber(100 + 9 * 10, l23);
            super.visitLdcInsn("Boson ASM/JSON Locale");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/apache/logging/log4j/LogManager", "getLogger", "(Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;", false);
            super.visitLdcInsn("LogManager.getLogger(\"Boson ASM/JSON Locale\")");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "org/apache/logging/log4j/Logger");
            super.visitVarInsn(Opcodes.ASTORE, 6);

            final Label l24 = new Label();
            super.visitLineNumber(100 + 9 * 10 + 1, l24);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("********************************************************************************");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;)V", true);

            final Label l25 = new Label();
            super.visitLabel(l25);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("An error has occurred while attempting to load the language file '{}'");
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Object;)V", true);

            final Label l26 = new Label();
            super.visitLabel(l26);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("Loading will now be skipped and the file will be considered effectively empty!");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;)V", true);

            final Label l27 = new Label();
            super.visitLabel(l27);
            super.visitLineNumber(100 + 9 * 10 + 4, l27);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;)V", true);

            final Label l28 = new Label();
            super.visitLabel(l28);
            super.visitLineNumber(100 + 9 * 10 + 5, l28);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("Error message: {}");
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Object;)V", true);

            final Label l29 = new Label();
            super.visitLabel(l29);
            super.visitLineNumber(100 + 9 * 10 + 6, l29);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("Exception type: {}");
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Object;)V", true);

            final Label l30 = new Label();
            super.visitLabel(l30);
            super.visitLineNumber(100 + 9 * 10 + 7, l30);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("Name of the file that caused the error: {}");
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Object;)V", true);

            final Label l31 = new Label();
            super.visitLabel(l31);
            super.visitLineNumber(100 + 9 * 10 + 8, l31);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("********************************************************************************");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;)V", true);

            final Label l32 = new Label();
            super.visitLabel(l32);
            super.visitLineNumber(100 + 9 * 10 + 9, l32);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Collections", "emptyMap", "()Ljava/util/Map;", false);
            super.visitVarInsn(Opcodes.ASTORE, 2);

            super.visitLabel(l20);
            super.visitLineNumber(2 * 100, l20);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/Map" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitInsn(Opcodes.ARETURN);

            final Label l33 = new Label();
            super.visitLabel(l33);

            super.visitLocalVariable("name", "Lnet/minecraft/util/ResourceLocation;", null, l1, l33, 0);
            super.visitLocalVariable("content", "Ljava/lang/String;", null, l1, l33, 1);
            super.visitLocalVariable("$isEmpty$this", "Ljava/lang/CharSequence;", null, l4, l7, 2);
            super.visitLocalVariable("jsonObject", "Lcom/google/gson/JsonObject;", null, l10, l9, 2);
            super.visitLocalVariable("returningMap", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l1, l33, 2);
            super.visitLocalVariable("targetMap", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;", l11, l9, 3);
            super.visitLocalVariable("e", "Lcom/google/gson/JsonParseException;", null, l21, l20, 3);
            super.visitLocalVariable("$forEach$this", "Ljava/lang/Iterable;", "Ljava/lang/Iterable<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;>;", l12, l9, 4);
            super.visitLocalVariable("errorMessage", "Ljava/lang/String;", null, l22, l20, 4);
            super.visitLocalVariable("$forEach$$1", "Ljava/util/Iterator;", "Ljava/util/Itearator<Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;>;", l13, l14, 5);
            super.visitLocalVariable("exceptionType", "Ljava/lang/Class;", "Ljava/lang/Class<+Ljava/lang/Throwable;>;", l23, l20, 5);
            super.visitLocalVariable("$forEach$element", "Ljava/lang/Object;", null, l15, l14, 6);
            super.visitLocalVariable("logger", "Lorg/apache/logging/log4j/Logger;", null, l24, l20, 6);
            super.visitLocalVariable("$forEach$it", "Ljava/util/Map$Entry;", "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;", l16, l14, 7);
            super.visitLocalVariable("it", "Ljava/util/Map$Entry;", "Ljava/util/Map$Entry<Ljava/lang/String;Ljava/lang/String;>;", l17, l14, 8);

            super.visitMaxs(6, 9);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class AttemptAsStringMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  private static final attemptAsString(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;
        //   L0
        //    LINENUMBER 202 L0
        //    ALOAD 0
        //    ALOAD 1
        //    INVOKESTATIC net/minecraft/util/JsonUtils.<fermion-remap:func_151206_a> (Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;
        //    ARETURN
        //   L1
        //    LOCALVARIABLE $this$attemptAsString Lcom/google/gson/JsonElement; L0 L1 0
        //    LOCALVARIABLE name Ljava/lang/String; L0 L1 1
        //    MAXSTACK = 2
        //    MAXLOCALS = 2

        private AttemptAsStringMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(2 * 100 + 2, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/minecraft/util/JsonUtils", MappingUtilities.INSTANCE.mapMethod("func_151206_a"), "(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;", false);
            super.visitInsn(Opcodes.ARETURN);

            final Label l1 = new Label();
            super.visitLabel(l1);

            super.visitLocalVariable("$this$attemptAsString", "Lcom/google/gson/JsonElement;", null, l0, l1, 0);
            super.visitLocalVariable("name", "Ljava/lang/String;", null, l0, l1, 1);

            super.visitMaxs(2, 2);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class SafelyGetAllResourcesMethodVisitor extends MethodVisitor {
        //  // access flags 0x1A
        //  // signature (Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/util/ResourceLocation;)Ljava/util/List<Lnet/minecraft/client/resources/IResource;>;
        //  // declaration: java.util.List<net.minecraft.client.resources.IResource> safelyGetAllResources(net.minecraft.client.resources.IResourceManager, net.minecraft.util.ResourceLocation)
        //  private static final safelyGetAllResources(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/util/ResourceLocation;)Ljava/util/List;
        //    TRYCATCHBLOCK L0 L1 L2 java/io/IOException
        //   L0
        //    LINENUMBER 203 L0
        //    NOP
        //    ALOAD 0
        //    ALOAD 1
        //    INVOKEINTERFACE net/minecraft/client/resources/IResourceManager.<fermion-remap:func_135056_b> (Lnet/minecraft/util/ResourceLocation;)Ljava/util/List; (itf)
        //    LDC "$this$safelyGetAllResources.func_135056_b(location)"
        //    INVOKESTATIC java/util/Objects.requireNonNull (Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
        //    CHECKCAST java/util/List
        //    CHECKCAST java/lang/Iterable
        //    CHECKCAST java/util/List
        //    ASTORE 2
        //   L1
        //    GOTO L3
        //   L2
        //   FRAME SAME1 java/io/IOException
        //    ASTORE 3
        //   L4
        //    INVOKESTATIC java/util/Collections.emptyList ()Ljava/util/List;
        //    ASTORE 2
        //   L3
        //    LINENUMBER 203 L3
        //   FRAME APPEND [java/util/List]
        //    ALOAD 2
        //    ARETURN
        //   L5
        //    LOCALVARIABLE $this$safelyGetAllResources Lnet/minecraft/client/resources/IResourceManager; L0 L5 0
        //    LOCALVARIABLE location Lnet/minecraft/util/ResourceLocation; L0 L5 1
        //    LOCALVARIABLE list Ljava/util/List; L1 L5 2
        //    // signature Ljava/util/List<Lnet/minecraft/client/resources/IResource;>;
        //    // declaration: list extends java.util.List<net.minecraft.client.resources.IResource>
        //    LOCALVARIABLE e Ljava/io/IOException; L4 L3 3
        //    MAXSTACK = 3
        //    MAXLOCALS = 4

        private SafelyGetAllResourcesMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitTryCatchBlock(l0, l1, l2, "java/io/IOException");

            super.visitLabel(l0);
            super.visitLineNumber(2 * 100 + 3, l0);
            super.visitInsn(Opcodes.NOP);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "net/minecraft/client/resources/IResourceManager", MappingUtilities.INSTANCE.mapMethod("func_135056_b"), "(Lnet/minecraft/util/ResourceLocation;)Ljava/util/List;", true);
            super.visitLdcInsn("$this$safelyGetAllResources.func_135056_b(location)");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Objects","requireNonNull","(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/List");
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Iterable");
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/List");
            super.visitVarInsn(Opcodes.ASTORE, 2);

            final Label l3 = new Label();
            super.visitLabel(l1);
            super.visitJumpInsn(Opcodes.GOTO, l3);

            super.visitLabel(l2);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/io/IOException" });
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l4 = new Label();
            super.visitLabel(l4);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/util/Collections", "emptyList", "()Ljava/util/List;", false);
            super.visitVarInsn(Opcodes.ASTORE, 2);

            super.visitLabel(l3);
            super.visitLineNumber(2 * 100 + 3, l3);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/List" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitInsn(Opcodes.ARETURN);

            final Label l5 = new Label();
            super.visitLabel(l5);

            super.visitLocalVariable("$this$safelyGetAllResources", "Lnet/minecraft/client/resources/IResourceManager;", null, l0, l5, 0);
            super.visitLocalVariable("location", "Lnet/minecraft/util/ResourceLocation;", null, l0, l5, 1);
            super.visitLocalVariable("list", "Ljava/util/List;", "Ljava/util/List<Lnet/minecraft/client/resources/IResource;>;", l1, l5, 2);
            super.visitLocalVariable("e", "Ljava/io/IOException;", null, l4, l3, 3);

            super.visitMaxs(3, 4);
            super.visitEnd();
        }
    }

    private static final String THIS = "net/minecraft/client/resources/Locale";
    private static final String HOOK_JSON_LOCALE = "fermion$$injected$$hookJsonLocale$$generated$$00_64_1122";
    private static final String RUN_HOOK = "fermion$$injected$$runHook$$generated$$00_64_1122";
    private static final String LOAD_ALL_JSON_FILES_FOR_LANGUAGE = "fermion$$injected$$loadAllJsonFilesForLanguage$$generated$$00_64_1122";
    private static final String SAFELY_GET_ALL_RESOURCES = "fermion$$injected$$safelyGetAllResources$$generated$$00_64_1122";
    private static final String LOAD_JSON_LOCALE_FILES = "fermion$$injected$$loadJsonLocaleFiles$$generated$$00_64_1122";
    private static final String LOAD_JSON_LOCALE = "fermion$$injected$$loadJsonLocale$$generated$$00_64_1122";
    private static final String READ_JSON_FILE = "fermion$$injected$$readJsonFile$$generated$$00_64_1122";
    private static final String TRY_PARSE_FILE = "fermion$$injected$$tryParseFile$$generated$$00_64_1122";
    private static final String ATTEMPT_AS_STRING = "fermion$$injected$$attemptAsString$$generated$$00_64_1122";

    public LocaleTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("locale")
                        .setDescription("Edits the Locale class so that it supports loading of JSON lang files")
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
                if (MappingUtilities.INSTANCE.mapMethod("func_135022_a").equals(name) && "(Lnet/minecraft/client/resources/IResourceManager;Ljava/util/List;)V".equals(desc)) {
                    return new HookInjectorMethodVisitor(v, parent);
                }
                return parent;
            }

            @Override
            public void visitEnd() {
                new HookJsonLocaleMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                HOOK_JSON_LOCALE,
                                "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V",
                                "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V",
                                null
                        )
                ).visitCode();

                new RunHookMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                RUN_HOOK,
                                "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V",
                                "(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V",
                                null
                        )
                ).visitCode();

                new LoadAllJsonFilesForLanguageMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                LOAD_ALL_JSON_FILES_FOR_LANGUAGE,
                                "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map;)V",
                                "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResourceManager;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V",
                                null
                        )
                ).visitCode();

                new LoadJsonLocaleFilesMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                LOAD_JSON_LOCALE_FILES,
                                "(Lnet/minecraft/util/ResourceLocation;Ljava/util/List;Ljava/util/Map;)V",
                                "(Lnet/minecraft/util/ResourceLocation;Ljava/util/List<+Lnet/minecraft/client/resources/IResource;>;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V",
                                null
                        )
                ).visitCode();

                new LoadJsonLocaleMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                LOAD_JSON_LOCALE,
                                "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResource;Ljava/util/Map;)V",
                                "(Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/resources/IResource;Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;)V",
                                null
                        )
                ).visitCode();

                new ReadJsonFileMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                READ_JSON_FILE,
                                "(Lnet/minecraft/client/resources/IResource;)Ljava/lang/String;",
                                null,
                                null
                        )
                ).visitCode();

                new TryParseFileMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                TRY_PARSE_FILE,
                                "(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Ljava/util/Map;",
                                "(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;",
                                null
                        )
                ).visitCode();

                new AttemptAsStringMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                ATTEMPT_AS_STRING,
                                "(Lcom/google/gson/JsonElement;Ljava/lang/String;)Ljava/lang/String;",
                                null,
                                null
                        )
                ).visitCode();

                new SafelyGetAllResourcesMethodVisitor(
                        v,
                        super.visitMethod(
                                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                                SAFELY_GET_ALL_RESOURCES,
                                "(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/util/ResourceLocation;)Ljava/util/List;",
                                "(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/util/ResourceLocation;)Ljava/util/List<Lnet/minecraft/client/resources/IResource;>;",
                                null
                        )
                ).visitCode();

                super.visitEnd();
            }
        };
    }
}
