package net.thesilkminer.mc.boson.asm;

import com.google.common.collect.ImmutableSet;
import net.thesilkminer.mc.boson.asm.transformer.GameDataTransformer;
import net.thesilkminer.mc.boson.asm.transformer.LoadControllerTransformer;
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
                .setVersion("1.0.0")
                .setCredits("Fermion for making this possible");
    }

    @Nonnull
    @Override
    public Set<String> getRootPackages() {
        return ImmutableSet.of("net.thesilkminer.mc.boson.asm");
    }

    private void registerTransformers() {
        this.registerTransformer(new GameDataTransformer(this));
        this.registerTransformer(new LoadControllerTransformer(this));
        this.registerTransformer(new ObjectHolderRegistryTransformer(this));
        this.registerTransformer(new ProgressBarTransformer(this));
        this.registerTransformer(new ProgressManagerTransformer(this));
    }
}
