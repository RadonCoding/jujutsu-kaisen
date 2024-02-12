package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;

import java.util.ArrayList;
import java.util.List;

public class ReleaseShikigami extends Ability {
    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) {
            IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (jujutsuCap == null) return false;

            ISorcererData data = jujutsuCap.getSorcererData();

            if (data == null) return false;

            List<TenShadowsSummon> summons = new ArrayList<>();

            for (Entity entity : data.getSummons()) {
                if (entity instanceof TenShadowsSummon summon && summon.isTame()) summons.add(summon);
            }
            return !summons.isEmpty();
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

        IJujutsuCapability jujutsuCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (jujutsuCap == null) return;

        ISorcererData data = jujutsuCap.getSorcererData();


        for (Entity entity : data.getSummons()) {
            if (!(entity instanceof TenShadowsSummon summon && summon.isTame())) continue;
            summon.discard();
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
