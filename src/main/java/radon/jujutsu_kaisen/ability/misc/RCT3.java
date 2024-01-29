package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;


public class RCT3 extends RCT2 {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getBurnout() > 0) {
            return true;
        }
        return super.shouldTrigger(owner, target);
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        int burnout = cap.getBurnout();

        if (burnout > 0) {
            cap.setBurnout(Math.max(0, burnout - 5));

            if (this.getCharge(owner) % 20 == 0) {
                cap.increaseBrainDamage();
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        float cost = super.getCost(owner);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (cap.getBurnout() > 0) {
            cost += 100.0F / 20;
        }
        return cost;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        return JJKAbilities.RCT2.get();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(4.0F, 2.0F);
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.rct3Cost.get();
    }

    @Override
    protected int getMultiplier() {
        return 8;
    }
}
