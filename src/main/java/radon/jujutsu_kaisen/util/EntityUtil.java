package radon.jujutsu_kaisen.util;


import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.misc.RCT1;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.data.ability.IAbilityData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityUtil {
    public static Vec3 getPosition(Entity entity, float partialTicks) {
        return getPosition(entity, 0.0F, partialTicks);
    }

    public static Vec3 getPosition(Entity entity, double yOffset, float partialTicks) {
        return new Vec3(
                Mth.lerp(partialTicks, entity.xOld, entity.getX()),
                Mth.lerp(partialTicks, entity.yOld, entity.getY()) + yOffset,
                Mth.lerp(partialTicks, entity.zOld, entity.getZ())
        );
    }

    public static float calculateDamage(DamageSource source, LivingEntity target) {
        float damage = target.getMaxHealth();
        float armor = (float) target.getArmorValue();
        float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        float f = 2.0F + toughness / 4.0F;
        float f1 = Mth.clamp(armor - damage / f, armor * 0.2F, 20.0F);
        damage /= 1.0F - f1 / 25.0F;

        MobEffectInstance instance = target.getEffect(MobEffects.DAMAGE_RESISTANCE);

        if (instance != null) {
            int resistance = instance.getAmplifier();
            int i = (resistance + 1) * 5;
            int j = 25 - i;

            if (j == 0) {
                return damage;
            } else {
                float x = 25.0F / (float) j;
                damage = damage * x;
            }
        }

        int k = EnchantmentHelper.getDamageProtection(target.getArmorSlots(), source);

        if (k > 0) {
            float f2 = Mth.clamp(k, 0.0F, 20.0F);
            damage /= 1.0F - f2 / 25.0F;
        }
        return damage;
    }

    @Nullable
    public static LivingEntity getOwner(TamableAnimal tamable) {
        LivingEntity owner = tamable;

        while (owner instanceof TamableAnimal parent && parent.isTame()) {
            owner = parent.getOwner();

            if (owner == null) return null;
        }
        return owner;
    }

    public static <T extends Entity> List<T> getEntities(Class<T> clazz, EntityGetter getter, @Nullable LivingEntity owner, AABB bounds) {
        return getter.getEntitiesOfClass(clazz, bounds, EntitySelector.ENTITY_STILL_ALIVE
                .and(EntitySelector.NO_CREATIVE_OR_SPECTATOR)
                // Add entities if owner is null or the entity is not owner and the entity is not a tame owned by the owner
                .and(entity -> owner == null || (entity != owner && (!(entity instanceof TamableAnimal tamable) || getOwner(tamable) != owner))));
    }

    public static <T extends Entity> List<T> getTouchableEntities(Class<T> clazz, EntityGetter getter, @Nullable LivingEntity owner, AABB bounds) {
        List<T> entities = new ArrayList<>();

        for (T entity : getEntities(clazz, getter, owner, bounds)) {
            IJujutsuCapability entityCap = entity.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (entityCap != null) {
                IAbilityData entityData = entityCap.getAbilityData();

                if (entityData.hasActive(JJKAbilities.INFINITY.get())) continue;
            }
            entities.add(entity);
        }
        return entities;
    }

    public static void makePoofParticles(Entity entity) {
        for (int i = 0; i < 20; ++i) {
            double d0 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d1 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d2 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            ((ServerLevel) entity.level()).sendParticles(ParticleTypes.POOF, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D),
                    0, d0, d1, d2, 1.0D);
        }
    }

    public static void rotation(Entity entity, Vec3 look) {
        entity.setYRot(RotationUtil.getYaw(look));
        entity.setXRot(RotationUtil.getPitch(look));
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();
    }

    public static void offset(Entity entity, Vec3 look, Vec3 pos) {
        rotation(entity, look);
        entity.setPos(pos.x, pos.y, pos.z);
    }

    public static void convertTo(LivingEntity src, LivingEntity dst, boolean transferInventory, boolean kill) {
        if (!src.isRemoved()) {
            dst.copyPosition(src);

            if (src.hasCustomName()) {
                dst.setCustomName(src.getCustomName());
                dst.setCustomNameVisible(src.isCustomNameVisible());
            }

            dst.setInvulnerable(src.isInvulnerable());

            if (transferInventory) {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    ItemStack stack = src.getItemBySlot(slot);

                    if (!stack.isEmpty()) {
                        dst.setItemSlot(slot, stack.copy());
                    }
                }
            }

            src.level().addFreshEntity(dst);

            if (src.isPassenger()) {
                Entity vehicle = src.getVehicle();
                src.stopRiding();

                if (vehicle != null) {
                    vehicle.startRiding(vehicle, true);
                }
            }

            if (kill) {
                if (src instanceof Player) {
                    src.kill();
                } else {
                    src.discard();
                }
            }
        }
    }

    @Nullable
    public static RCT1 getRCTTier(LivingEntity owner) {
        if (JJKAbilities.RCT3.get().isUnlocked(owner)) return JJKAbilities.RCT3.get();
        if (JJKAbilities.RCT2.get().isUnlocked(owner)) return JJKAbilities.RCT2.get();
        if (JJKAbilities.RCT1.get().isUnlocked(owner)) return JJKAbilities.RCT1.get();

        return null;
    }

    public static boolean applyModifier(LivingEntity owner, Holder<Attribute> holder, UUID identifier, String name, double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = owner.getAttribute(holder);
        AttributeModifier modifier = new AttributeModifier(identifier, name, amount, operation);

        if (instance == null) return false;
        AttributeModifier existing = instance.getModifier(identifier);

        if (existing == null) {
            instance.addTransientModifier(modifier);
            return true;
        }

        if (existing.amount() == amount) return false;

        instance.removeModifier(identifier);
        instance.addTransientModifier(modifier);

        return true;
    }

    public static void removeModifier(LivingEntity owner, Holder<Attribute> holder, UUID identifier) {
        AttributeInstance instance = owner.getAttribute(holder);

        if (instance != null) {
            instance.removeModifier(identifier);
        }
    }
}
