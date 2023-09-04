package radon.jujutsu_kaisen.ability.rika;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Rika extends Summon<RikaEntity> {
    private static final float AMOUNT = 100.0F;
    private static final int INTERVAL = 5 * 20;

    public Rika() {
        super(RikaEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (JJKAbilities.hasToggled(owner, this)) return target != null;
        if (owner.getHealth() / owner.getMaxHealth() <= 0.5F) return true;

        AtomicBoolean result = new AtomicBoolean();

        if (target != null) {
            target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.getGrade().ordinal() > SorcererGrade.GRADE_1.ordinal()));
        }
        return result.get();
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level.getGameTime() % INTERVAL != 0) return;

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(ownerCap -> {
                RikaEntity rika = ownerCap.getSummonByClass(level, RikaEntity.class);

                if (rika == null) return;

                rika.getCapability(SorcererDataHandler.INSTANCE).ifPresent(summonCap -> {
                    if (summonCap.getEnergy() > AMOUNT) {
                        ownerCap.addEnergy(AMOUNT);
                        summonCap.useEnergy(AMOUNT);

                        if (owner instanceof ServerPlayer player) {
                            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), player);
                        }
                    }
                });
            });
        }
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
    public boolean isTamed(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.getGrade().ordinal() >= SorcererGrade.GRADE_1.ordinal()));
        return result.get();
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0;
    }

    @Override
    protected RikaEntity summon(int index, LivingEntity owner) {
        return new RikaEntity(owner, this.isTamed(owner));
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
