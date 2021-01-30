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

package net.thesilkminer.mc.boson.asm.transformer.modfix;

import net.thesilkminer.mc.boson.asm.utility.Log;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractTransformer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public final class GuiIngredientTransformer extends AbstractTransformer {

    @SuppressWarnings("SpellCheckingInspection")
    private static final class DrawTooltipMethodVisitor extends MethodVisitor {
        //  // access flags 0x2
        //  // signature (Lnet/minecraft/client/Minecraft;IIIITT;)V
        //  // declaration: void drawTooltip(net.minecraft.client.Minecraft, int, int, int, int, T)
        //  private drawTooltip(Lnet/minecraft/client/Minecraft;IIIILjava/lang/Object;)V
        //    TRYCATCHBLOCK L0 L1 L2 java/lang/RuntimeException
        //   L0
        //    LINENUMBER 197 L0
        //    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.func_179097_i ()V
        //   L3
        //    LINENUMBER 199 L3
        //    INVOKESTATIC net/minecraft/client/renderer/RenderHelper.func_74518_a ()V
        //   L4
        //    LINENUMBER 200 L4
        //    ILOAD 2
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.rect : Ljava/awt/Rectangle;
        //    GETFIELD java/awt/Rectangle.x : I
        //    IADD
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.xPadding : I
        //    IADD
        //    ILOAD 3
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.rect : Ljava/awt/Rectangle;
        //    GETFIELD java/awt/Rectangle.y : I
        //    IADD
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.yPadding : I
        //    IADD
        //    ILOAD 2
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.rect : Ljava/awt/Rectangle;
        //    GETFIELD java/awt/Rectangle.x : I
        //    IADD
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.rect : Ljava/awt/Rectangle;
        //    GETFIELD java/awt/Rectangle.width : I
        //    IADD
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.xPadding : I
        //    ISUB
        //    ILOAD 3
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.rect : Ljava/awt/Rectangle;
        //    GETFIELD java/awt/Rectangle.y : I
        //    IADD
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.rect : Ljava/awt/Rectangle;
        //    GETFIELD java/awt/Rectangle.height : I
        //    IADD
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.yPadding : I
        //    ISUB
        //    LDC 2147483647
        //    INVOKESTATIC mezz/jei/gui/ingredients/GuiIngredient.func_73734_a (IIIII)V
        //   L5
        //    LINENUMBER 205 L5
        //    FCONST_1
        //    FCONST_1
        //    FCONST_1
        //    FCONST_1
        //    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.func_179131_c (FFFF)V
        //   L6
        //    LINENUMBER 207 L6
        //    ALOAD 1
        //    GETFIELD net/minecraft/client/Minecraft.field_71474_y : Lnet/minecraft/client/settings/GameSettings;
        //    GETFIELD net/minecraft/client/settings/GameSettings.field_82882_x : Z
        //    IFEQ L7
        //    GETSTATIC net/minecraft/client/util/ITooltipFlag$TooltipFlags.ADVANCED : Lnet/minecraft/client/util/ITooltipFlag$TooltipFlags;
        //    GOTO L8
        //   L7
        //   FRAME SAME
        //    GETSTATIC net/minecraft/client/util/ITooltipFlag$TooltipFlags.NORMAL : Lnet/minecraft/client/util/ITooltipFlag$TooltipFlags;
        //   L8
        //   FRAME SAME1 net/minecraft/client/util/ITooltipFlag$TooltipFlags
        //    ASTORE 7
        //   L9
        //    LINENUMBER 208 L9
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.ingredientRenderer : Lmezz/jei/api/ingredients/IIngredientRenderer;
        //    ALOAD 1
        //    ALOAD 6
        //    ALOAD 7
        //    INVOKEINTERFACE mezz/jei/api/ingredients/IIngredientRenderer.getTooltip (Lnet/minecraft/client/Minecraft;Ljava/lang/Object;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List; (itf)
        //    ASTORE 8
        //   L10
        //    LINENUMBER 209 L10
        //    INVOKESTATIC mezz/jei/startup/ForgeModIdHelper.getInstance ()Lmezz/jei/startup/IModIdHelper;
        //    ALOAD 8
        //    ALOAD 6
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.ingredientHelper : Lmezz/jei/api/ingredients/IIngredientHelper;
        //    INVOKEINTERFACE mezz/jei/startup/IModIdHelper.addModNameToIngredientTooltip (Ljava/util/List;Ljava/lang/Object;Lmezz/jei/api/ingredients/IIngredientHelper;)Ljava/util/List; (itf)
        //    ASTORE 8
        //   L11
        //    LINENUMBER 211 L11
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.tooltipCallback : Lmezz/jei/api/gui/ITooltipCallback;
        //    IFNULL L12
        //   L13
        //    LINENUMBER 212 L13
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.tooltipCallback : Lmezz/jei/api/gui/ITooltipCallback;
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.slotIndex : I
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.input : Z
        //    ALOAD 6
        //    ALOAD 8
        //    INVOKEINTERFACE mezz/jei/api/gui/ITooltipCallback.onTooltip (IZLjava/lang/Object;Ljava/util/List;)V (itf)
        //   L12
        //    LINENUMBER 215 L12
        //   FRAME APPEND [net/minecraft/client/util/ITooltipFlag$TooltipFlags java/util/List]
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.ingredientRenderer : Lmezz/jei/api/ingredients/IIngredientRenderer;
        //    ALOAD 1
        //    ALOAD 6
        //    INVOKEINTERFACE mezz/jei/api/ingredients/IIngredientRenderer.getFontRenderer (Lnet/minecraft/client/Minecraft;Ljava/lang/Object;)Lnet/minecraft/client/gui/FontRenderer; (itf)
        //    ASTORE 9
        //   L14
        //    LINENUMBER 216 L14
        //    ALOAD 6
        //    INSTANCEOF net/minecraft/item/ItemStack
        //    IFEQ L15
        //   L16
        //    LINENUMBER 218 L16
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.allIngredients : Ljava/util/List;
        //    ASTORE 10
        //   L17
        //    LINENUMBER 219 L17
        //    INVOKESTATIC mezz/jei/Internal.getStackHelper ()Lmezz/jei/startup/StackHelper;
        //    ALOAD 10
        //    INVOKEVIRTUAL mezz/jei/startup/StackHelper.getOreDictEquivalent (Ljava/util/Collection;)Ljava/lang/String;
        //    ASTORE 11
        //   L18
        //    LINENUMBER 220 L18
        //    ALOAD 11
        // <<< INJECTION BEGIN
        //    POP
        //    ACONST_NULL
        // >>> INJECTION END
        //    IFNULL L19
        //   L20
        //    LINENUMBER 221 L20
        //    GETSTATIC mezz/jei/gui/ingredients/GuiIngredient.oreDictionaryIngredient : Ljava/lang/String;
        //    ICONST_1
        //    ANEWARRAY java/lang/Object
        //    DUP
        //    ICONST_0
        //    ALOAD 11
        //    AASTORE
        //    INVOKESTATIC java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //    ASTORE 12
        //   L21
        //    LINENUMBER 222 L21
        //    ALOAD 8
        //    NEW java/lang/StringBuilder
        //    DUP
        //    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
        //    GETSTATIC net/minecraft/util/text/TextFormatting.GRAY : Lnet/minecraft/util/text/TextFormatting;
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    ALOAD 12
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
        //    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
        //    POP
        //   L19
        //    LINENUMBER 224 L19
        //   FRAME APPEND [net/minecraft/client/gui/FontRenderer java/util/Collection java/lang/String]
        // <<< INJECTION BEGIN
        //    ALOAD 0
        //    ALOAD 8
        //    ALOAD 6
        //    CHECKCAST net/minecraft/item/ItemStack
        //    ALOAD 11
        //    INVOKESPECIAL mezz/jei/gui/ingredients/GuiIngredient.<fermion-inject:appendTooltipInformation> (Ljava/util/List;Lnet/minecraft/item/ItemStack;Ljava/lang/String;)V
        // >>> INJECTION END
        //    ALOAD 6
        //    CHECKCAST net/minecraft/item/ItemStack
        //    ALOAD 1
        //    ALOAD 8
        //    ILOAD 2
        //    ILOAD 4
        //    IADD
        //    ILOAD 3
        //    ILOAD 5
        //    IADD
        //    ALOAD 9
        //    INVOKESTATIC mezz/jei/gui/TooltipRenderer.drawHoveringText (Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/Minecraft;Ljava/util/List;IILnet/minecraft/client/gui/FontRenderer;)V
        //   L22
        //    LINENUMBER 225 L22
        //    GOTO L23
        //   L15
        //    LINENUMBER 226 L15
        //   FRAME CHOP 2
        //    ALOAD 1
        //    ALOAD 8
        //    ILOAD 2
        //    ILOAD 4
        //    IADD
        //    ILOAD 3
        //    ILOAD 5
        //    IADD
        //    ALOAD 9
        //    INVOKESTATIC mezz/jei/gui/TooltipRenderer.drawHoveringText (Lnet/minecraft/client/Minecraft;Ljava/util/List;IILnet/minecraft/client/gui/FontRenderer;)V
        //   L23
        //    LINENUMBER 229 L23
        //   FRAME SAME
        //    INVOKESTATIC net/minecraft/client/renderer/GlStateManager.func_179126_j ()V
        //   L1
        //    LINENUMBER 232 L1
        //    GOTO L24
        //   L2
        //    LINENUMBER 230 L2
        //   FRAME FULL [mezz/jei/gui/ingredients/GuiIngredient net/minecraft/client/Minecraft I I I I java/lang/Object] [java/lang/RuntimeException]
        //    ASTORE 7
        //   L25
        //    LINENUMBER 231 L25
        //    INVOKESTATIC mezz/jei/util/Log.get ()Lorg/apache/logging/log4j/Logger;
        //    LDC "Exception when rendering tooltip on {}."
        //    ALOAD 6
        //    ALOAD 7
        //    INVOKEINTERFACE org/apache/logging/log4j/Logger.error (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V (itf)
        //   L24
        //    LINENUMBER 233 L24
        //   FRAME SAME
        //    RETURN
        //   L26
        //    LOCALVARIABLE acceptsAny Ljava/lang/String; L21 L19 12
        //    LOCALVARIABLE itemStacks Ljava/util/Collection; L17 L22 10
        //    // signature Ljava/util/Collection<Lnet/minecraft/item/ItemStack;>;
        //    // declaration: itemStacks extends java.util.Collection<net.minecraft.item.ItemStack>
        //    LOCALVARIABLE oreDictEquivalent Ljava/lang/String; L18 L22 11
        //    LOCALVARIABLE tooltipFlag Lnet/minecraft/client/util/ITooltipFlag$TooltipFlags; L9 L1 7
        //    LOCALVARIABLE tooltip Ljava/util/List; L10 L1 8
        //    // signature Ljava/util/List<Ljava/lang/String;>;
        //    // declaration: tooltip extends java.util.List<java.lang.String>
        //    LOCALVARIABLE fontRenderer Lnet/minecraft/client/gui/FontRenderer; L14 L1 9
        //    LOCALVARIABLE e Ljava/lang/RuntimeException; L25 L24 7
        //    LOCALVARIABLE this Lmezz/jei/gui/ingredients/GuiIngredient; L0 L26 0
        //    // signature Lmezz/jei/gui/ingredients/GuiIngredient<TT;>;
        //    // declaration: this extends mezz.jei.gui.ingredients.GuiIngredient<T>
        //    LOCALVARIABLE minecraft Lnet/minecraft/client/Minecraft; L0 L26 1
        //    LOCALVARIABLE xOffset I L0 L26 2
        //    LOCALVARIABLE yOffset I L0 L26 3
        //    LOCALVARIABLE mouseX I L0 L26 4
        //    LOCALVARIABLE mouseY I L0 L26 5
        //    LOCALVARIABLE value Ljava/lang/Object; L0 L26 6
        //    // signature TT;
        //    // declaration: value extends T
        //    MAXSTACK = 6
        //    MAXLOCALS = 13

        private boolean hasInvokedGetOreDictionaryEquivalent;
        private boolean hasInjectedAfterOreDictionaryEquivalent;
        private boolean hasFoundAddToList;
        private boolean hasFoundFrameAppend;
        private boolean hasInjectedBeforeALoad;

        private DrawTooltipMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitMethodInsn(final int opcode, @Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc, final boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);

            if (Opcodes.INVOKEVIRTUAL == opcode && "mezz/jei/startup/StackHelper".equals(owner) && "getOreDictEquivalent".equals(name) &&
                    "(Ljava/util/Collection;)Ljava/lang/String;".equals(desc)) {
                this.hasInvokedGetOreDictionaryEquivalent = true;
            }

            if (Opcodes.INVOKEINTERFACE == opcode && "java/util/List".equals(owner) && "add".equals(name) && "(Ljava/lang/Object;)Z".equals(desc)
                    && this.hasInvokedGetOreDictionaryEquivalent) {
                this.hasFoundAddToList = true;
            }
        }

        @Override
        public void visitVarInsn(final int opcode, final int var) {
            if (opcode == Opcodes.ALOAD && var == 6 && this.hasFoundAddToList && this.hasFoundFrameAppend && !this.hasInjectedBeforeALoad) {
                this.hasInjectedBeforeALoad = true;
                super.visitVarInsn(Opcodes.ALOAD, 0);
                super.visitVarInsn(Opcodes.ALOAD, 8);
                super.visitVarInsn(Opcodes.ALOAD, 6);
                super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/item/ItemStack");
                super.visitVarInsn(Opcodes.ALOAD, 10 + 1);
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, THIS, APPEND_TOOLTIP_INFORMATION_METHOD_NAME, APPEND_TOOLTIP_INFORMATION_METHOD_DESC, false);
            }

            super.visitVarInsn(opcode, var);

            if (opcode == Opcodes.ALOAD && var == 10 + 1 && this.hasInvokedGetOreDictionaryEquivalent && !this.hasInjectedAfterOreDictionaryEquivalent) {
                this.hasInjectedAfterOreDictionaryEquivalent = true;
                super.visitInsn(Opcodes.POP);
                super.visitInsn(Opcodes.ACONST_NULL);
            }
        }

        @Override
        public void visitFrame(final int type, final int nLocal, @Nullable final Object[] local, final int nStack, @Nullable final Object[] stack) {
            super.visitFrame(type, nLocal, local, nStack, stack);

            if (Opcodes.F_APPEND == type && nLocal == 3 && this.hasFoundAddToList && !this.hasFoundFrameAppend) {
                this.hasFoundFrameAppend = true;
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class AppendTooltipInformationMethodVisitor extends MethodVisitor {
        //  // access flags 0x2
        //  // signature (Ljava/util/List<Ljava/lang/String;>;Lnet/minecraft/item/ItemStack;Ljava/lang/String;)V
        //  // declaration: void <fermion-inject:appendTooltipInformation>(java.util.List<String>, net.minecraft.item.ItemStack, String)
        //  private <fermion-inject:appendTooltipInformation>(Ljava/util/List;Lnet/minecraft/item/ItemStack;Ljava/lang/String;)V
        //   L0
        //    LINENUMBER 245 L0
        //    ALOAD 0
        //    ALOAD 0
        //    GETFIELD mezz/jei/gui/ingredients/GuiIngredient.allIngredients : Ljava/util/List;
        //    INVOKESPECIAL mezz/jei/gui/ingredients/GuiIngredient.<fermion-inject:getTagEquivalent> (Ljava/util/Collection;)Ljava/lang/String;
        //    ASTORE 4
        //   L1
        //    LINENUMBER 246 L1
        //    ALOAD 3
        //    IFNONNULL L2
        //    ALOAD 4
        //    IFNONNULL L2
        //   L3
        //    LINENUMBER 246 L3
        //    RETURN
        //   L2
        //    LINENUMBER 247 L2
        //   FRAME APPEND [java/lang/String]
        //    ALOAD 1
        //    NEW java/lang/StringBuilder
        //    DUP
        //    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
        //    GETSTATIC net/minecraft/util/text/TextFormatting.GRAY : Lnet/minecraft/util/text/TextFormatting;
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    GETSTATIC mezz/jei/gui/ingredients/GuiIngredient.oreDictionaryIngredient : Ljava/lang/String;
        //    ICONST_1
        //    ANEWARRAY java/lang/Object
        //    DUP
        //    ICONST_0
        //    LDC ""
        //    AASTORE
        //    INVOKESTATIC java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
        //    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
        //    POP
        //   L4
        //    LINENUMBER 248 L4
        //    ALOAD 3
        //    IFNULL L5
        //   L6
        //    LINENUMBER 248 L6
        //    ALOAD 1
        //    NEW java/lang/StringBuilder
        //    DUP
        //    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
        //    GETSTATIC net/minecraft/util/text/TextFormatting.GRAY : Lnet/minecraft/util/text/TextFormatting;
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    LDC "- "
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    LDC "jei.tooltip.recipe.ore.dict.dictionary"
        //    INVOKESTATIC mezz/jei/util/Translator.translateToLocal (Ljava/lang/String;)Ljava/lang/String;
        //    ICONST_1
        //    ANEWARRAY java/lang/Object
        //    DUP
        //    ICONST_0
        //    ALOAD 3
        //    AASTORE
        //    INVOKESTATIC java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
        //    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
        //    POP
        //   L5
        //    LINENUMBER 249 L5
        //   FRAME SAME
        //    ALOAD 4
        //    IFNULL L7
        //   L8
        //    LINENUMBER 249 L8
        //    ALOAD 1
        //    NEW java/lang/StringBuilder
        //    DUP
        //    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
        //    GETSTATIC net/minecraft/util/text/TextFormatting.GRAY : Lnet/minecraft/util/text/TextFormatting;
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    LDC "- "
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    LDC "jei.tooltip.recipe.ore.dict.tag"
        //    INVOKESTATIC mezz/jei/util/Translator.translateToLocal (Ljava/lang/String;)Ljava/lang/String;
        //    ICONST_1
        //    ANEWARRAY java/lang/Object
        //    DUP
        //    ICONST_0
        //    ALOAD 4
        //    AASTORE
        //    INVOKESTATIC java/lang/String.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
        //    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
        //    POP
        //   L7
        //    LINENUMBER 250 L7
        //   FRAME SAME
        //    NOP
        //    RETURN
        //   L9
        //    LOCALVARIABLE this Lmezz/jei/gui/ingredients/GuiIngredient; L0 L9 0
        //    LOCALVARIABLE list Ljava/util/List; L0 L9 1
        //    // signature Ljava/util/List<Ljava/lang/String;>;
        //    // declaration: list extends java.util.List<java.lang.String>
        //    LOCALVARIABLE stack Lnet/minecraft/item/ItemStack; L0 L9 2
        //    LOCALVARIABLE string1 Ljava/lang/String; L0 L9 3
        //    LOCALVARIABLE string2 Ljava/lang/String; L1 L9 4
        //    MAXSTACK = 7
        //    MAXLOCALS = 5

        private AppendTooltipInformationMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(2 * 100 + 4 * 10 + 5, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, THIS, "allIngredients", "Ljava/util/List;");
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, THIS, GET_TAG_EQUIVALENT_METHOD_NAME, GET_TAG_EQUIVALENT_METHOD_DESC, false);
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(2 * 100 + 4 * 10 + 6, l1);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitJumpInsn(Opcodes.IFNONNULL, l2);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitJumpInsn(Opcodes.IFNONNULL, l2);

            final Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(2 * 100 + 4 * 10 + 6, l3);
            super.visitInsn(Opcodes.RETURN);

            super.visitLabel(l2);
            super.visitLineNumber(2 * 100 + 4 * 10 + 7, l2);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/String" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/util/text/TextFormatting", "GRAY", "Lnet/minecraft/util/text/TextFormatting;");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            super.visitFieldInsn(Opcodes.GETSTATIC, THIS, "oreDictionaryIngredient", "Ljava/lang/String;");
            super.visitInsn(Opcodes.ICONST_1);
            super.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
            super.visitInsn(Opcodes.DUP);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitLdcInsn("");
            super.visitInsn(Opcodes.AASTORE);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            super.visitInsn(Opcodes.POP);

            final Label l4 = new Label();
            final Label l5 = new Label();
            super.visitLabel(l4);
            super.visitLineNumber(2 * 100 + 4 * 10 + 8, l4);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitJumpInsn(Opcodes.IFNULL, l5);

            final Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLineNumber(2 * 100 + 4 * 10 + 8, l5);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/util/text/TextFormatting", "GRAY", "Lnet/minecraft/util/text/TextFormatting;");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            super.visitLdcInsn("- ");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitLdcInsn("jei.tooltip.recipe.ore.dict.dictionary");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "mezz/jei/util/Translator", "translateToLocal", "(Ljava/lang/String;)Ljava/lang/String;", false);
            super.visitInsn(Opcodes.ICONST_1);
            super.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
            super.visitInsn(Opcodes.DUP);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitInsn(Opcodes.AASTORE);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            super.visitInsn(Opcodes.POP);

            final Label l7 = new Label();
            super.visitLabel(l5);
            super.visitLineNumber(2 * 100 + 4 * 10 + 9, l5);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitJumpInsn(Opcodes.IFNULL, l7);

            final Label l8 = new Label();
            super.visitLabel(l8);
            super.visitLineNumber(2 * 100 + 4 * 10 + 9, l8);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraft/util/text/TextFormatting", "GRAY", "Lnet/minecraft/util/text/TextFormatting;");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            super.visitLdcInsn("- ");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitLdcInsn("jei.tooltip.recipe.ore.dict.tag");
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "mezz/jei/util/Translator", "translateToLocal", "(Ljava/lang/String;)Ljava/lang/String;", false);
            super.visitInsn(Opcodes.ICONST_1);
            super.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
            super.visitInsn(Opcodes.DUP);
            super.visitInsn(Opcodes.ICONST_0);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitInsn(Opcodes.AASTORE);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            super.visitInsn(Opcodes.POP);

            super.visitLabel(l7);
            super.visitLineNumber(2 * 100 + 5 * 10, l7);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitInsn(Opcodes.NOP);
            super.visitInsn(Opcodes.RETURN);

            final Label l9 = new Label();
            super.visitLabel(l9);

            super.visitLocalVariable("this", "L" + THIS + ";", null, l0, l9, 0);
            super.visitLocalVariable("list", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/String;>;", l0, l9, 2);
            super.visitLocalVariable("stack", "Lnet/minecraft/item/ItemStack;", null, l0, l9, 2);
            super.visitLocalVariable("string1", "Ljava/lang/String;", null, l0, l9, 3);
            super.visitLocalVariable("string2", "Ljava/lang/String;", null, l1, l9, 4);

            super.visitMaxs(7, 5);
            super.visitEnd();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static final class GetTagEquivalentMethodVisitor extends MethodVisitor {
        //  // access flags 0x2
        //  // signature (Ljava/util/Collection<Lnet/minecraft/item/ItemStack;>;)Ljava/lang/String;
        //  // declaration: String <fermion-inject:getTagEquivalent>(java.util.Collection<net.minecraft.item.ItemStack>)
        //  private <fermion-inject:getTagEquivalent>(Ljava/util/Collection;)Ljava/lang/String;
        //  @Ljavax/annotation/Nullable;()
        //   L0
        //    LINENUMBER 254 L0
        //    INVOKESTATIC mezz/jei/Internal.getStackHelper ()Lmezz/jei/startup/StackHelper;
        //    ASTORE 2
        //   L1
        //    LINENUMBER 255 L1
        //    ALOAD 1
        //    INVOKEINTERFACE java/util/Collection.size ()I (itf)
        //    ICONST_2
        //    IF_ICMPGE L2
        //   L3
        //    LINENUMBER 255 L3
        //    ACONST_NULL
        //    ARETURN
        //   L2
        //    LINENUMBER 256 L2
        //   FRAME APPEND [mezz/jei/startup/StackHelper]
        //    ALOAD 1
        //    INVOKEINTERFACE java/util/Collection.iterator ()Ljava/util/Iterator; (itf)
        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
        //    CHECKCAST net/minecraft/item/ItemStack
        //    ASTORE 3
        //   L4
        //    LINENUMBER 257 L4
        //    ALOAD 3
        //    IFNULL L5
        //   L6
        //    LINENUMBER 258 L6
        //    INVOKESTATIC net/thesilkminer/mc/boson/api/ApiBindings.getBosonApi ()Lnet/thesilkminer/mc/boson/api/BosonApi;
        //    INVOKEINTERFACE net/thesilkminer/mc/boson/api/BosonApi.getTagRegistry ()Lnet/thesilkminer/mc/boson/api/tag/TagRegistry; (itf)
        //    INVOKESTATIC net/thesikminer/mc/boson/prefab/tag/CTT.getItemTagType ()Lnet/thesilkminer/mc/boson/api/tag/TagType;
        //    INVOKEINTERFACE net/thesilkminer/mc/boson/api/tag/TagRegistry.findAllTagsOf (Lnet/thesilkminer/mc/boson/api/tag/TagType;)Ljava/util/List; (itf)
        //    ASTORE 4
        //   L7
        //    LINENUMBER 259 L7
        //    NEW java/util/ArrayList
        //    DUP
        //    INVOKESPECIAL java/util/ArrayList.<init> ()V
        //    CHECKCAST java/util/List
        //    ASTORE 5
        //   L8
        //    LINENUMBER 260 L8
        //    ALOAD 4
        //    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator; (itf)
        //    ASTORE 6
        //   L9
        //    LINENUMBER 261 L9
        //   FRAME FULL [mezz/jei/gui/ingredients/GuiIngredient java/util/Collection mezz/jei/startup/StackHelper net/minecraft/item/ItemStack java/util/List java/util/List java/util/Iterator] []
        //    NOP
        //    ALOAD 6
        //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
        //    IFEQ L10
        //    ALOAD 6
        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
        //    CHECKCAST net/thesilkminer/mc/boson/api/tag/Tag
        //    ASTORE 7
        //   L11
        //    LINENUMBER 262 L11
        //    ALOAD 7
        //    ALOAD 3
        //    INVOKESTATIC net/thesilkminer/mc/boson/prefab/tag/TCTTC.has (Lnet/thesilkminer/mc/boson/api/tag/Tag;Lnet/minecraft/item/ItemStack;)Z
        //    IFEQ L12
        //   L13
        //    LINENUMBER 262 L13
        //    ALOAD 5
        //    ALOAD 7
        //    INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
        //    POP
        //   L12
        //    LINENUMBER 263 L12
        //   FRAME SAME
        //    GOTO L9
        //   L10
        //    LINENUMBER 264 L10
        //   FRAME CHOP 1
        //    ALOAD 5
        //    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator; (itf)
        //    ASTORE 6
        //   L14
        //    LINENUMBER 264 L14
        //   FRAME APPEND [java/util/Iterator]
        //    ALOAD 6
        //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
        //    IFEQ L5
        //    ALOAD 6
        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Objext; (itf)
        //    CHECKCAST net/thesilkminer/mc/boson/api/tag/Tag
        //    ASTORE 7
        //   L15
        //    LINENUMBER 265 L15
        //    ALOAD 2
        //    ALOAD 7
        //    INVOKEINTERFACE net/thesilkminer/mc/boson/api/tag/Tag.getElements ()Ljava/util/Set; (itf)
        //    INVOKEVIRTUAL mezz/jei/startup/StackHelper.getAllSubtypes (Ljava/lang/Iterable;)Ljava/util/List;
        //    ASTORE 8
        //   L16
        //    LINENUMBER 266 L16
        //    ALOAD 2
        //    ALOAD 1
        //    ALOAD 8
        //    INVOKEVIRTUAL mezz/jei/startup/StackHelper.containsSameStacks (Ljava/util/Collection;Ljava/util/Collection;)Z
        //    IFEQ L17
        //   L18
        //    LINENUMBER 267 L18
        //    NEW java/lang/StringBuilder
        //    DUP
        //    LDC "#"
        //    INVOKESPECIAL java/lang/StringBuilder.<init> (Ljava/lang/String;)V
        //    ALOAD 7
        //    INVOKEINTERFACE net/thesilkminer/mc/boson/api/tag/Tag.getName ()Lnet/thesilkminer/mc/boson/api/id/NameSpacedString; (itf)
        //    CHECKCAST java/lang/Object
        //    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/Object;)Ljava/lang/StringBuilder;
        //    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
        //    ARETURN
        //   L17
        //    LINENUMBER 269 L17
        //   FRAME SAME
        //    GOTO L14
        //   L5
        //    LINENUMBER 271 L5
        //   FRAME CHOP 3
        //    ACONST_NULL
        //    ARETURN
        //   L19
        //    LOCALVARIABLE this Lmezz/jei/gui/ingredients/GuiIngredient; L0 L19 0
        //    LOCALVARIABLE collection Ljava/util/Collection; L0 L19 1
        //    // signature Ljava/util/Collection<Lnet/minecraft/item/ItemStack;>;
        //    // declaration: collection extends java.util.Collection<net.minecraft.item.ItemStack>
        //    LOCALVARIABLE stackHelper Lmezz/jei/startup/StackHelper; L1 L19 2
        //    LOCALVARIABLE itemStack Lnet/minecraft/item/ItemStack; L4 L19 3
        //    LOCALVARIABLE list1 Ljava/util/List; L7 L5 4
        //    // signature Ljava/util/List<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;
        //    // declaration: list1 extends java.util.List<net.thesilkminer.mc.boson.api.tag.Tag<net.minecraft.item.ItemStack>>
        //    LOCALVARIABLE list2 Ljava/util/List; L8 L5 5
        //    // signature Ljava/util/List<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;
        //    // declaration: list2 extends java.util.List<net.thesilkminer.mc.boson.api.tag.Tag<net.minecraft.item.ItemStack>>
        //    LOCALVARIABLE iterator1 Ljava/util/Iterator; L9 L10 6
        //    // signature Ljava/util/Iterator<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;
        //    // declaration: iterator1 extends java.util.Iterator<net.thesilkminer.mc.boson.api.tag.Tag<net.minecraft.item.ItemStack>>
        //    LOCALVARIABLE iterator2 Ljava/util/Iterator; L14 L5 6
        //    // signature Ljava/util/Iterator<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;
        //    // declaration: iterator2 extends java.util.Iterator<net.thesilkminer.mc.boson.api.tag.Tag<net.minecraft.item.ItemStack>>
        //    LOCALVARIABLE tag1 Lnet/thesilkminer/mc/boson/api/tag/Tag; L11 L12 7
        //    // signature Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;
        //    // declaration: tag1 extends net.thesilkminer.mc.boson.api.tag.Tag<net.minecraft.item.ItemStack>
        //    LOCALVARIABLE tag2 Lnet/thesilkminer/mc/boson/api/tag/Tag; L15 L17 7
        //    // signature Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;
        //    // declaration: tag2 extends net.thesilkminer.mc.boson.api.tag.Tag<net.minecraft.item.ItemStack>
        //    LOCALVARIABLE list3 Ljava/util/List; L16 L17 8
        //    // signature Ljava/util/List<Lnet/minecraft/item/ItemStack;>;
        //    // declaration: list3 extends java.util.List<net.minecraft.item.ItemStack>
        //    MAXSTACK = 4
        //    MAXLOCALS = 9

        private GetTagEquivalentMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            final AnnotationVisitor nullable = super.visitAnnotation("Ljavax/annotation/Nullable;", true);
            nullable.visitEnd();

            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(2 * 100 + 5 * 10 + 4, l0);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "mezz/jei/Internal", "getStackHelper", "()Lmezz/jei/startup/StackHelper;", false);
            super.visitVarInsn(Opcodes.ASTORE, 2);

            final Label l1 = new Label();
            final Label l2 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(2 * 100 + 5 * 10 + 5, l1);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Collection", "size", "()I", true);
            super.visitInsn(Opcodes.ICONST_2);
            super.visitJumpInsn(Opcodes.IF_ICMPGE, l2);

            final Label l3 = new Label();
            super.visitLabel(l3);
            super.visitLineNumber(2 * 100 + 5 * 10 + 5, l3);
            super.visitInsn(Opcodes.ACONST_NULL);
            super.visitInsn(Opcodes.ARETURN);

            super.visitLabel(l2);
            super.visitLineNumber(2 * 100 + 5 * 10 + 6, l2);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "mezz/jei/startup/StackHelper" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;", true);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/item/ItemStack");
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l4 = new Label();
            final Label l5 = new Label();
            super.visitLabel(l4);
            super.visitLineNumber(2 * 100 + 5 * 10 + 7, l4);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitJumpInsn(Opcodes.IFNULL, l5);

            final Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLineNumber(2 * 100 + 5 * 10 + 8, l6);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/api/ApiBindings", "getBosonApi", "()Lnet/thesilkminer/mc/boson/api/BosonApi;", false);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "net/thesilkminer/mc/boson/api/BosonApi", "getTagRegistry", "()Lnet/thesilkminer/mc/boson/api/tag/TagRegistry;", true);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/prefab/tag/CTT", "getItemTagType", "()Lnet/thesilkminer/mc/boson/api/tag/TagType;", false);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "net/thesilkminer/mc/boson/api/tag/TagRegistry", "findAllTagsOf", "(Lnet/thesilkminer/mc/boson/api/tag/TagType;)Ljava/util/List;", true);
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l7 = new Label();
            super.visitLabel(l7);
            super.visitLineNumber(2 * 100 + 5 * 10 + 9, l7);
            super.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/List");
            super.visitVarInsn(Opcodes.ASTORE, 5);

            final Label l8 = new Label();
            super.visitLabel(l8);
            super.visitLineNumber(2 * 100 + 6 * 10, l8);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            super.visitVarInsn(Opcodes.ASTORE, 6);

            final Label l9 = new Label();
            final Label l10 = new Label();
            super.visitLabel(l9);
            super.visitLineNumber(2 * 100 + 6 * 10 + 1, l9);
            super.visitFrame(Opcodes.F_FULL, 7, new Object[] { THIS, "java/util/Collection", "mezz/jei/startup/StackHelper", "net/minecraft/item/ItemStack", "java/util/List", "java/util/List", "java/util/Iterator" }, 0, new Object[] { });
            super.visitInsn(Opcodes.NOP);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE,"java/util/Iterator", "hasNext", "()Z", true);
            super.visitJumpInsn(Opcodes.IFEQ, l10);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/thesilkminer/mc/boson/api/tag/Tag");
            super.visitVarInsn(Opcodes.ASTORE, 7);

            final Label l11 = new Label();
            final Label l12 = new Label();
            super.visitLabel(l11);
            super.visitLineNumber(2 * 100 + 6 * 10 + 2, l11);
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/prefab/tag/TCTTC", "has", "(Lnet/thesilkminer/mc/boson/api/tag/Tag;Lnet/minecraft/item/ItemStack;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l12);

            final Label l13 = new Label();
            super.visitLabel(l13);
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
            super.visitInsn(Opcodes.POP);

            super.visitLabel(l12);
            super.visitLineNumber(2 * 100 + 6 * 10 + 3, l12);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitJumpInsn(Opcodes.GOTO, l9);

            super.visitLabel(l10);
            super.visitLineNumber(2 * 100 + 6 * 10 + 4, l10);
            super.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
            super.visitVarInsn(Opcodes.ASTORE, 6);

            final Label l14 = new Label();
            super.visitLabel(l14);
            super.visitLineNumber(2 * 100 + 6 * 10 + 4, l14);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/Iterator" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            super.visitJumpInsn(Opcodes.IFEQ, l5);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/thesilkminer/mc/boson/api/tag/Tag");
            super.visitVarInsn(Opcodes.ASTORE, 7);

            final Label l15 = new Label();
            super.visitLabel(l15);
            super.visitLineNumber(2 * 100 + 6 * 10 + 5, l15);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "net/thesilkminer/mc/boson/api/tag/Tag", "getElements", "()Ljava/util/Set;", true);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "mezz/jei/startup/StackHelper", "getAllSubtypes", "(Ljava/lang/Iterable;)Ljava/util/List;", false);
            super.visitVarInsn(Opcodes.ASTORE, 8);

            final Label l16 = new Label();
            final Label l17 = new Label();
            super.visitLabel(l16);
            super.visitLineNumber(2 * 100 + 6 * 10 + 6, l16);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitVarInsn(Opcodes.ALOAD, 8);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "mezz/jei/startup/StackHelper", "containsSameStacks", "(Ljava/util/Collection;Ljava/util/Collection;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l17);

            final Label l18 = new Label();
            super.visitLabel(l18);
            super.visitLineNumber(2 * 100 + 6 * 10 + 7, l18);
            super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            super.visitInsn(Opcodes.DUP);
            super.visitLdcInsn("#");
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "net/thesilkminer/mc/boson/api/tag/Tag", "getName", "()Lnet/thesilkminer/mc/boson/api/id/NameSpacedString;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            super.visitInsn(Opcodes.ARETURN);

            super.visitLabel(l17);
            super.visitLineNumber(2 * 100 + 6 * 10 + 9, l17);
            super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            super.visitJumpInsn(Opcodes.GOTO, l14);

            super.visitLabel(l5);
            super.visitLineNumber(2 * 100 + 7 * 10 + 1, l5);
            super.visitFrame(Opcodes.F_CHOP, 3, null, 0, null);
            super.visitInsn(Opcodes.ACONST_NULL);
            super.visitInsn(Opcodes.ARETURN);

            final Label l19 = new Label();
            super.visitLabel(l19);

            super.visitLocalVariable("this", "L" + THIS + ";", null, l0, l19, 0);
            super.visitLocalVariable("collection", "Ljava/util/Collection;", "Ljava/util/Collection<Lnet/minecraft/item/ItemStack;>;", l0, l19, 1);
            super.visitLocalVariable("stackHelper", "Lmezz/jei/startup/StackHelper;", null, l1, l19, 2);
            super.visitLocalVariable("itemStack", "Lnet/minecraft/item/ItemStack;", null, l4, l19, 3);
            super.visitLocalVariable("list1", "Ljava/util/List;", "Ljava/util/List<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;", l7, l5, 4);
            super.visitLocalVariable("list2", "Ljava/util/List;", "Ljava/util/List<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;", l8, l5, 5);
            super.visitLocalVariable("iterator1", "Ljava/util/Iterator;", "Ljava/util/Iterator<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;", l9, l10, 6);
            super.visitLocalVariable("iterator2", "Ljava/util/Iterator;", "Ljava/util/Iterator<Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;>;", l14, l5, 6);
            super.visitLocalVariable("tag1", "Lnet/thesilkminer/mc/boson/api/tag/Tag;", "Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;", l11, l12, 7);
            super.visitLocalVariable("tag2", "Lnet/thesilkminer/mc/boson/api/tag/Tag;", "Lnet/thesilkminer/mc/boson/api/tag/Tag<Lnet/minecraft/item/ItemStack;>;", l15, l17, 7);
            super.visitLocalVariable("list3", "Ljava/util/List;", "Ljava/util/List<Lnet/minecraft/item/ItemStack;>;", l16, l17, 8);

            super.visitMaxs(4,9);
            super.visitEnd();
        }
    }

    private static final String THIS = "mezz/jei/gui/ingredients/GuiIngredient";
    private static final String APPEND_TOOLTIP_INFORMATION_METHOD_NAME = "fermion$$injected$$appendTooltipInformation$$generated$$00_64_1122";
    private static final String APPEND_TOOLTIP_INFORMATION_METHOD_DESC = "(Ljava/util/List;Lnet/minecraft/item/ItemStack;Ljava/lang/String;)V";
    private static final String GET_TAG_EQUIVALENT_METHOD_NAME = "fermion$$injected$$getTagEquivalent$$generated$$00_78_1122";
    private static final String GET_TAG_EQUIVALENT_METHOD_DESC = "(Ljava/util/Collection;)Ljava/lang/String;";

    private static final Log LOG = Log.of("GUI Ingredient");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public GuiIngredientTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("gui_ingredient")
                        .setDescription("Fixes JEI 'Accepts any' text in ore-dictionary enabled recipes to also consider tags")
                        .build(),
                ClassDescriptor.of(THIS)
        );
        DrawTooltipMethodVisitor.class.toString();
        AppendTooltipInformationMethodVisitor.class.toString();
        GetTagEquivalentMethodVisitor.class.toString();
    }

    @Nonnull
    @Override
    @SuppressWarnings("SpellCheckingInspection")
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            public MethodVisitor visitMethod(final int access, @Nonnull final String name, @Nonnull final String desc,
                                             @Nullable final String signature, @Nullable final String[] exceptions) {
                final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
                if ("drawTooltip".equals(name) && "(Lnet/minecraft/client/Minecraft;IIIILjava/lang/Object;)V".equals(desc)) {
                    LOG.i("Found 'drawTooltip(Lnet/minecraft/client/Minecraft;IIIILjava/lang/Object;)V': transforming method");
                    return new DrawTooltipMethodVisitor(v, parent);
                }
                return parent;
            }

            @Override
            public void visitEnd() {
                LOG.i("End of class: injecting methods '" + APPEND_TOOLTIP_INFORMATION_METHOD_NAME + "' and '" + GET_TAG_EQUIVALENT_METHOD_NAME + "'");

                final MethodVisitor appendTooltipInfoMethod = super.visitMethod(Opcodes.ACC_PRIVATE, APPEND_TOOLTIP_INFORMATION_METHOD_NAME,
                        APPEND_TOOLTIP_INFORMATION_METHOD_DESC, "(Ljava/util/List<Ljava/lang/String;>;Lnet/minecraft/item/ItemStack;Ljava/lang/String;)V", null);
                final MethodVisitor appendTooltipVisitor = new AppendTooltipInformationMethodVisitor(v, appendTooltipInfoMethod);
                appendTooltipVisitor.visitCode();

                final MethodVisitor getTagEquivalentMethod = super.visitMethod(Opcodes.ACC_PRIVATE, GET_TAG_EQUIVALENT_METHOD_NAME,
                        GET_TAG_EQUIVALENT_METHOD_DESC, "(Ljava/util/Collection<Lnet/minecraft/item/ItemStack;>;)Ljava/lang/String;",null);
                final MethodVisitor getTagEquivalentVisitor = new GetTagEquivalentMethodVisitor(v, getTagEquivalentMethod);
                getTagEquivalentVisitor.visitCode();

                super.visitEnd();
            }
        };
    }
}
