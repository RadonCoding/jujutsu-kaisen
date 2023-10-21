package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;

import java.util.ArrayList;
import java.util.List;

public class ReleaseShikigami extends Ability {
    @Override
    public boolean isChantable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) {
            if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            List<TenShadowsSummon> summons = new ArrayList<>();

            for (Entity entity : cap.getSummons((ServerLevel) owner.level())) {
                if (entity instanceof TenShadowsSummon summon && summon.isTame()) summons.add(summon);
            }
            return summons.size() > 0;
        }
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (Entity entity : cap.getSummons((ServerLevel) owner.level())) {
                if (!(entity instanceof TenShadowsSummon summon && summon.isTame())) continue;
                summon.discard();
            }
        });
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
