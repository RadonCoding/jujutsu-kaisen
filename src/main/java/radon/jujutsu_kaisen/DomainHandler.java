package radon.jujutsu_kaisen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.domain.IDomainData;
import radon.jujutsu_kaisen.data.registry.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.DomainExpansionEntity;
import radon.jujutsu_kaisen.entity.IBarrier;
import radon.jujutsu_kaisen.entity.IDomain;
import radon.jujutsu_kaisen.entity.ISimpleDomain;

public class DomainHandler {
    public static @Nullable Level getOrCreateInside(ServerLevel level, DomainExpansionEntity domain) {
        for (IBarrier barrier : VeilHandler.getBarriers(level, domain.getBounds())) {
            if (!(barrier instanceof IDomain other) || barrier instanceof ISimpleDomain) continue;
            if (other == domain) continue;

            if (domain.getInside() == null) continue;

            return domain.getInside();
        }

        ServerLevel inside = DimensionManager.createDomainInside(level.getServer());

        if (inside == null) return null;

        IDomainData data = inside.getData(JJKAttachmentTypes.DOMAIN);
        data.init(level.dimension());

        return inside;
    }
}
