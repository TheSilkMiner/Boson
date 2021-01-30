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

public final class ModelLoaderVariantLoaderTransformer extends AbstractTransformer {

    @SuppressWarnings("SpellCheckingInspection")
    private static final class LoadModelMethodVisitor extends MethodVisitor {
        //  // access flags 0x1
        //  public loadModel(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/client/model/IModel; throws java/lang/Exception
        //    TRYCATCHBLOCK L0 L1 L2 net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException
        //   L3
        //    LINENUMBER 1170 L3
        //    ALOAD 1
        //    CHECKCAST net/minecraft/client/renderer/block/model/ModelResourceLocation
        //    ASTORE 2
        //   L4
        //    LINENUMBER 1171 L4
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/client/model/ModelLoader$VariantLoader.loader : Lnet/minecraftforge/client/model/ModelLoader;
        //    ALOAD 2
        //    INVOKEVIRTUAL net/minecraftforge/client/model/ModelLoader.getModelBlockDefinition (Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition;
        //    ASTORE 3
        //   L0
        //    LINENUMBER 1175 L0
        // <<< INJECTION BEGIN
        //    ALOAD 0
        // >>> INJECTION END
        //    ALOAD 3
        //    ALOAD 2
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelResourceLocation.getVariant ()Ljava/lang/String;
        // <<< OVERWRITE BEGIN
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelBlockDefinition.getVariant (Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;
        // === OVERWRITE WITH
        //    INVOKESPECIAL net/minecraftforge/client/model/ModelLoader$VariantLoader.<fermion-inject:getVariantByName> (Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition;Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;
        // >>> OVERWRITE END
        //    ASTORE 4
        //   L5
        //    LINENUMBER 1176 L5
        //    NEW net/minecraftforge/client/model/ModelLoader$WeightedRandomModel
        //    DUP
        //    ALOAD 2
        //    ALOAD 4
        //    INVOKESPECIAL net/minecraftforge/client/model/ModelLoader$WeightedRandomModel.<init> (Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/block/model/VariantList;)V
        //   L1
        //    ARETURN
        //   L2
        //    LINENUMBER 1178 L2
        //   FRAME FULL [net/minecraftforge/client/model/ModelLoader$VariantLoader net/minecraft/util/ResourceLocation net/minecraft/client/renderer/block/model/ModelResourceLocation net/minecraft/client/renderer/block/model/ModelBlockDefinition] [net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException]
        //    ASTORE 4
        //   L6
        //    LINENUMBER 1180 L6
        //    ALOAD 3
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/client/model/ModelLoader$VariantLoader.loader : Lnet/minecraftforge/client/model/ModelLoader;
        //    INVOKESTATIC net/minecraftforge/client/model/ModelLoader.access$1700 (Lnet/minecraftforge/client/model/ModelLoader;)Ljava/util/Map;
        //    ALOAD 2
        //    INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; (itf)
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelBlockDefinition.equals (Ljava/lang/Object;)Z
        //    IFEQ L7
        //   L8
        //    LINENUMBER 1182 L8
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/client/model/ModelLoader$VariantLoader.loader : Lnet/minecraftforge/client/model/ModelLoader;
        //    INVOKESTATIC net/minecraftforge/client/model/ModelLoader.access$1800 (Lnet/minecraftforge/client/model/ModelLoader;)Ljava/util/Map;
        //    ALOAD 3
        //    INVOKEINTERFACE java/util/Map.get (Ljava/lang/Object;)Ljava/lang/Object; (itf)
        //    CHECKCAST net/minecraftforge/client/model/IModel
        //    ASTORE 5
        //   L9
        //    LINENUMBER 1183 L9
        //    ALOAD 5
        //    IFNONNULL L10
        //   L11
        //    LINENUMBER 1185 L11
        //    NEW net/minecraftforge/client/model/ModelLoader$MultipartModel
        //    DUP
        //    NEW net/minecraft/util/ResourceLocation
        //    DUP
        //    ALOAD 2
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelResourceLocation.getNamespace ()Ljava/lang/String;
        //    ALOAD 2
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelResourceLocation.getPath ()Ljava/lang/String;
        //    INVOKESPECIAL net/minecraft/util/ResourceLocation.<init> (Ljava/lang/String;Ljava/lang/String;)V
        //    ALOAD 3
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelBlockDefinition.getMultipartData ()Lnet/minecraft/client/renderer/block/model/multipart/Multipart;
        //    INVOKESPECIAL net/minecraftforge/client/model/ModelLoader$MultipartModel.<init> (Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/block/model/multipart/Multipart;)V
        //    ASTORE 5
        //   L12
        //    LINENUMBER 1186 L12
        //    ALOAD 0
        //    GETFIELD net/minecraftforge/client/model/ModelLoader$VariantLoader.loader : Lnet/minecraftforge/client/model/ModelLoader;
        //    INVOKESTATIC net/minecraftforge/client/model/ModelLoader.access$1800 (Lnet/minecraftforge/client/model/ModelLoader;)Ljava/util/Map;
        //    ALOAD 3
        //    ALOAD 5
        //    INVOKEINTERFACE java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; (itf)
        //    POP
        //   L10
        //    LINENUMBER 1188 L10
        //   FRAME APPEND [net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException net/minecraftforge/client/model/IModel]
        //    ALOAD 5
        //    ARETURN
        //   L7
        //    LINENUMBER 1190 L7
        //   FRAME CHOP 1
        //    ALOAD 4
        //    ATHROW
        //   L13
        //    LOCALVARIABLE variants Lnet/minecraft/client/renderer/block/model/VariantList; L5 L2 4
        //    LOCALVARIABLE model Lnet/minecraftforge/client/model/IModel; L9 L7 5
        //    LOCALVARIABLE e Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException; L6 L13 4
        //    LOCALVARIABLE this Lnet/minecraftforge/client/model/ModelLoader$VariantLoader; L3 L13 0
        //    LOCALVARIABLE modelLocation Lnet/minecraft/util/ResourceLocation; L3 L13 1
        //    LOCALVARIABLE variant Lnet/minecraft/client/renderer/block/model/ModelResourceLocation; L4 L13 2
        //    LOCALVARIABLE definition Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition; L0 L13 3
        //    MAXSTACK = 6
        //    MAXLOCALS = 6

        private boolean foundFirstAStoreThree;
        private boolean injectedALoadZero;

        private LoadModelMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
            this.foundFirstAStoreThree = false;
            this.injectedALoadZero = false;
        }

        @Override
        public void visitVarInsn(final int opcode, final int var) {
            if (this.injectedALoadZero) {
                super.visitVarInsn(opcode, var);
                return;
            }
            if (this.foundFirstAStoreThree && opcode == Opcodes.ALOAD && var == 3) {
                L.i("Found target instruction 'ALOAD 3': injecting 'ALOAD 0'");
                super.visitVarInsn(Opcodes.ALOAD, 0);
                this.injectedALoadZero = true;
            }
            if (!this.foundFirstAStoreThree && opcode == Opcodes.ASTORE && var == 3) {
                L.i("Found marker 'ASTORE 3': enabling injection");
                this.foundFirstAStoreThree = true;
            }
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitMethodInsn(final int opcode, @Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc, final boolean itf) {
            if (opcode == Opcodes.INVOKEVIRTUAL && "net/minecraft/client/renderer/block/model/ModelBlockDefinition".equals(owner)
                    && "getVariant".equals(name) && "(Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;".equals(desc)) {
                L.i("Found target instruction 'INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelBlockDefinition.getVariant (Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;': replacing");
                super.visitMethodInsn(Opcodes.INVOKESPECIAL, THIS_CLASS_NAME, GET_VARIANT_BY_NAME_METHOD_NAME, GET_VARIANT_BY_NAME_METHOD_DESC, false);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class GetVariantByNameMethodVisitor extends MethodVisitor {
        //  // access flags 0x12
        //  private final <fermion-inject:getVariantByName>(Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition;Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList; throws java/lang/Exception
        //  @Ljavax/annotation/Nonnull;()
        //    // annotable parameter count: 2 (visible)
        //    @Ljavax/annotation/Nonnull;() // parameter 0
        //    @Ljavax/annotation/Nonnull;() // parameter 1
        //    TRYCATCHBLOCK L0 L1 L2 net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException
        //    TRYCATCHBLOCK L3 L4 L5 net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException
        //   L0
        //    LINENUMBER 1500 L0
        //    ALOAD 1
        //    ALOAD 2
        //    INVOKEVIRTUAL "net/minecraft/client/renderer/block/model/ModelBlockDefinition".getVariant (Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;
        //   L1
        //    ARETURN
        //   L2
        //    LINENUMBER 1501 L2
        //   FRAME SAME1 net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException
        //    ASTORE 3
        //   L6
        //    LINENUMBER 1502 L6
        //    LDC "normal"
        //    ALOAD 2
        //    INVOKEVIRTUAL java/lang/String.equals (Ljava/lang/Object;)Z
        //    IFEQ L7
        //   L3
        //    LINENUMBER 1503 L3
        //    ALOAD 1
        //    LDC ""
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelBlockDefinition.getVariant (Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;
        //    ASTORE 4
        //   L8
        //    LINENUMBER 1504 L8
        //    LDC "Boson ASM/ModelPatch"
        //    INVOKESTATIC org/apache/logging/log4j/LogManager.getLogger (Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;
        //    LDC "Successfully managed to load variant \"\" instead of expected \"normal\": probably a 1.13-format blockstate, accepting it anyway"
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.info (Ljava/lang/String;)V (itf)
        //   L9
        //    LINENUMBER 1505 L9
        //    ALOAD 4
        //   L4
        //    ARETURN
        //   L5
        //    LINENUMBER 1506 L5
        //   FRAME FULL [net/minecraftforge/client/model/ModelLoader$VariantLoader net/minecraft/client/renderer/block/model/ModelBlockDefinition java/lang/String net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException] [net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException]
        //    ASTORE 4
        //   L10
        //    LINENUMBER 1507 L10
        //    LDC "Boson ASM/ModelPatch"
        //    INVOKESTATIC org/apache/logging/log4j/LogManager.getLogger (Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;
        //    LDC "Missing both \"normal\" and \"\" variants: unable to get main block variant! Considering blockstate file as broken."
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.warn (Ljava/lang/String;)V (itf)
        //   L11
        //    LINENUMBER 1508 L11
        //    ALOAD 4
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException.getCause ()Ljava/lang/Throwable;
        //    IFNONNULL L12
        //   L13
        //    LINENUMBER 1509 L13
        //    ALOAD 4
        //    ALOAD 3
        //    INVOKEVIRTUAL net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException.initCause (Ljava/lang/Throwable;)Ljava/lang/Throwable;
        //    NOP
        //    POP
        //   L12
        //    LINENUMBER 1511 L12
        //   FRAME APPEND [net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException]
        //    ALOAD 4
        //    ATHROW
        //   L7
        //    LINENUMBER 1513 L7
        //   FRAME CHOP 1
        //    ALOAD 3
        //    ATHROW
        //   L14
        //    LOCALVARIABLE this Lnet/minecraftforge/client/model/ModelLoader$VariantLoader; L0 L14 0
        //    LOCALVARIABLE modelBlockDefinition Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition; L0 L14 1
        //    LOCALVARIABLE string Ljava/lang/String; L0 L14 2
        //    LOCALVARIABLE modelBlockDefinition$missingVariantException$1 Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException; L6 L14 3
        //    LOCALVARIABLE variantList Lnet/minecraft/client/renderer/block/model/VariantList; L8 L5 4
        //    LOCALVARIABLE modelBlockDefinition$missingVariantException$2 Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException; L10 L7 4
        //    MAXSTACK = 2
        //    MAXLOCALS = 5

        private GetVariantByNameMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitAnnotation("Ljavax/annotation/Nonnull;", true).visitEnd();
            super.visitParameterAnnotation(0, "Ljavax/annotation/Nonnull;", true).visitEnd();
            super.visitParameterAnnotation(1, "Ljavax/annotation/Nonnull;", true).visitEnd();

            super.visitCode();

            final Label l0 = new Label();
            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitTryCatchBlock(l0, l1, l2, "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException");

            final Label l3 = new Label();
            final Label l4 = new Label();
            final Label l5 = new Label();
            super.visitTryCatchBlock(l3, l4, l5, "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException");

            super.visitLabel(l0);
            super.visitLineNumber(1000 + 5 * 100, l0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/block/model/ModelBlockDefinition", "getVariant",
                    "(Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;", false);

            super.visitLabel(l1);
            super.visitInsn(Opcodes.ARETURN);

            super.visitLabel(l2);
            super.visitLineNumber(1000 + 5 * 100 + 1, l1);
            super.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException" });
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l6 = new Label();
            final Label l7 = new Label();
            super.visitLabel(l6);
            super.visitLineNumber(1000 + 5 * 100 + 2, l6);
            super.visitLdcInsn("normal");
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l7);

            super.visitLabel(l3);
            super.visitLineNumber(1000 + 5 * 100 + 3, l3);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitLdcInsn("");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/block/model/ModelBlockDefinition", "getVariant",
                    "(Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;", false);
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l8 = new Label();
            super.visitLabel(l8);
            super.visitLineNumber(1000 + 5 * 100 + 4, l8);
            super.visitLdcInsn("Boson ASM/ModelPatch");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/apache/logging/log4j/LogManager", "getLogger", "(Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;", false);
            super.visitLdcInsn("Successfully managed to load variant \"\" instead of expected \"normal\": probably a 1.13-format blockstate, accepting it anyway");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "info", "(Ljava/lang/String;)V", true);

            final Label l9 = new Label();
            super.visitLabel(l9);
            super.visitLineNumber(1000 + 5 * 100 + 5, l9);
            super.visitVarInsn(Opcodes.ALOAD, 4);

            super.visitLabel(l4);
            super.visitInsn(Opcodes.ARETURN);

            super.visitLabel(l5);
            super.visitLineNumber(1000 + 5 * 100 + 6, l5);
            super.visitFrame(Opcodes.F_FULL,
                    4, new Object[] { "net/minecraftforge/client/model/ModelLoader$VariantLoader", "net/minecraft/client/renderer/block/model/ModelBlockDefinition", "java/lang/String",
                            "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException" },
                    1, new Object[] { "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException" });
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l10 = new Label();
            super.visitLabel(l10);
            super.visitLineNumber(1000 + 5 * 100 + 7, l10);
            super.visitLdcInsn("Boson ASM/ModelPatch");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/apache/logging/log4j/LogManager", "getLogger", "(Ljava/lang/String;)Lorg/apache/logging/log4j/Logger;", false);
            super.visitLdcInsn("Missing both \"normal\" and \"\" variants: unable to get main block variant! Considering blockstate file as broken.");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "info", "(Ljava/lang/String;)V", true);

            final Label l11 = new Label();
            final Label l12 = new Label();
            super.visitLabel(l11);
            super.visitLineNumber(100 + 5 * 100 + 8, l11);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException", "getCause",
                    "()Ljava/lang/Throwable;", false);
            super.visitJumpInsn(Opcodes.IFNONNULL, l12);

            final Label l13 = new Label();
            super.visitLabel(l13);
            super.visitLineNumber(100 + 5 * 100 + 9, l13);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException", "initCause",
                    "(Ljava/lang/Throwable;)Ljava/lang/Throwable;", false);
            super.visitInsn(Opcodes.NOP);
            super.visitInsn(Opcodes.POP);

            super.visitLabel(l12);
            super.visitLineNumber(100 + 5 * 100 + 10 + 1, l12);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "net/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitInsn(Opcodes.ATHROW);

            super.visitLabel(l7);
            super.visitLineNumber(100 + 5 * 100 + 10 + 3, l13);
            super.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitInsn(Opcodes.ATHROW);

            final Label l14 = new Label();
            super.visitLabel(l14);
            super.visitLocalVariable("this", "Lnet/minecraftforge/client/model/ModelLoader$VariantLoader;", null, l0, l14, 0);
            super.visitLocalVariable("modelBlockDefinition", "Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition;", null, l0, l14, 1);
            super.visitLocalVariable("string", "Ljava/lang/String;", null, l0, l14, 2);
            super.visitLocalVariable("modelBlockDefinition$missingVariantException$1", "Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException;",
                    null, l6, l14, 3);
            super.visitLocalVariable("variantList", "Lnet/minecraft/client/renderer/block/model/VariantList;", null, l8, l5, 4);
            super.visitLocalVariable("modelBlockDefinition$missingVariantException$1", "Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition$MissingVariantException;",
                    null, l10, l7, 4);

            super.visitMaxs(2, 5);
            super.visitEnd();
        }
    }

    private static final Log L = Log.of("ModelLoader$VariantLoader");

    private static final String THIS_CLASS_NAME = "net/minecraftforge/client/model/ModelLoader$VariantLoader";
    private static final String GET_VARIANT_BY_NAME_METHOD_NAME = "fermion$$injected$$getVariantByName$$generated$$00_01_1122";
    private static final String GET_VARIANT_BY_NAME_METHOD_DESC = "(Lnet/minecraft/client/renderer/block/model/ModelBlockDefinition;Ljava/lang/String;)Lnet/minecraft/client/renderer/block/model/VariantList;";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ModelLoaderVariantLoaderTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("model_loader_variant_loader")
                        .setDescription("Transforms ModelLoader$VariantLoader to allow blockstates to specify \"\" as a variant instead of \"normal\"")
                        .build(),
                ClassDescriptor.of(THIS_CLASS_NAME)
        );
        LoadModelMethodVisitor.class.toString();
        GetVariantByNameMethodVisitor.class.toString();
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {

            @Override
            public MethodVisitor visitMethod(final int access, @Nonnull final String name, @Nonnull final String desc,
                                             @Nullable final String signature, @Nullable final String[] exceptions) {
                final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
                if ("loadModel".equals(name) && "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraftforge/client/model/IModel;".equals(desc)) {
                    L.i("Identified target method 'loadModel': patching");
                    return new LoadModelMethodVisitor(v, parent);
                }
                return parent;
            }

            @Override
            public void visitEnd() {
                L.i("Reached end of class: injecting private variant getter method");

                final MethodVisitor getVariantByName = new GetVariantByNameMethodVisitor(v, super.visitMethod(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                        GET_VARIANT_BY_NAME_METHOD_NAME,
                        GET_VARIANT_BY_NAME_METHOD_DESC,
                        null,
                        new String[] { "java/lang/Exception" }
                ));
                getVariantByName.visitCode();

                super.visitEnd();
            }
        };
    }
}
