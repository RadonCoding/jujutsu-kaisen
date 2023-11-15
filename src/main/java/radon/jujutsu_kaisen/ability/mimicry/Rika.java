package radon.jujutsu_kaisen.ability.mimicry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class Rika extends Summon<RikaEntity> {
    private static final float AMOUNT = 100.0F;
    private static final int INTERVAL = 5 * 20;

    public Rika() {
        super(RikaEntity.class);
    }

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (ownerCap.hasToggled(this)) return target != null;

        if (target != null) {
            if (owner.getHealth() / owner.getMaxHealth() <= 0.5F) return true;
            if (!target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
            ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            return HelperMethods.getGrade(targetCap.getExperience()).ordinal() > SorcererGrade.GRADE_1.ordinal();
        }
        return false;
    }

    @Override
    public void run(LivingEntity owner) {

    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.RIKA.get());
    }

    @Override
    public boolean isTenShadows() {
        return false;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    protected RikaEntity summon(int index, LivingEntity owner) {
        return new RikaEntity(owner);
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public boolean isTechnique() {
        return true;
    }
}
