package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.registry.JJKAbilities;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.entity.projectile.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.EntityUtil;

public class ForestWaveEntity extends JujutsuProjectile {
    private static final float DAMAGE = 10.0F;
    private static final int DURATION = 5 * 20;

    public ForestWaveEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ForestWaveEntity(LivingEntity owner, float power) {
        super(JJKEntities.FOREST_WAVE.get(), owner.level(), owner, power);
    }

    @Override
    public void push(@NotNull Entity pEntity) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.level().isClientSide) return;

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        for (Entity entity : EntityUtil.getTouchableEntities(Entity.class, this.level(), owner, this.getBoundingBox().inflate(1.0D))) {
            if (!entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.FOREST_WAVE.get()), DAMAGE * this.getPower())) continue;

            entity.setDeltaMovement(this.position().subtract(entity.position()).normalize().reverse());
            entity.hurtMarked = true;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getTime() >= DURATION) {
            this.discard();
        }
    }
}
