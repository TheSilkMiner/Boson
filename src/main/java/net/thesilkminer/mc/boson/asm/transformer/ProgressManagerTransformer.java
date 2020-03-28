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
import java.util.function.BiFunction;

public final class ProgressManagerTransformer extends SingleTargetMethodTransformer {

    private static final Log LOGGER = Log.of("Progress Manager");

    public ProgressManagerTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("progress_manager")
                        .setDescription("This modifies the Progress Bar screen so that Boson can show a higher resolution and better refined Progress Bar while loading")
                        .build(),
                ClassDescriptor.of("net.minecraftforge.fml.common.ProgressManager"),
                MethodDescriptor.of(
                        "push",
                        ImmutableList.of(ClassDescriptor.of(String.class), ClassDescriptor.of(int.class), ClassDescriptor.of(boolean.class)),
                        ClassDescriptor.of("net.minecraftforge.fml.common.ProgressManager$ProgressBar")
                )
        );
    }

    @Nonnull
    @Override
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            @Override
            public void visitCode() {
                super.visitCode();

                LOGGER.i("Reached beginning of method: preparing to inject our hook");

                final Label l0 = new Label();
                super.visitLabel(l0);
                super.visitLineNumber(4 * 10 + 8, l0);
                super.visitVarInsn(Opcodes.ALOAD, 0);
                super.visitVarInsn(Opcodes.ILOAD, 1);
                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "net/thesilkminer/mc/boson/hook/ProgressManagerHook",
                        "hookFmlProgressBarCreation",
                        "(Ljava/lang/String;I)I",
                        false);
                super.visitVarInsn(Opcodes.ISTORE, 1);

                LOGGER.i("Successfully injected hook: now calling Kotlin function to edit steps");
            }
        };
    }
}
