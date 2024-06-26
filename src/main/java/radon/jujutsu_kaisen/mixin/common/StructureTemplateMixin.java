package radon.jujutsu_kaisen.mixin.common;


import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import radon.jujutsu_kaisen.world.gen.processor.JJKProcessors;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
    @Inject(method = "placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z", at = @At(value = "HEAD"))
    private void placeInWorld(ServerLevelAccessor pServerLevel, BlockPos pOffset, BlockPos pPos, StructurePlaceSettings pSettings, RandomSource pRandom, int pFlags, CallbackInfoReturnable<Boolean> cir) {
        if (pSettings.getProcessors().stream().anyMatch(processor ->
                ((StructureProcessorAccessor) processor).getTypeAccessor() == JJKProcessors.WATERLOGGING_FIX_PROCESSOR.get())) {
            pSettings.setKeepLiquids(false);
        }
    }
}
