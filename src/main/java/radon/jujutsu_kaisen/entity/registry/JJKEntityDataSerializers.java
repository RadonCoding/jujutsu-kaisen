package radon.jujutsu_kaisen.entity.registry;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.Optional;

public class JJKEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<GameProfile>>> GAME_PROFILE = ENTITY_DATA_SERIALIZERS.register("game_profile", () ->
            EntityDataSerializer.forValueType(ByteBufCodecs.optional(ByteBufCodecs.GAME_PROFILE)));
}
