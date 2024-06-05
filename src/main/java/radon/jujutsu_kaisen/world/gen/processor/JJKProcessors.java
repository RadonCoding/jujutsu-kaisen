package radon.jujutsu_kaisen.world.gen.processor;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<CursedToolItemFrameProcessor>> CURSED_TOOL_ITEM_FRAME_PROCESSOR = PROCESSORS.register("cursed_tool_item_frame_processor",
            () -> () -> CursedToolItemFrameProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<WaterloggingFixProcessor>> WATERLOGGING_FIX_PROCESSOR = PROCESSORS.register("waterlogging_fix_processor",
            () -> () -> WaterloggingFixProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<HeadquartersProcessor>> HEADQUARTERS_PROCESSOR = PROCESSORS.register("headquarters_processor",
            () -> () -> HeadquartersProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<MissionProcessor>> MISSION_PROCESSOR = PROCESSORS.register("mission_processor",
            () -> () -> MissionProcessor.CODEC);
}
