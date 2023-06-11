package radon.jujutsu_kaisen.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JujutsuBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<BlockEntityType<DomainBlockEntity>> DOMAIN_BLOCK_ENTITY = BLOCK_ENTITIES.register("domain_block_entity", () ->
            BlockEntityType.Builder.of(DomainBlockEntity::new, JujutsuBlocks.INFINITE_VOID.get()).build(null));
}
