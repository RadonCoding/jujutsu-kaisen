package radon.jujutsu_kaisen.ability.rika;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;

public class CommandPureLove extends Ability {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null && owner.getHealth() / owner.getMaxHealth() <= 0.25F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level instanceof ServerLevel level)) return;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        RikaEntity rika = cap.getSummonByClass(level, RikaEntity.class);

        if (rika == null) return;

        rika.setOpen();
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        return JJKAbilities.hasToggled(owner, JJKAbilities.RIKA.get()) && super.isUnlocked(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }
}
