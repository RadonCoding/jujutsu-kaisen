package radon.jujutsu_kaisen.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.Optional;

public class JJKEntityDataSerializers {
    public static DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<EntityDataSerializer<Optional<ResourceLocation>>> OPTIONAL_RESOURCE_LOCATION = ENTITY_DATA_SERIALIZERS.register("optional_resource_location", () ->
            EntityDataSerializer.optional(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation));
    public static RegistryObject<EntityDataSerializer<Optional<Vector3f>>> OPTIONAL_VECTOR3F = ENTITY_DATA_SERIALIZERS.register("optional_vector3", () ->
            EntityDataSerializer.optional(FriendlyByteBuf::writeVector3f, FriendlyByteBuf::readVector3f));
}
