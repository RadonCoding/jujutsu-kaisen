package radon.jujutsu_kaisen.ability.idle_transfiguration;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Transformation;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class InstantSpiritBodyOfDistortedKilling extends Transformation {
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("81461f5f-89d5-4cc9-8b25-17e7caac9255");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("84341016-e56a-4b95-9fd5-42b36154c885");
    private static final UUID STEP_HEIGHT_UUID = UUID.fromString("654c65b5-dc0f-4092-8423-59cbe3d19682");
    private static final UUID ARMOR_UUID = UUID.fromString("486fd273-fdbc-4876-b0b8-af5a64bfb08a");
    private static final UUID ARMOR_TOUGHNESS_UUID = UUID.fromString("0be71dde-8aeb-4c5d-955f-d37325c31a94");

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null || target.isDeadOrDying()) return false;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getType() == JujutsuType.CURSE || JJKAbilities.RCT1.get().isUnlocked(owner) ? owner.getHealth() / owner.getMaxHealth() < 0.9F : owner.getHealth() / owner.getMaxHealth() < 0.4F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level)) return;

        int count = (int) (owner.getBbWidth() * owner.getBbHeight()) * 8;

        for (int i = 0; i < count; i++) {
            double x = owner.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2) - owner.getLookAngle().scale(0.35D).x;
            double y = owner.getY() + HelperMethods.RANDOM.nextDouble() * owner.getBbHeight();
            double z = owner.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (owner.getBbWidth() * 2) - owner.getLookAngle().scale(0.35D).z;
            level.sendParticles(ParticleTypes.SMOKE, x, y, z, 0, 0.0D, HelperMethods.RANDOM.nextDouble() * 0.1D, 0.0D, 1.0D);
        }
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.isInZone() && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 1.0F;
    }

    @Override
    public boolean isReplacement() {
        return true;
    }

    @Override
    public Item getItem() {
        return JJKItems.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get();
    }

    @Override
    public Part getBodyPart() {
        return Part.BODY;
    }

    @Override
    public void onRightClick(LivingEntity owner) {

    }

    @Override
    public void applyModifiers(LivingEntity owner) {
        EntityUtil.applyModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Attack damage", 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtil.applyModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID, "Movement speed", 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        EntityUtil.applyModifier(owner, NeoForgeMod.STEP_HEIGHT.value(), STEP_HEIGHT_UUID, "Step height addition", 2.0F, AttributeModifier.Operation.ADDITION);
        EntityUtil.applyModifier(owner, Attributes.ARMOR, ARMOR_UUID, "Armor", 20.0D, AttributeModifier.Operation.ADDITION);
        EntityUtil.applyModifier(owner, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_UUID, "Armor toughness", 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void removeModifiers(LivingEntity owner) {
        EntityUtil.removeModifier(owner, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
        EntityUtil.removeModifier(owner, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
        EntityUtil.removeModifier(owner, NeoForgeMod.STEP_HEIGHT.value(), STEP_HEIGHT_UUID);
        EntityUtil.removeModifier(owner, Attributes.ARMOR, ARMOR_UUID);
        EntityUtil.removeModifier(owner, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_UUID);
    }

    @Override
    public void onEnabled(LivingEntity owner) {

    }

    @Override
    public void onDisabled(LivingEntity owner) {

    }
}