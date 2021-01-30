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

package net.thesilkminer.mc.boson.asm;

import com.google.common.collect.ImmutableSet;
import net.thesilkminer.mc.boson.asm.transformer.modfix.BosonDeferredRegisterTransformer;
import net.thesilkminer.mc.boson.asm.transformer.modfix.CrtStoringErrorLoggerTransformer;
import net.thesilkminer.mc.boson.asm.transformer.modfix.GuiIngredientTransformer;
import net.thesilkminer.mc.fermion.asm.api.PluginMetadata;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractLaunchPlugin;

import javax.annotation.Nonnull;
import java.util.Set;

public final class BosonModFixerLaunchPlugin extends AbstractLaunchPlugin {

    public BosonModFixerLaunchPlugin() {
        super("boson.asm.modfix");
        this.registerTransformers();
    }

    @Override
    protected void populateMetadata(@Nonnull final PluginMetadata.Builder metadataBuilder) {
        metadataBuilder.setName("Boson ASM Mod-Fixer")
                .setDescription("Interaction with other particles may have unintended consequences, and we're here to fix this")
                .addAuthor("TheSilkMiner")
                .addAuthor("RE/SYST")
                .setVersion("0.1.1")
                .setLogoPath("boson_logo.png")
                .setCredits("Fermion for making this possible");
    }

    @Nonnull
    @Override
    public Set<String> getRootPackages() {
        return ImmutableSet.of("net.thesilkminer.mc.boson.asm");
    }

    private void registerTransformers() {
        this.registerTransformer(new BosonDeferredRegisterTransformer(this));
        this.registerTransformer(new CrtStoringErrorLoggerTransformer(this));
        this.registerTransformer(new GuiIngredientTransformer(this));
    }
}
