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

package net.thesilkminer.mc.boson.asm.transformer.modfix;

import net.thesilkminer.mc.boson.asm.utility.Log;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public final class BosonDeferredRegisterTransformer extends AbstractTransformer {
    private static final Log L = Log.of("BosonDeferredRegister");

    public BosonDeferredRegisterTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("boson_deferred_register")
                        .setDescription("Works around the limitation of the Kotlin type system by setting the parameter of register(RegistryObject.Register) to a raw type. DO NOT DISABLE")
                        .build(),
                ClassDescriptor.of("net.thesilkminer.mc.boson.implementation.registry.BosonDeferredRegister")
        );
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            public MethodVisitor visitMethod(final int access, @Nonnull final String name, @Nonnull final String desc, @Nullable final String signature, @Nullable final String[] exceptions) {
                if ((Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL) == access
                        && "register".equals(name)
                        && "(Lnet/minecraftforge/event/RegistryEvent$Register;)V".equals(desc)
                        && "(Lnet/minecraftforge/event/RegistryEvent$Register<*>;)V".equals(signature)) {
                    L.i("Stripping target method 'public final register(Lnet/minecraftforge/event/RegistryEvent$Register;)V of generic signature");
                    return super.visitMethod(access, name, desc, null, exceptions);
                }

                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };
    }
}
