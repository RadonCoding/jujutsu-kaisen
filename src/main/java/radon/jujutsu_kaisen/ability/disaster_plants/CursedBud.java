package radon.jujutsu_kaisen.ability.disaster_plants;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.cursed_technique.registry.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.entity.projectile.CursedBudProjectile;
import radon.jujutsu_kaisen.util.DamageUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class CursedBud extends Ability {


    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return HelperMethods.RANDOM.nextInt(20) == 0 && target != null && owner.hasLineOfSight(target);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        CursedBudProjectile bud = new CursedBudProjectile(owner, this.getOutput(owner));

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        Vec3 spawn = new Vec3(owner.getX(), owner.getEyeY() - (bud.getBbHeight() / 2.0F), owner.getZ()).add(look);
        bud.moveTo(spawn.x, spawn.y, spawn.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));

        owner.level().addFreshEntity(bud);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 50.0F;
    }

    @EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingDamageEvent event) {
            DamageSource source = event.getSource();
            if (!(source.getEntity() instanceof LivingEntity attacker)) return;

            if (attacker.level().isClientSide) return;

            LivingEntity victim = event.getEntity();

            if (!DamageUtil.isMelee(source)) return;

            IJujutsuCapability cap = attacker.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (cap == null) return;

            ISorcererData data = cap.getSorcererData();

            if (!data.hasActiveTechnique(JJKCursedTechniques.DISASTER_PLANTS.get())) return;

            for (CursedBudProjectile bud : victim.level().getEntitiesOfClass(CursedBudProjectile.class, AABB.ofSize(victim.position(), 8.0D, 8.0D, 8.0D))) {
                if (bud.getOwner() != attacker) continue;
                bud.implant(victim);
                return;
            }
        }
    }
}
