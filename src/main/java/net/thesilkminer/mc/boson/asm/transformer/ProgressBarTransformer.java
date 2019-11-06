package net.thesilkminer.mc.boson.asm.transformer;

import com.google.common.collect.ImmutableList;
import net.thesilkminer.mc.boson.asm.utility.Log;
import net.thesilkminer.mc.fermion.asm.api.LaunchPlugin;
import net.thesilkminer.mc.fermion.asm.api.descriptor.ClassDescriptor;
import net.thesilkminer.mc.fermion.asm.api.descriptor.MethodDescriptor;
import net.thesilkminer.mc.fermion.asm.api.transformer.TransformerData;
import net.thesilkminer.mc.fermion.asm.prefab.transformer.SingleTargetMethodTransformer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public final class ProgressBarTransformer extends SingleTargetMethodTransformer {

    private static final Log LOGGER = Log.of("ProgressManager$ProgressBar");

    public ProgressBarTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("progress_bar")
                        .setDescription("Companion to the ProgressManager plugin that fixes wrong messages being displayed on the ProgressBar")
                        .build(),
                ClassDescriptor.of("net.minecraftforge.fml.common.ProgressManager$ProgressBar"),
                MethodDescriptor.of("step", ImmutableList.of(ClassDescriptor.of(String.class)), ClassDescriptor.of(void.class))
        );
    }

    @Nonnull
    @Override
    protected BiFunction<Integer, MethodVisitor, MethodVisitor> getMethodVisitorCreator() {
        return (v, mv) -> new MethodVisitor(v, mv) {
            @Override
            public void visitCode() {
                super.visitCode();

                super.visitVarInsn(Opcodes.ALOAD, 1);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/thesilkminer/mc/boson/hook/ProgressManagerHook",
                        "checkForRegistryCreationMessage", "(Ljava/lang/String;)Ljava/lang/String;", false);
                super.visitVarInsn(Opcodes.ASTORE, 1);

                LOGGER.i("Successfully added checking code");
            }
        };
    }
}
