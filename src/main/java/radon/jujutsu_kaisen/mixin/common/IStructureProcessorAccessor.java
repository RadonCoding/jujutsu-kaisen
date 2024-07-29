package radon.jujutsu_kaisen.mixin.common;


import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureProcessor.class)
public interface IStructureProcessorAccessor {
    @Invoker("getType")
    StructureProcessorType<?> getTypeAccessor();
}
