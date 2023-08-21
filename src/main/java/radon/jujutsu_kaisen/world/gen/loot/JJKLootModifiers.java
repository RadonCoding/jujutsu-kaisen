package radon.jujutsu_kaisen.world.gen.loot;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEMS = LOOT_MODIFIERS.register("add_items",
            () -> AddItemsModifier.CODEC);
}
