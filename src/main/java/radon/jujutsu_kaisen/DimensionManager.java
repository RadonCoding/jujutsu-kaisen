package radon.jujutsu_kaisen;


import com.mojang.serialization.Lifecycle;
import net.minecraft.server.RegistryLayer;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.network.packet.s2c.AddDimensionS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.RemoveDimensionS2CPacket;
import radon.jujutsu_kaisen.world.level.biome.JJKBiomes;
import radon.jujutsu_kaisen.world.level.dimension.JJKDimensionTypes;

import java.util.*;

public class DimensionManager {
    private static final Set<ResourceKey<Level>> temporary = new HashSet<>();
    private static final Map<ResourceKey<Level>, BorderChangeListener> listeners = new HashMap<>();

    private static int index;

    public static void remove(ServerLevel level) {
        MinecraftServer server = level.getServer();

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        if (overworld == null) return;

        temporary.remove(level.dimension());

        server.forgeGetWorldMap().remove(level.dimension());

        server.markWorldsDirty();

        overworld.getWorldBorder().removeListener(listeners.get(level.dimension()));

        listeners.remove(level.dimension());

        NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));

        PacketDistributor.sendToAllPlayers(new RemoveDimensionS2CPacket(level.dimension()));
    }

    public static ServerLevel create(MinecraftServer server, ResourceKey<DimensionType> type, FlatLevelGeneratorSettings settings) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(JujutsuKaisen.MOD_ID, String.valueOf(index)));

        Registry<DimensionType> dimensionTypeRegistry = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);

        LevelStem dimension = new LevelStem(dimensionTypeRegistry.getHolderOrThrow(type), new FlatLevelSource(settings));

        WorldData data = server.getWorldData();
        WorldOptions options = data.worldGenOptions();
        DerivedLevelData derivedLevelData = new DerivedLevelData(data, data.overworldData());

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        if (overworld == null) return null;

        ServerLevel instance = new ServerLevel(
                server,
                server.executor,
                server.storageSource,
                derivedLevelData,
                key,
                dimension,
                server.progressListenerFactory.create(11),
                data.isDebugWorld(),
                BiomeManager.obfuscateSeed(options.seed()),
                ImmutableList.of(),
                false,
                overworld.getRandomSequences()
        );

        instance.noSave = true;

        temporary.add(key);

        server.forgeGetWorldMap().put(key, instance);

        server.markWorldsDirty();

        BorderChangeListener listener = new BorderChangeListener.DelegateBorderChangeListener(instance.getWorldBorder());

        overworld.getWorldBorder().addListener(listener);

        listeners.put(key, listener);

        NeoForge.EVENT_BUS.post(new LevelEvent.Load(instance));

        PacketDistributor.sendToAllPlayers(new AddDimensionS2CPacket(key));

        index++;

        return instance;
    }
}
