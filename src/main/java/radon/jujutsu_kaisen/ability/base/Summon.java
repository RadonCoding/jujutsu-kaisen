package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.entity.TenShadowsSummon;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Summon<T extends Entity> extends Ability implements Ability.IToggled {
    private final Class<T> clazz;

    public Summon(Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract EntityType<T> getType();

    protected boolean canTame() {
        return false;
    }

    public boolean canDie() {
        return false;
    }

    protected int getCount() {
        return 1;
    }

    public abstract boolean isTenShadows();

    public boolean isTamed(LivingEntity owner) {
        if (!this.canTame()) return false;

        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.hasTamed(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), this.getType())));
        return result.get();
    }

    public boolean isDead(LivingEntity owner) {
        return this.isDead(owner, this.getType());
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (this.isTenShadows()) {
            AtomicBoolean result = new AtomicBoolean();

            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                    result.set(cap.getMode() == TenShadowsMode.SUMMON));

            if (!result.get()) {
                return false;
            }
        }
        return !this.isDead(owner);
    }

    protected boolean isDead(LivingEntity owner, EntityType<?> type) {
        if (!this.canDie()) return false;

        AtomicBoolean result = new AtomicBoolean();

        owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
                result.set(cap.isDead(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), type)));
        return result.get();
    }


    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (this.getActivationType(owner) == ActivationType.INSTANT) {
            if (!owner.level.isClientSide) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                    for (int i = 0; i < this.getCount(); i++) {
                        T summon = this.summon(i, owner);
                        owner.level.addFreshEntity(summon);
                        cap.addSummon(summon);
                    }
                });
            }
        }
    }

    @Override
    public Status checkTriggerable(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (cap.hasSummonOfClass(level, this.clazz)) {
                    result.set(true);
                }
            });
        }
        return result.get() ? Status.FAILURE : super.checkTriggerable(owner);
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        AtomicBoolean result = new AtomicBoolean();

        if (owner.level instanceof ServerLevel level) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                if (!cap.hasSummonOfClass(level, this.clazz)) {
                    result.set(true);
                }
            });
        }
        return result.get() ? Status.FAILURE : super.checkStatus(owner);
    }

    protected abstract T summon(int index, LivingEntity owner);

    public void spawn(LivingEntity owner, boolean clone) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                for (int i = 0; i < this.getCount(); i++) {
                    T summon = this.summon(i, owner);

                    if (summon instanceof TenShadowsSummon) {
                        ((TenShadowsSummon) summon).setClone(clone);
                    }
                    owner.level.addFreshEntity(summon);
                    cap.addSummon(summon);
                }
            });
        }
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        this.spawn(owner, false);
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        if (!owner.level.isClientSide) {
            owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap -> {
                cap.unsummonByClass((ServerLevel) owner.level, this.clazz);

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        }
    }
}
