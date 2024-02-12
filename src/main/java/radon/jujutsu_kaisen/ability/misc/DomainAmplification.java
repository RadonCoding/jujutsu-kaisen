package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;

public class DomainAmplification extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.hasToggled(JJKAbilities.INFINITY.get()) && owner.distanceTo(target) <= 3.0D;
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public boolean isCursedEnergyColor() {
        return true;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.2F;
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.DOMAIN;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return null;

        ISorcererData data = cap.getSorcererData();

        ICursedTechnique technique = data.getTechnique();
        return technique == null || technique.getDomain() == null ? JJKAbilities.CURSED_ENERGY_FLOW.get() : technique.getDomain();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(4.0F, 1.0F);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public int getPointsCost() {
        return ConfigHolder.SERVER.domainAmplificationCost.get();
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (!(event.getSource() instanceof JJKDamageSources.JujutsuDamageSource source)) return;

            LivingEntity victim = event.getEntity();

            IJujutsuCapability cap = victim.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (!data.hasToggled(JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

            Ability ability = source.getAbility();

            if (ability == null) return;

            if (ability.isTechnique()) {
                event.setAmount(event.getAmount() * (ability.getRequirements().contains(JJKAbilities.RCT1.get()) ? 0.75F : 0.5F));
            }
        }
    }
}