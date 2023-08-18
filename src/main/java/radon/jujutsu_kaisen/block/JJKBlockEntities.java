package radon.jujutsu_kaisen.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.fluid.JJKBlocks;

public class JJKBlockEntities {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JujutsuKaisen.MOD_ID);

    public static RegistryObject<BlockEntityType<DomainBlockEntity>> DOMAIN_BLOCK_ENTITY = BLOCK_ENTITIES.register("domain", () ->
            BlockEntityType.Builder.of(DomainBlockEntity::new,
                            JJKBlocks.UNLIMITED_VOID.get(),
                            JJKBlocks.COFFIN_OF_IRON_MOUNTAIN_ONE.get(),
                            JJKBlocks.COFFIN_OF_IRON_MOUNTAIN_TWO.get(),
                            JJKBlocks.COFFIN_OF_IRON_MOUNTAIN_THREE.get(),
                            JJKBlocks.CHIMERA_SHADOW_GARDEN.get())
                    .build(null));


}
