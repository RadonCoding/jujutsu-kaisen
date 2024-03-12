package radon.jujutsu_kaisen.world.gen.processor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PROCESSOR, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<AncientShrineProcessor>> ANCIENT_SHRINE = PROCESSORS.register("ancient_shrine_processor",
            () -> () -> AncientShrineProcessor.CODEC);
}
