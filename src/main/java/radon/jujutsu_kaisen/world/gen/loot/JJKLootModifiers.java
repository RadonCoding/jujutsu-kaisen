package radon.jujutsu_kaisen.world.gen.loot;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<? extends IGlobalLootModifier>> ADD_TABLE = LOOT_MODIFIERS.register("add_table",
            () -> AddTableLootModifier.CODEC);
}
