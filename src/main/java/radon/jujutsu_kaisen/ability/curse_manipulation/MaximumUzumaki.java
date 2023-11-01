package radon.jujutsu_kaisen.ability.curse_manipulation;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.projectile.MaximumUzumakiProjectile;

import java.util.Map;

public class MaximumUzumaki extends Ability {
    @Override
    public boolean isScalable() {
        return true;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        return owner.getHealth() / owner.getMaxHealth() <= 0.1F;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.INSTANT;
    }

    @Override
    public void run(LivingEntity owner) {
        owner.swing(InteractionHand.MAIN_HAND);

        MaximumUzumakiProjectile uzumaki = new MaximumUzumakiProjectile(owner, this.getPower(owner));
        owner.level().addFreshEntity(uzumaki);
    }

    @Override
    public boolean isValid(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        Registry<EntityType<?>> registry = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        Map<EntityType<?>, Integer> curses = cap.getCurses(registry);
        return !curses.isEmpty() && super.isValid(owner);
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    public int getCooldown() {
        return 60 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
