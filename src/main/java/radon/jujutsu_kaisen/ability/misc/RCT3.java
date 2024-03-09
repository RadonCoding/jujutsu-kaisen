package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;


public class RCT3 extends RCT2 {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        if (data.getBurnout() > 0) {
            return true;
        }
        return super.shouldTrigger(owner, target);
    }

    @Override
    public void run(LivingEntity owner) {
        super.run(owner);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();
        

        int burnout = data.getBurnout();

        if (burnout > 0) {
            data.setBurnout(Math.max(0, burnout - 10));

            if (this.getCharge(owner) % 20 == 0) {
                data.increaseBrainDamage();
            }
        }
    }

    @Override
    public float getCost(LivingEntity owner) {
        float cost = super.getCost(owner);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0.0F;

        ISorcererData data = cap.getSorcererData();
        
        if (data == null) return 0.0F;

        if (data.getBurnout() > 0) {
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
    public int getPointsCost() {
        return ConfigHolder.SERVER.rct3Cost.get();
    }

    @Override
    protected int getMultiplier() {
        return 8;
    }
}
