package radon.jujutsu_kaisen.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
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
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.network.packet.s2c.AddDimensionS2CPacket;
import radon.jujutsu_kaisen.world.level.biome.JJKBiomes;
import radon.jujutsu_kaisen.world.level.dimension.JJKDimensionTypes;

import java.util.*;

public class DimensionUtil {
    private static int index;

    public static ServerLevel createDomainInside(MinecraftServer server) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(JujutsuKaisen.MOD_ID, String.valueOf(index)));

        Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
        Registry<DimensionType> dimensionTypeRegistry = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);

        FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(
                Optional.empty(), biomeRegistry.getHolderOrThrow(Biomes.PLAINS), List.of()
        );
        settings.getLayersInfo().add(new FlatLayerInfo(1, JJKBlocks.DOMAIN_FLOOR.get()));
        LevelStem dimension = new LevelStem(dimensionTypeRegistry.getHolderOrThrow(JJKDimensionTypes.DOMAIN_EXPANSION), new FlatLevelSource(settings));

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

        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(instance.getWorldBorder()));

        server.forgeGetWorldMap().put(key, instance);

        server.markWorldsDirty();

        NeoForge.EVENT_BUS.post(new LevelEvent.Load(instance));

        PacketDistributor.sendToAllPlayers(new AddDimensionS2CPacket(key));

        index++;

        return instance;
    }
}
