package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Release extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        AtomicBoolean result = new AtomicBoolean();

        if (target == null) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                List<TenShadowsSummon> summons = new ArrayList<>();

                for (Entity entity : cap.getSummons((ServerLevel) owner.level)) {
                    if (entity instanceof TenShadowsSummon summon && summon.isTame()) summons.add(summon);
                }
                result.set(summons.size() > 0);
            });
        }
        return result.get();
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level.isClientSide) return;

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
            for (Entity entity : cap.getSummons((ServerLevel) owner.level)) {
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
