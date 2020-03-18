package net.thesilkminer.mc.boson.asm.transformer;

import net.thesilkminer.mc.boson.asm.utility.Log;
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
import java.util.function.BiFunction;

@SuppressWarnings("SpellCheckingInspection")
public final class BlockMobSpawnerTransformer extends AbstractTransformer {

    private static final class GetSubBlocksMethodVisitor extends MethodVisitor {
        //  // access flags 0x1
        //  // signature (Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/util/NonNullList<Lnet/minecraft/item/ItemStack;>;)V
        //  // declaration: void <fermion-remap:func_149666_a>(net.minecraft.creativetab.CreativeTabs, net.minecraft.util.NonNullList<net.minecraft.item.ItemStack>)
        //  public <fermion-remap:func_149666_a>(Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/util/NonNullList;)V
        //   L0
        //    LINENUMBER 95 L0
        //    ALOAD 0
        //    ALOAD 1
        //    ALOAD 2
        //    INVOKESPECIAL net/minecraft/block/Block.<fermion-remap:func_149666_a> (Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/util/NonNullList;)V
        //   L1
        //    LINENUMBER 96 L1
        //    GETSTATIC net/minecraftforge/fml/common/registry/ForgeRegistries.ENTITIES : Lnet/minecraftforge/registries/IForgeRegistry;
        //    INVOKEINTERFACE net/minecraftforge/registries/IForgeRegistry.getEntries ()Ljava/util/Set; (itf)
        //    CHECKCAST java/lang/Iterable
        //    INVOKEINTERFACE java/lang/Iterable.iterator ()Ljava/util/Iterator; (itf)
        //    ASTORE 3
        //   L2
        //    LINENUMBER 97 L2
        //   FRAME APPEND [java/util/Iterator]
        //    ALOAD 3
        //    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
        //    IFEQ L3
        //   L4
        //    LINENUMBER 98 L4
        //    ALOAD 3
        //    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
        //    CHECKCAST java/util/Map$Entry
        //    ASTORE 4
        //   L5
        //    LINENUMBER 99 L5
        //    ALOAD 4
        //    INVOKEINTERFACE java/util/Map$Entry.getValue ()Ljava/lang/Object; (itf)
        //    CHECKCAST net/minecraftforge/fml/common/registry/EntityEntry
        //    ASTORE 5
        //   L6
        //    LINENUMBER 100 L6
        //    LDC Lnet/minecraft/entity/EntityLiving;.class
        //    ALOAD 5
        //    INVOKEVIRTUAL net/minecraftforge/fml/common/registry/EntityEntry.getEntityClass ()Ljava/lang/Class;
        //    INVOKEVIRTUAL java/lang/Class.isAssignableFrom (Ljava/lang/Class;)Z
        //    IFEQ L2
        //   L7
        //    LINENUMBER 101 L7
        //    NEW net/minecraft/nbt/NBTTagCompound
        //    DUP
        //    INVOKESPECIAL net/minecraft/nbt/NBTTagCompound.<init> ()V
        //    ASTORE 6
        //   L8
        //    LINENUMBER 102 L8
        //    ALOAD 6
        //    LDC "id"
        //    LDC "minecraft:mob_spawner"
        //    INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setString (Ljava/lang/String;Ljava/lang/String;)V
        //   L9
        //    LINENUMBER 103 L9
        //    ALOAD 6
        //    LDC "Count"
        //    ICONST_1
        //    INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setByte (Ljava/lang/String;B)V
        //   L10
        //    LINENUMBER 104 L10
        //    NEW net/minecraft/nbt/NBTTagCompound
        //    DUP
        //    INVOKESPECIAL net/minecraft/nbt/NBTTagCompound.<init> ()V
        //    ASTORE 7
        //   L11
        //    LINENUMBER 105 L11
        //    NEW net/minecraft/nbt/NBTTagCompound
        //    DUP
        //    INVOKESPECIAL net/minecraft/nbt/NBTTagCompound.<init> ()V
        //    ASTORE 8
        //   L12
        //    LINENUMBER 106 L12
        //    NEW net/minecraft/nbt/NBTTagCompound
        //    DUP
        //    INVOKESPECIAL net/minecraft/nbt/NBTTagCompound.<init> ()V
        //    ASTORE 9
        //   L13
        //    LINENUMBER 107 L13
        //    ALOAD 9
        //    LDC "id"
        //    ALOAD 4
        //    INVOKEINTERFACE java/util/Map$Entry.getKey ()Ljava/lang/Object; (itf)
        //    CHECKCAST net/minecraft/util/ResourceLocation
        //    CHECKCAST java/lang/Object
        //    INVOKEVIRTUAL java/lang/Object.toString ()Ljava/lang/String;
        //    INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setString (Ljava/lang/String;Ljava/lang/String;)V
        //   L14
        //    LINENUMBER 108 L14
        //    ALOAD 8
        //    LDC "SpawnData"
        //    ALOAD 9
        //    CHECKCAST net/minecraft/nbt/NBTBase
        //    INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setTag (Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V
        //   L15
        //    LINENUMBER 109 L15
        //    ALOAD 7
        //    LDC "BlockEntityTag"
        //    ALOAD 8
        //    CHECKCAST net/minecraft/nbt/NBTBase
        //    INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setTag (Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V
        //   L16
        //    LINENUMBER 110 L16
        //    ALOAD 6
        //    LDC "tag"
        //    ALOAD 7
        //    CHECKCAST net/minecraft/nbt/NBTBase
        //    INVOKEVIRTUAL net/minecraft/nbt/NBTTagCompound.setTag (Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V
        //   L17
        //    LINENUMBER 111 L17
        //    ALOAD 2
        //    CHECKCAST java/util/AbstractList
        //    NEW net/minecraft/item/ItemStack
        //    DUP
        //    ALOAD 6
        //    INVOKESPECIAL net/minecraft/item/ItemStack.<init> (Lnet/minecraft/nbt/NBTTagCompound;)V
        //    INVOKEVIRTUAL java/util/AbstractList.add (Ljava/lang/Object;)Z
        //    POP
        //   L18
        //    LINENUMBER 112 L18
        //    GOTO L2
        //   L3
        //    LINENUMBER 113 L3
        //   FRAME FULL [net/minecraft/block/BlockMobSpawner net/minecraft/creativetab/CreativeTabs net/minecraft/util/NonNullList java/util/Iterator] []
        //    NOP
        //    RETURN
        //   L19
        //    LOCALVARIABLE this Lnet/minecraft/block/BlockMobSpawner; L0 L19 0
        //    LOCALVARIABLE creativeTabs Lnet/minecraft/creativetab/CreativeTabs; L0 L19 1
        //    LOCALVARIABLE nonNullList Lnet/minecraft/util/NonNullList; L0 L19 2
        //    // signature Lnet/minecraft/util/NonNullList<Lnet/minecraft/item/ItemStack;>;
        //    // declaration: nonNullList extends net.minecraft.util.NonNullList<net.minecraft.item.ItemStack>
        //    LOCALVARIABLE iterator Ljava/util/Iterator; L2 L19 3
        //    // signature Ljava/util/Iterator<Ljava/util/Map$Entry<Lnet/minecraft/util/ResourceLocation;Lnet/minecraftforge/fml/common/registry/EntityEntry;>;>;
        //    // declaration: iterator extends java.util.Iterator<java.util.Map.Entry<net.minecraft.util.ResourceLocation, net.minecraftforge.fml.common.registry.EntityEntry>>
        //    LOCALVARIABLE entry Ljava/util/Map$Entry; L5 L3 4
        //    // signature Ljava/util/Map$Entry<Lnet/minecraft/util/ResourceLocation;Lnet/minecraftforge/fml/common/registry/EntityEntry;>;
        //    // declaration: entry extends java.util.Map.Entry<net.minecraft.util.ResourceLocation, net.minecraftforge.fml.common.registry.EntityEntry>
        //    LOCALVARIABLE entityEntry Lnet/minecraftforge/fml/common/registry/EntityEntry; L6 L3 5
        //    LOCALVARIABLE nBTTagCompound1 Lnet/minecraft/nbt/NBTTagCompound; L8 L3 6
        //    LOCALVARIABLE nBTTagCompound2 Lnet/minecraft/nbt/NBTTagCompound; L11 L3 7
        //    LOCALVARIABLE nBTTagCompound3 Lnet/minecraft/nbt/NBTTagCompound; L12 L3 8
        //    LOCALVARIABLE nBTTagCompound4 Lnet/minecraft/nbt/NBTTagCompound; L13 L3 9
        //    MAXSTACK = 4
        //    MAXLOCALS = 10

        private GetSubBlocksMethodVisitor(final int version, @Nonnull final MethodVisitor parent) {
            super(version, parent);
        }

        @Override
        public void visitCode() {
            super.visitCode();

            final Label l0 = new Label();
            super.visitLabel(l0);
            super.visitLineNumber(9 * 10 + 5, l0);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/block/Block", MappingUtilities.INSTANCE.mapMethod("func_149666_a"),
                    "(Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/util/NonNullList;)V", false);

            final Label l1 = new Label();
            super.visitLabel(l1);
            super.visitLineNumber(9 * 10 + 6, l1);
            super.visitFieldInsn(Opcodes.GETSTATIC, "net/minecraftforge/fml/common/registry/ForgeRegistries", "ENTITIES", "Lnet/minecraftforge/registries/IForgeRegistry;");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "net/minecraftforge/registries/IForgeRegistry", "getEntries", "()Ljava/util/Set;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Iterable");
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/lang/Iterable", "iterator", "()Ljava/util/Iterator;", true);
            super.visitVarInsn(Opcodes.ASTORE, 3);

            final Label l2 = new Label();
            final Label l3 = new Label();
            super.visitLabel(l2);
            super.visitLineNumber(9 * 10 + 7, l2);
            super.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/util/Iterator" }, 0, null);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            super.visitJumpInsn(Opcodes.IFEQ, l3);

            final Label l4 = new Label();
            super.visitLabel(l4);
            super.visitLineNumber(9 * 10 + 8, l4);
            super.visitVarInsn(Opcodes.ALOAD, 3);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/Map$Entry");
            super.visitVarInsn(Opcodes.ASTORE, 4);

            final Label l5 = new Label();
            super.visitLabel(l5);
            super.visitLineNumber(9 * 10 + 9, l5);
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraftforge/fml/common/registry/EntityEntry");
            super.visitVarInsn(Opcodes.ASTORE, 5);

            final Label l6 = new Label();
            super.visitLabel(l6);
            super.visitLineNumber(100, l6);
            super.visitLdcInsn(Type.getType("Lnet/minecraft/entity/EntityLiving;"));
            super.visitVarInsn(Opcodes.ALOAD, 5);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/registry/EntityEntry", "getEntityClass", "()Ljava/lang/Class;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z", false);
            super.visitJumpInsn(Opcodes.IFEQ, l2);

            final Label l7 = new Label();
            super.visitLabel(l7);
            super.visitLineNumber(100 + 1, l7);
            super.visitTypeInsn(Opcodes.NEW, "net/minecraft/nbt/NBTTagCompound");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/nbt/NBTTagCompound", "<init>", "()V", false);
            super.visitVarInsn(Opcodes.ASTORE, 6);

            final Label l8 = new Label();
            super.visitLabel(l8);
            super.visitLineNumber(100 + 2, l8);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("id");
            super.visitLdcInsn("minecraft:mob_spawner");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "setString", "(Ljava/lang/String;Ljava/lang/String;)V", false);

            final Label l9 = new Label();
            super.visitLabel(l9);
            super.visitLineNumber(100 + 3, l9);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("Count");
            super.visitInsn(Opcodes.ICONST_1);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "setByte", "(Ljava/lang/String;B)V", false);

            final Label l10 = new Label();
            super.visitLabel(l10);
            super.visitLineNumber(100 + 4, l10);
            super.visitTypeInsn(Opcodes.NEW, "net/minecraft/nbt/NBTTagCompound");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/nbt/NBTTagCompound", "<init>", "()V", false);
            super.visitVarInsn(Opcodes.ASTORE, 7);

            final Label l11 = new Label();
            super.visitLabel(l11);
            super.visitLineNumber(100 + 5, l11);
            super.visitTypeInsn(Opcodes.NEW, "net/minecraft/nbt/NBTTagCompound");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/nbt/NBTTagCompound", "<init>", "()V", false);
            super.visitVarInsn(Opcodes.ASTORE, 8);

            final Label l12 = new Label();
            super.visitLabel(l12);
            super.visitLineNumber(100 + 6, l12);
            super.visitTypeInsn(Opcodes.NEW, "net/minecraft/nbt/NBTTagCompound");
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/nbt/NBTTagCompound", "<init>", "()V", false);
            super.visitVarInsn(Opcodes.ASTORE, 9);

            final Label l13 = new Label();
            super.visitLabel(l13);
            super.visitLineNumber(100 + 7, l13);
            super.visitVarInsn(Opcodes.ALOAD, 9);
            super.visitLdcInsn("id");
            super.visitVarInsn(Opcodes.ALOAD, 4);
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;", true);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/util/ResourceLocation");
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "setString", "(Ljava/lang/String;Ljava/lang/String;)V", false);

            final Label l14 = new Label();
            super.visitLabel(l14);
            super.visitLineNumber(100 + 8, l14);
            super.visitVarInsn(Opcodes.ALOAD, 8);
            super.visitLdcInsn("SpawnData");
            super.visitVarInsn(Opcodes.ALOAD, 9);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/nbt/NBTBase");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "setTag", "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", false);

            final Label l15 = new Label();
            super.visitLabel(l15);
            super.visitLineNumber(100 + 9, l15);
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitLdcInsn("BlockEntityTag");
            super.visitVarInsn(Opcodes.ALOAD, 8);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/nbt/NBTBase");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "setTag", "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", false);

            final Label l16 = new Label();
            super.visitLabel(l16);
            super.visitLineNumber(100 + 10, l16);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitLdcInsn("tag");
            super.visitVarInsn(Opcodes.ALOAD, 7);
            super.visitTypeInsn(Opcodes.CHECKCAST, "net/minecraft/nbt/NBTBase");
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", "setTag", "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V", false);

            final Label l17 = new Label();
            super.visitLabel(l17);
            super.visitLineNumber(100 + 10 + 1, l17);
            super.visitVarInsn(Opcodes.ALOAD, 2);
            super.visitTypeInsn(Opcodes.CHECKCAST, "java/util/AbstractList");
            super.visitTypeInsn(Opcodes.NEW, "net/minecraft/item/ItemStack");
            super.visitInsn(Opcodes.DUP);
            super.visitVarInsn(Opcodes.ALOAD, 6);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/minecraft/item/ItemStack", "<init>", "(Lnet/minecraft/nbt/NBTTagCompound;)V", false);
            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/AbstractList", "add", "(Ljava/lang/Object;)Z", false);
            super.visitInsn(Opcodes.POP);

            final Label l18 = new Label();
            super.visitLabel(l18);
            super.visitLineNumber(100 + 10 + 2, l18);
            super.visitJumpInsn(Opcodes.GOTO, l2);

            super.visitLabel(l3);
            super.visitLineNumber(100 + 10 + 3, l3);
            super.visitFrame(Opcodes.F_FULL,
                    4, new Object[] { "net/minecraft/block/BlockMobSpawner", "net/minecraft/creativetab/CreativeTabs", "net/minecraft/util/NonNullList", "java/util/Iterator" },
                    0, new Object[] {});
            super.visitInsn(Opcodes.NOP);
            super.visitInsn(Opcodes.RETURN);

            final Label l19 = new Label();
            super.visitLabel(l19);

            super.visitLocalVariable("this", "Lnet/minecraft/block/BlockMobSpawner;", null, l0, l19, 0);
            super.visitLocalVariable("creativeTabs", "Lnet/minecraft/creativetab/CreativeTabs;", null, l0, l19, 1);
            super.visitLocalVariable("nonNullList", "Lnet/minecraft/util/NonNullList;", "Lnet/minecraft/util/NonNullList<Lnet/minecraft/item/ItemStack;>;", l0, l19, 2);
            super.visitLocalVariable("iterator", "Ljava/util/Iterator;",
                    "Ljava/util/Iterator<Ljava/util/Map$Entry<Lnet/minecraft/util/ResourceLocation;Lnet/minecraftforge/fml/common/registry/EntityEntry;>;>;", l2, l19, 3);
            super.visitLocalVariable("entry", "Ljava/util/Map$Entry;",
                    "Ljava/util/Map$Entry<Lnet/minecraft/util/ResourceLocation;Lnet/minecraftforge/fml/common/registry/EntityEntry;>;", l5, l3, 4);
            super.visitLocalVariable("entityEntry", "Lnet/minecraftforge/fml/common/registry/EntityEntry;", null, l6, l3, 5);
            super.visitLocalVariable("nBTTagCompound1", "Lnet/minecraft/nbt/NBTTagCompound;", null, l8, l3, 6);
            super.visitLocalVariable("nBTTagCompound2", "Lnet/minecraft/nbt/NBTTagCompound;", null, l11, l3, 7);
            super.visitLocalVariable("nBTTagCompound3", "Lnet/minecraft/nbt/NBTTagCompound;", null, l12, l3, 8);
            super.visitLocalVariable("nBTTagCompound4", "Lnet/minecraft/nbt/NBTTagCompound;", null, l13, l3, 9);

            super.visitMaxs(4, 10);
            super.visitEnd();
        }
    }

    // func_149666_a -> getSubBlocks
    // tag:{BlockEntityTag:{SpawnData:{id:"minecraft:pig"}}}
    //     7               8          9                  987
    private static final Log LOGGER = Log.of("BlockMobSpawner");

    public BlockMobSpawnerTransformer(@Nonnull final LaunchPlugin owner) {
        super(
                TransformerData.Builder.create()
                        .setOwningPlugin(owner)
                        .setName("block_mob_spawner")
                        .setDescription("Patches the Mob Spawner to list all possible mob spawner entries")
                        .build(),
                ClassDescriptor.of("net.minecraft.block.BlockMobSpawner")
        );
    }

    @Nonnull
    @Override
    public BiFunction<Integer, ClassVisitor, ClassVisitor> getClassVisitorCreator() {
        return (v, cw) -> new ClassVisitor(v, cw) {
            @Override
            public void visitEnd() {
                LOGGER.i("Reached end of class: injecting overload for 'getSubBlocks");

                final MethodVisitor parent = super.visitMethod(Opcodes.ACC_PUBLIC,
                        MappingUtilities.INSTANCE.mapMethod("func_149666_a"),
                        "(Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/util/NonNullList;)V",
                        "(Lnet/minecraft/creativetab/CreativeTabs;Lnet/minecraft/util/NonNullList<Lnet/minecraft/item/ItemStack;>;",
                        null);
                final MethodVisitor generator = new GetSubBlocksMethodVisitor(v, parent);
                generator.visitCode();

                super.visitEnd();
            }
        };
    }
}
