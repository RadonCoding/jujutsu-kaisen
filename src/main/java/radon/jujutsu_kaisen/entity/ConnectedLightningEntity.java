package radon.jujutsu_kaisen.entity;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import radon.jujutsu_kaisen.entity.effect.LightningEntity;

public class ConnectedLightningEntity extends LightningEntity {
    private Vec3 start;
    private Vec3 end;

    public ConnectedLightningEntity(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public ConnectedLightningEntity(LivingEntity owner, float power, Vec3 start, Vec3 end) {
        super(owner, power);

        this.start = start;
        this.end = end;
    }

    @Override
    protected Vec3 calculateStartPos() {
        return this.start;
    }

    @Override
    protected void calculateEndPos() {
        this.endPosX = this.end.x;
        this.endPosY = this.end.y;
        this.endPosZ = this.end.z;
    }
}
