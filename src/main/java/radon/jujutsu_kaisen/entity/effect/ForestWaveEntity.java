package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.damage.JJKDamageSources;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.UUID;

public class ForestWaveEntity extends JujutsuProjectile {
    private static final float DAMAGE = 10.0F;

    private boolean damage;

    public ForestWaveEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ForestWaveEntity(LivingEntity owner, float power) {
        super(JJKEntities.FOREST_WAVE.get(), owner.level(), owner, power);
    }

    public void setDamage(boolean damage) {
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        if (this.level().isClientSide) return;

        if (!this.damage) return;

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        for (Entity entity : HelperMethods.getEntityCollisions(this.level(), this.getBoundingBox())) {
            if ((entity instanceof LivingEntity living && !owner.canAttack(living)) || entity == owner || entity instanceof ForestWaveEntity)
                continue;

            entity.setDeltaMovement(this.position().subtract(entity.position()).normalize().reverse());
            entity.hurtMarked = true;

            entity.hurt(JJKDamageSources.indirectJujutsuAttack(this, owner, JJKAbilities.FOREST_WAVE.get()), DAMAGE * getPower());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.getOwner() instanceof LivingEntity owner)) return;

        if (!JJKAbilities.isChanneling(owner, JJKAbilities.FOREST_WAVE.get())) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        pCompound.putBoolean("damage", this.damage);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        this.damage = pCompound.getBoolean("damage");
    }
}
