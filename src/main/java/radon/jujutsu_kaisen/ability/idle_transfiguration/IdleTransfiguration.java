package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.shrine.Cleave;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.data.sorcerer.Trait;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.data.stat.ISkillData;
import radon.jujutsu_kaisen.data.stat.Skill;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.idle_transfiguration.base.TransfiguredSoulEntity;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.EntityUtil;

public class IdleTransfiguration extends Ability implements IToggled, IAttack {
    public static final double RANGE = 32.0D;

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return target != null;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    public static float calculateStrength(LivingEntity entity) {
        float strength = entity.getHealth();

        IJujutsuCapability cap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap != null) {
            ISkillData data = cap.getSkillData();
            strength *= data.getSkill(Skill.SOUL) + 1;
        }
        return strength;
    }

    public static boolean checkSukuna(LivingEntity owner, LivingEntity target) {
        IJujutsuCapability ownerCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (ownerCap == null) return false;

        ISorcererData ownerData = ownerCap.getSorcererData();

        IJujutsuCapability targetCap = target.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (targetCap == null) return false;

        ISorcererData targetData = targetCap.getSorcererData();

        if (!targetData.hasTrait(Trait.VESSEL) || targetData.getFingers() == 0) return false;

        float experience = targetData.getFingers() * ((SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 4.0F) / 20);

        if (experience <= ownerData.getExperience()) return false;

        Cleave.perform(target, owner, null, JJKDamageSources.soulAttack(owner));

        return true;
    }

    private void run(LivingEntity owner, LivingEntity target) {
        if (checkSukuna(owner, target)) return;

        MobEffectInstance existing = target.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

        int amplifier = 0;

        if (existing != null) {
            amplifier = existing.getAmplifier() + 1;
        }

        MobEffectInstance instance = new MobEffectInstance(JJKEffects.TRANSFIGURED_SOUL.get(), 60 * 20, amplifier, false, true, true);
        target.addEffect(instance);
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public float getCost(LivingEntity owner) {
        return 10.0F;
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        IAbilityData data = cap.getAbilityData();

        if (data.hasToggled(JJKAbilities.SOUL_DECIMATION.get())) {
            data.toggle(JJKAbilities.SOUL_DECIMATION.get());
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }

    public static void absorb(LivingEntity owner, LivingEntity target) {
        ItemStack stack = new ItemStack(JJKItems.TRANSFIGURED_SOUL.get());

        if (owner instanceof Player player) {
            player.addItem(stack);
        } else {
            owner.setItemSlot(EquipmentSlot.MAINHAND, stack);
        }

        EntityUtil.makePoofParticles(target);

        if (!(target instanceof Player)) {
            target.discard();
        } else {
            target.kill();
        }
    }

    @Override
    public boolean attack(DamageSource source, LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return false;
        if (!DamageUtil.isMelee(source)) return false;
        if (!owner.getMainHandItem().isEmpty()) return false;

        this.run(owner, target);

        return true;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            DamageSource source = event.getSource();

            if (!DamageUtil.isMelee(source)) return;

            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            IAbilityData data = cap.getAbilityData();

            if (!data.hasToggled(JJKAbilities.IDLE_TRANSFIGURATION.get())) return;

            LivingEntity victim = event.getEntity();

            if (victim.level().isClientSide) return;

            MobEffectInstance existing = victim.getEffect(JJKEffects.TRANSFIGURED_SOUL.get());

            if (existing == null) return;

            int amplifier = existing.getAmplifier();

            float attackerStrength = IdleTransfiguration.calculateStrength(attacker);
            float victimStrength = IdleTransfiguration.calculateStrength(victim);

            int required = Math.round((victimStrength / attackerStrength) * 2);

            if (victim instanceof TransfiguredSoulEntity || amplifier >= required) {
                absorb(attacker, victim);
            }
        }
    }
}
