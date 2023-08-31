package radon.jujutsu_kaisen.world.gen.processor;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.world.gen.processor.base.ItemFrameProcessor;

public class AncientShrineProcessor extends ItemFrameProcessor {
    public static final Codec<AncientShrineProcessor> CODEC = Codec.unit(AncientShrineProcessor::new);

    @Override
    protected @NotNull StructureProcessorType<?> getType() {
        return JJKProcessors.ANCIENT_SHRINE.get();
    }
}
