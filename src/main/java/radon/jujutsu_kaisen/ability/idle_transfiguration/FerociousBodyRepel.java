package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.client.ClientWrapper;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.idle_transfiguration.IIdleTransfigurationData;
import radon.jujutsu_kaisen.entity.effect.BodyRepelEntity;
import radon.jujutsu_kaisen.entity.effect.FerociousBodyRepelEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public class FerociousBodyRepel extends Ability implements ICharged {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying() || !owner.hasLineOfSight(target)) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IAbilityData data = cap.getAbilityData();

        if (data.isChanneling(this)) {
            return HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return HelperMethods.RANDOM.nextInt(40) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.CHANNELED;
    }

    private int getSoulCost(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return 0;

        IIdleTransfigurationData data = cap.getIdleTransfigurationData();

        return Math.min(data.getTransfiguredSouls(), 1 + (this.getCharge(owner) / 5));
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner instanceof Player) || !owner.level().isClientSide) return;

        ClientWrapper.setOverlayMessage(Component.translatable(String.format("chat.%s.souls", JujutsuKaisen.MOD_ID),
                this.getSoulCost(owner)), false);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IIdleTransfigurationData data = cap.getIdleTransfigurationData();

        if (data.getTransfiguredSouls() == 0) return false;

        return super.isValid(owner);
    }

    @Override
    public boolean onRelease(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        int souls = this.getSoulCost(owner);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        IIdleTransfigurationData data = cap.getIdleTransfigurationData();

        data.useTransfiguredSouls(souls);

        for (int i = 0; i < souls; i++) {
            owner.level().addFreshEntity(new FerociousBodyRepelEntity(owner, souls, (HelperMethods.RANDOM.nextFloat() - 0.5F) * 5.0F,
                    (HelperMethods.RANDOM.nextFloat() - 0.5F) * 2.5F));
        }
        return true;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }
}