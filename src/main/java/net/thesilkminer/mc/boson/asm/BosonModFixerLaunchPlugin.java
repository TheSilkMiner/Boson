package net.thesilkminer.mc.boson.asm;

import com.google.common.collect.ImmutableSet;
import net.thesilkminer.mc.boson.asm.transformer.modfix.CrtStoringErrorLoggerTransformer;
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
                .setVersion("1.0.0")
                .setLogoPath("boson_logo.png")
                .setCredits("Fermion for making this possible");
    }

    @Nonnull
    @Override
    public Set<String> getRootPackages() {
        return ImmutableSet.of("net.thesilkminer.mc.boson.asm");
    }

    private void registerTransformers() {
        this.registerTransformer(new CrtStoringErrorLoggerTransformer(this));
    }
}
