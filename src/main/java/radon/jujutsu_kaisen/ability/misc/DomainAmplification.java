package radon.jujutsu_kaisen.ability.misc;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityDisplayInfo;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.util.HelperMethods;

public class DomainAmplification extends Ability implements Ability.IToggled {
    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, JJKAbilities.MAHORAGA.get())) return false;
        if (JJKAbilities.hasToggled(owner, JJKAbilities.WHEEL.get())) return false;

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        Ability domain = ((ISorcerer) owner).getDomain();
        return target != null && owner.distanceTo(target) <= 3.0D && owner.hasLineOfSight(target) &&
                (HelperMethods.isStrongest(cap.getExperience()) || !JJKAbilities.hasToggled(owner, domain)) && JJKAbilities.hasToggled(target, JJKAbilities.INFINITY.get());
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
    public MenuType getMenuType() {
        return MenuType.DOMAIN;
    }

    @Nullable
    @Override
    public Ability getParent(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        CursedTechnique technique = cap.getTechnique();
        return technique == null || technique.getDomain() == null ? JJKAbilities.SIMPLE_DOMAIN.get() : technique.getDomain();
    }

    @Override
    public Vec2 getDisplayCoordinates() {
        return new Vec2(4.0F, 0.5F);
    }

    @Override
    public AbilityDisplayInfo getDisplay(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Vec2 coordinates = this.getDisplayCoordinates();
        return new AbilityDisplayInfo(String.format("%s_%s", JJKAbilities.getKey(this).getPath(), cap.getType().name().toLowerCase()), coordinates.x, coordinates.y);
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
    public static class DomainAmplificationForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            if (!(event.getSource() instanceof JJKDamageSources.JujutsuDamageSource source)) return;

            LivingEntity victim = event.getEntity();

            // If not enabled, then enable
            if (victim instanceof ISorcerer && !JJKAbilities.hasToggled(victim, JJKAbilities.DOMAIN_AMPLIFICATION.get())) {
                ISorcererData cap = victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                Ability domain = ((ISorcerer) victim).getDomain();

                if ((HelperMethods.isStrongest(cap.getExperience()) || !JJKAbilities.hasToggled(victim, domain))) {
                    AbilityHandler.trigger(victim, JJKAbilities.DOMAIN_AMPLIFICATION.get());
                }
            }

            if (!JJKAbilities.hasToggled(victim, JJKAbilities.DOMAIN_AMPLIFICATION.get())) return;

            Ability ability = source.getAbility();

            if (ability == null) return;

            if (ability.isTechnique()) {
                event.setAmount(event.getAmount() * (ability.getRequirements().contains(Trait.REVERSE_CURSED_TECHNIQUE) ? 0.75F : 0.5F));
            }
        }
    }
}