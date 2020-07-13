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

package net.thesilkminer.mc.boson.asm;

import com.google.common.collect.ImmutableSet;
import net.thesilkminer.mc.boson.asm.transformer.BlockMobSpawnerTransformer;
import net.thesilkminer.mc.boson.asm.transformer.GameDataTransformer;
import net.thesilkminer.mc.boson.asm.transformer.InternalLoggerFactoryTransformer;
import net.thesilkminer.mc.boson.asm.transformer.LoadControllerTransformer;
import net.thesilkminer.mc.boson.asm.transformer.LocaleTransformer;
import net.thesilkminer.mc.boson.asm.transformer.ModelLoaderVariantLoaderTransformer;
import net.thesilkminer.mc.boson.asm.transformer.ObjectHolderRegistryTransformer;
import net.thesilkminer.mc.boson.asm.transformer.ProgressBarTransformer;
import net.thesilkminer.mc.boson.asm.transformer.ProgressManagerTransformer;
import net.thesilkminer.mc.fermion.asm.api.PluginMetadata;
import net.thesilkminer.mc.fermion.asm.prefab.AbstractLaunchPlugin;

import javax.annotation.Nonnull;
import java.util.Set;

public final class BosonLaunchPlugin extends AbstractLaunchPlugin {

    public BosonLaunchPlugin() {
        super("boson.asm");
        this.registerTransformers();
    }

    @Override
    protected void populateMetadata(@Nonnull final PluginMetadata.Builder metadataBuilder) {
        metadataBuilder.setName("Boson ASM")
                .setDescription("Every Boson is at the core of the universe: this is no exception")
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
        this.registerTransformer(new BlockMobSpawnerTransformer(this));
        this.registerTransformer(new GameDataTransformer(this));
        this.registerTransformer(new InternalLoggerFactoryTransformer(this));
        this.registerTransformer(new LoadControllerTransformer(this));
        this.registerTransformer(new LocaleTransformer(this));
        this.registerTransformer(new ModelLoaderVariantLoaderTransformer(this));
        this.registerTransformer(new ObjectHolderRegistryTransformer(this));
        this.registerTransformer(new ProgressBarTransformer(this));
        this.registerTransformer(new ProgressManagerTransformer(this));
    }
}
