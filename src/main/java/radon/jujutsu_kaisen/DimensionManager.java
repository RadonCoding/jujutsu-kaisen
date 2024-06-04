package radon.jujutsu_kaisen;

import com.google.common.collect.ImmutableList;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.network.packet.s2c.AddDimensionS2CPacket;
import radon.jujutsu_kaisen.world.level.biome.JJKBiomes;
import radon.jujutsu_kaisen.world.level.dimension.JJKDimensionTypes;

import java.util.*;

public class DimensionManager {
    private static final Set<ResourceKey<Level>> temporary = new HashSet<>();

    private static int index;

    public static boolean isTemporary(ServerLevel level) {
        return temporary.contains(level.dimension());
    }

    public static ServerLevel createDomainInside(MinecraftServer server) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(JujutsuKaisen.MOD_ID, String.valueOf(index)));

        Registry<Biome> biomeRegistry = server.registryAccess().registryOrThrow(Registries.BIOME);
        Registry<DimensionType> dimensionTypeRegistry = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);

        FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(
                Optional.empty(), biomeRegistry.getHolderOrThrow(JJKBiomes.TEMPORARY), List.of()
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

        instance.noSave = true;

        temporary.add(key);

        server.forgeGetWorldMap().put(key, instance);

        server.markWorldsDirty();

        overworld.getWorldBorder().addListener(new BorderChangeListener.DelegateBorderChangeListener(instance.getWorldBorder()));
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(instance));

        PacketDistributor.sendToAllPlayers(new AddDimensionS2CPacket(key));

        index++;

        return instance;
    }
}
