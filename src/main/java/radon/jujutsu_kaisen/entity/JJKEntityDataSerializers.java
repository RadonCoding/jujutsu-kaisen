package radon.jujutsu_kaisen.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JJKEntityDataSerializers {
    public static DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<EntityDataSerializer<Optional<CompoundTag>>> OPTIONAL_COMPOUND_TAG = ENTITY_DATA_SERIALIZERS.register("optional_compound_tag", () ->
            EntityDataSerializer.optional(FriendlyByteBuf::writeNbt, FriendlyByteBuf::readNbt));
}
