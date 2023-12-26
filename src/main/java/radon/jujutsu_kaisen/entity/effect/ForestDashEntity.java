package radon.jujutsu_kaisen.entity.effect;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.JujutsuProjectile;

public class ForestDashEntity extends JujutsuProjectile {
    public static final float SIZE = 3.0F;
    private static final int DURATION = 5 * 20;

    public ForestDashEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ForestDashEntity(LivingEntity owner) {
        super(JJKEntities.FOREST_DASH.get(), owner.level(), owner);
    }

    @Override
    public void push(@NotNull Entity pEntity) {

    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected boolean isProjectile() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

       if (this.getTime() >= DURATION) {
           this.discard();
       }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        int i = pPacket.getId();
        double d0 = pPacket.getX();
        double d1 = pPacket.getY();
        double d2 = pPacket.getZ();
        this.syncPacketPositionCodec(d0, d1, d2);
        this.moveTo(d0, d1, d2);
        this.setXRot(pPacket.getXRot());
        this.setYRot(pPacket.getYRot());
        this.setId(i);
        this.setUUID(pPacket.getUUID());
    }
}
