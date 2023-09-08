package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.DisplayType;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.util.HelperMethods;


public class AbsorbTechnique extends Ability {
    private static final double RANGE = 3.0D;

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        return false;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    private @Nullable Entity getTarget(LivingEntity owner) {
        if (HelperMethods.getLookAtHit(owner, RANGE) instanceof EntityHitResult hit) {
            return hit.getEntity();
        }
        return null;
    }

    private static void makePoofParticles(Entity entity) {
        for (int i = 0; i < 20; ++i) {
            double d0 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d1 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            double d2 = HelperMethods.RANDOM.nextGaussian() * 0.02D;
            ((ServerLevel) entity.level).sendParticles(ParticleTypes.POOF, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D),
                    0, d0, d1, d2, 1.0D);
        }
    }

    public static boolean canAbsorb(LivingEntity owner, Entity entity) {
        if (!(entity instanceof CursedSpirit curse) || !curse.isTame() || curse.getOwner() != owner) return false;

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (ownerCap.getAbsorbed().size() > 0) return false;

        if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData curseCap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        CursedTechnique current = ownerCap.getTechnique();
        CursedTechnique absorbed = curseCap.getTechnique();

        if (current == null || ownerCap.hasTechnique(absorbed)) return false;

        return !current.equals(absorbed);
    }

    @Override
    public void run(LivingEntity owner) {
        if (this.getTarget(owner) instanceof CursedSpirit curse) {
            owner.swing(InteractionHand.MAIN_HAND);

            Registry<EntityType<?>> registry = owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            ResourceLocation key = registry.getKey(curse.getType());

            if (key == null) return;

            if (!canAbsorb(owner, curse)) return;

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(ownerCap -> {
                curse.getCapability(SorcererDataHandler.INSTANCE).ifPresent(curseCap -> {
                    CursedTechnique absorbed = curseCap.getTechnique();

                    ownerCap.absorb(absorbed);

                    if (!owner.level.isClientSide) {
                        makePoofParticles(curse);
                    }
                    curse.discard();
                });
            });
        }
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        Entity target = this.getTarget(owner);

        if (!canAbsorb(owner, target)) {
            return Status.FAILURE;
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.SCROLL;
    }
}
