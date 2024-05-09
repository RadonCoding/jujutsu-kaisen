package radon.jujutsu_kaisen.world.gen.structure;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<ExtendedJigsawStructure>> EXTENDED_JIGSAW = STRUCTURE_TYPES.register("extended_jigsaw",
            () -> () -> ExtendedJigsawStructure.CODEC);
}
