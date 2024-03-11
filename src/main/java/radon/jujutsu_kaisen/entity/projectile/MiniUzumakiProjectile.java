package radon.jujutsu_kaisen.entity.projectile;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.effect.base.BeamEntity;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.SorcererUtil;

public class MiniUzumakiProjectile extends BeamEntity {
    public static final double RANGE = 16.0D;

    public MiniUzumakiProjectile(EntityType<? extends Projectile> pType, Level pLevel) {
        super(pType, pLevel);

        this.noCulling = true;
    }

    public MiniUzumakiProjectile(LivingEntity owner, float power) {
        this(JJKEntities.MINI_UZUMAKI.get(), owner.level());

        this.setOwner(owner);
        this.setPower(power);

        IJujutsuCapability cap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData ownerSorcererData = cap.getSorcererData();
        ICurseManipulationData ownerCurseManipulationData = cap.getCurseManipulationData();

        Entity weakest = null;

        for (Entity current : ownerSorcererData.getSummons()) {
            if (!(current instanceof CursedSpirit)) continue;

            if (weakest == null) {
                weakest = current;
                continue;
            }

            IJujutsuCapability weakestCap = weakest.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (weakestCap == null) continue;

            IJujutsuCapability currentCap = current.getCapability(JujutsuCapabilityHandler.INSTANCE);

            if (currentCap == null) continue;

            ISorcererData weakestData = weakestCap.getSorcererData();
            ISorcererData currentData = currentCap.getSorcererData();

            if (currentData.getExperience() < weakestData.getExperience()) weakest = current;
        }

        if (weakest == null) return;

        IJujutsuCapability weakestCap = weakest.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (weakestCap == null) return;

        ISorcererData weakestData = weakestCap.getSorcererData();

        this.setPower(Math.min(ownerSorcererData.getAbilityOutput() * 1.25F, this.getPower() + weakestData.getBaseOutput()));

        if (SorcererUtil.getGrade(weakestData.getExperience()).ordinal() >= SorcererGrade.SEMI_GRADE_1.ordinal() && weakestData.getTechnique() != null) {
            ownerCurseManipulationData.absorb(weakestData.getTechnique());

            if (owner instanceof ServerPlayer player) {
                PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerSorcererData.serializeNBT()), player);
            }
        }
        weakest.discard();
    }

    @Override
    public int getFrames() {
        return 3;
    }

    @Override
    public float getScale() {
        return 1.0F;
    }

    @Override
    protected double getRange() {
        return RANGE;
    }

    @Override
    protected float getDamage() {
        return 15.0F;
    }

    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    public int getCharge() {
        return 10;
    }

    @Override
    protected @Nullable Ability getSource() {
        return JJKAbilities.MINI_UZUMAKI.get();
    }
}
