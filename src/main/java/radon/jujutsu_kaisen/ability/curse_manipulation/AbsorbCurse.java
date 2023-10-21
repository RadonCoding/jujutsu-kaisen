package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.item.CursedSpiritOrbItem;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.util.HelperMethods;

public class AbsorbCurse extends Ability {
    private static final double RANGE = 5.0D;

    @Override
    public boolean isChantable() {
        return false;
    }

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
            ((ServerLevel) entity.level()).sendParticles(ParticleTypes.POOF, entity.getRandomX(1.0D), entity.getRandomY(), entity.getRandomZ(1.0D),
                    0, d0, d1, d2, 1.0D);
        }
    }

    public static boolean canAbsorb(LivingEntity owner, Entity entity) {
        return entity instanceof CursedSpirit curse && !curse.isTame() &&
                (JJKAbilities.getGrade(owner).ordinal() - curse.getGrade().ordinal() >= 2 || curse.getHealth() / owner.getMaxHealth() <= 0.1F);
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        if (this.getTarget(owner) instanceof CursedSpirit curse && !curse.isTame()) {
            owner.swing(InteractionHand.MAIN_HAND, true);

            Registry<EntityType<?>> registry = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
            ResourceLocation key = registry.getKey(curse.getType());

            if (key == null) return;

            if (!canAbsorb(owner, curse)) return;

            ItemStack stack = new ItemStack(JJKItems.CURSED_SPIRIT_ORB.get());
            CursedSpiritOrbItem.setKey(stack, key);

            if (owner instanceof Player player) {
                player.addItem(stack);
            } else {
                owner.setItemSlot(EquipmentSlot.MAINHAND, stack);
            }
            makePoofParticles(curse);
            curse.discard();
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
        return 100.0F;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }

}
