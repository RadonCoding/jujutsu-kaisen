package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.Optional;

public class JJKEntityDataSerializers {
    public static DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<CompoundTag>>> OPTIONAL_COMPOUND_TAG = ENTITY_DATA_SERIALIZERS.register("optional_compound_tag", () ->
            EntityDataSerializer.optional(FriendlyByteBuf::writeNbt, FriendlyByteBuf::readNbt));
}
