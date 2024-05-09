package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.client.particle.ParticleColors;
import radon.jujutsu_kaisen.entity.registry.JJKEntities;
import radon.jujutsu_kaisen.util.EntityUtil;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

public class CursedEnergyImbuedItemProjectile extends ItemEntity {
    private static final double SPEED = 2.0D;

    public CursedEnergyImbuedItemProjectile(EntityType<? extends ItemEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public CursedEnergyImbuedItemProjectile(Entity owner, ItemStack stack) {
        super(JJKEntities.CURSED_ENERGY_IMBUED_ITEM.get(), owner.level());

        this.setThrower(owner);
        this.setItem(stack);

        Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
        EntityUtil.offset(this, look, new Vec3(owner.getX(), owner.getEyeY() - (this.getBbHeight() / 2.0F), owner.getZ()).add(look));

        this.setDeltaMovement(look.scale(SPEED));
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.level() instanceof ServerLevel level)) return;

        Entity owner = this.getOwner();

        if (owner != null) {
            for (int i = 0; i < 12; i++) {
                double x = this.getX() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (this.getBbWidth() * 1.5F);
                double y = this.getY() + HelperMethods.RANDOM.nextDouble() * this.getBbHeight();
                double z = this.getZ() + (HelperMethods.RANDOM.nextDouble() - 0.5D) * (this.getBbWidth() * 1.5F);
                double speed = (this.getBbHeight() * 0.3F) * HelperMethods.RANDOM.nextDouble();
                level.sendParticles(new CursedEnergyParticle.Options(ParticleColors.getCursedEnergyColor(owner), this.getBbWidth() * 0.5F,
                        0.2F, 6), x, y, z, 0, 0.0D, speed, 0.0D, 1.0D);
            }
        }
    }
}
