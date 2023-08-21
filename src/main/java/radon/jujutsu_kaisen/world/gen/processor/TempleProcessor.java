package radon.jujutsu_kaisen.world.gen.processor;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.world.gen.processor.base.DisplayCaseProcessor;

public class TempleProcessor extends DisplayCaseProcessor {
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(JujutsuKaisen.MOD_ID, "structures/cursed_objects");

    public static final Codec<TempleProcessor> CODEC = Codec.unit(TempleProcessor::new);

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT_TABLE;
    }

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JJKProcessors.TEMPLE.get();
    }
}
