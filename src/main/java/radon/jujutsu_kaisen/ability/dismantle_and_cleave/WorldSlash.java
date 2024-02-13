package radon.jujutsu_kaisen.ability.dismantle_and_cleave;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.projectile.BigDismantleProjectile;
import radon.jujutsu_kaisen.entity.projectile.WorldSlashProjectile;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class WorldSlash extends Ability {
    private static final double RANGE = 64.0D;
    public static final float SPEED = 5.0F;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        if (owner instanceof MahoragaEntity) return HelperMethods.RANDOM.nextInt(5) == 0;

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return false;

        ISorcererData data = cap.getSorcererData();

        return data.getType() == JujutsuType.CURSE || data.isUnlocked(JJKAbilities.RCT1.get()) ? owner.getHealth() / owner.getMaxHealth() < 0.9F :
                owner.getHealth() / owner.getMaxHealth() < 0.8F || target.getHealth() > owner.getHealth() * 2;
    }

    @Override
    public boolean isUnlockable() {
        return true;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (owner instanceof MahoragaEntity) {
            IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return false;

            ITenShadowsData data = cap.getTenShadowsData();

            if (data.getAdaptation(JJKAbilities.INFINITY.get()) > 1) return true;
        }
        return super.isUnlocked(owner);
    }

    @Override
    public boolean isTechnique() {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        if (owner.level().isClientSide) return;

        WorldSlashProjectile slash = new WorldSlashProjectile(owner, this.getPower(owner), (owner.isShiftKeyDown() ? 90.0F : 0.0F) + (HelperMethods.RANDOM.nextFloat() - 0.5F) * 60.0F);
        slash.setDeltaMovement(RotationUtil.getTargetAdjustedLookAngle(owner).scale(SPEED));
        owner.level().addFreshEntity(slash);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 500.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public Classification getClassification() {
        return Classification.SLASHING;
    }

    @Override
    public MenuType getMenuType(LivingEntity owner) {
        return MenuType.MELEE;
    }

    @Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onAbilityTrigger(AbilityTriggerEvent.Post event) {
            Ability ability = event.getAbility();

            if (ability != JJKAbilities.WORLD_SLASH.get()) return;

            LivingEntity owner = event.getEntity();

            for (Entity entity : owner.level().getEntities(owner, AABB.ofSize(owner.position(), RANGE, RANGE, RANGE))) {
                if (!(entity instanceof LivingEntity living) || !living.hasLineOfSight(owner)) continue;

                IJujutsuCapability cap = living.getCapability(JujutsuCapabilityHandler.INSTANCE);

                if (cap == null) continue;

                ISorcererData data = cap.getSorcererData();

                if (data.isUnlocked(JJKAbilities.WORLD_SLASH.get())) continue;
                if (!data.hasTechnique(JJKCursedTechniques.DISMANTLE_AND_CLEAVE.get())) continue;

                data.unlock(JJKAbilities.WORLD_SLASH.get());
            }
        }
    }
}
