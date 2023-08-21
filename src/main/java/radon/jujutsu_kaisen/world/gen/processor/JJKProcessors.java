package radon.jujutsu_kaisen.world.gen.processor;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<StructureProcessorType<AncientShrineProcessor>> ANCIENT_SHRINE = PROCESSORS.register("ancient_shrine_processor",
            () -> () -> AncientShrineProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<TempleProcessor>> TEMPLE = PROCESSORS.register("temple_processor",
            () -> () -> TempleProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<OutpostProcessor>> OUTPOST = PROCESSORS.register("outpost_processor",
            () -> () -> OutpostProcessor.CODEC);
}
