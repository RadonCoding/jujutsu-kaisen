package radon.jujutsu_kaisen;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.ISimpleDomain;
import radon.jujutsu_kaisen.world.level.biome.JJKBiomes;
import radon.jujutsu_kaisen.world.level.dimension.JJKDimensionTypes;

import java.util.List;
import java.util.Optional;

public class DomainHandler {
    public static @Nullable Level getOrCreateInside(ServerLevel level, DomainExpansionEntity domain) {
        for (IBarrier barrier : VeilHandler.getBarriers(level, domain.getBounds())) {
            if (!(barrier instanceof IDomain other) || barrier instanceof ISimpleDomain) continue;
            if (other == domain) continue;

            if (domain.getInside() == null) continue;

            return domain.getInside();
        }

        Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);

        FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(
                Optional.empty(), registry.getHolderOrThrow(JJKBiomes.TEMPORARY), List.of()
        );
        settings.getLayersInfo().add(new FlatLayerInfo(1, JJKBlocks.DOMAIN_FLOOR.get()));
        settings.updateLayers();

        ServerLevel inside = DimensionManager.create(level.getServer(), JJKDimensionTypes.DOMAIN_EXPANSION, settings);

        if (inside == null) return null;

        IDomainData data = inside.getData(JJKAttachmentTypes.DOMAIN);
        data.setOriginal(level.dimension());

        return inside;
    }
}
