package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.TenShadowsMode;
import radon.jujutsu_kaisen.entity.base.TenShadowsSummon;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

import java.util.List;

public abstract class Summon<T extends Entity> extends Ability implements Ability.IToggled {
    private final Class<T> clazz;

    public Summon(Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract List<EntityType<?>> getTypes();

    protected boolean canTame() {
        return false;
    }

    public boolean canDie() {
        return false;
    }

    protected int getCount() {
        return 1;
    }

    public boolean display() {
        return true;
    }

    protected List<EntityType<?>> getFusions() {
        return List.of();
    }

    public abstract boolean isTenShadows();

    protected boolean shouldRemove() {
        return true;
    }

    protected boolean isBottomlessWell() {
        return false;
    }

    public boolean isTamed(LivingEntity owner) {
        if (!this.canTame()) return true;

        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        for (EntityType<?> type : this.getTypes()) {
            if (cap.hasTamed(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), type)) return true;
        }
        return false;
    }

    public boolean isDead(LivingEntity owner) {
        for (EntityType<?> type : this.getTypes()) {
            if (this.isDead(owner, type)) return true;
        }
        return false;
    }

    @Override
    public boolean isUnlocked(LivingEntity owner) {
        if (!super.isUnlocked(owner)) return false;

        if (!JJKAbilities.hasToggled(owner, this) && this.isTenShadows()) {
            if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getMode() != TenShadowsMode.SUMMON) return false;

            for (Ability ability : JJKAbilities.getToggled(owner)) {
                if (!(ability instanceof Summon<?> summon)) continue;

                for (EntityType<?> type : this.getTypes()) {
                    if (summon.getTypes().contains(type)) return false;
                    if (summon.getFusions().contains(type)) return false;
                }
                for (EntityType<?> fusion : this.getFusions()) {
                    if (!JJKAbilities.hasTamed(owner, fusion)) return false;
                    if (summon.getTypes().contains(fusion)) return false;
                    if (summon.getFusions().contains(fusion)) return false;
                }
            }

            for (EntityType<?> fusion : this.getFusions()) {
                if (this.isBottomlessWell() ? !JJKAbilities.hasTamed(owner, fusion) : !JJKAbilities.isDead(owner, fusion)) return false;
            }
        }
        return !this.isDead(owner);
    }

    protected boolean isDead(LivingEntity owner, EntityType<?> type) {
        if (!this.canDie()) return false;
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return false;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isDead(owner.level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE), type);
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
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.level instanceof ServerLevel level) {
            if (cap.hasSummonOfClass(level, this.clazz)) {
                return Status.FAILURE;
            }
        }
        return super.checkTriggerable(owner);
    }

    @Override
    public Status checkStatus(LivingEntity owner) {
        if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return Status.FAILURE;
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner.level instanceof ServerLevel level) {
            if (!cap.hasSummonOfClass(level, this.clazz)) {
                return Status.FAILURE;
            }
        }
        return super.checkStatus(owner);
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
                if (this.shouldRemove()) {
                    cap.unsummonByClass((ServerLevel) owner.level, this.clazz);
                } else {
                    cap.removeSummonByClass((ServerLevel) owner.level, this.clazz);
                }

                if (owner instanceof ServerPlayer player) {
                    PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
                }
            });
        }
    }

    @Override
    public float getRealCost(LivingEntity owner) {
        return this.isTenShadows() && JJKAbilities.hasToggled(owner, JJKAbilities.CHIMERA_SHADOW_GARDEN.get()) ? 0.0F : super.getRealCost(owner);
    }
}
