package radon.jujutsu_kaisen.ability.base;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsMode;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
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

    public boolean display() {
        return true;
    }

    public boolean isTotality() {
        return false;
    }

    public List<EntityType<?>> getFusions() {
        return List.of();
    }

    public abstract boolean isTenShadows();

    public boolean isSpecificFusion() {
        return true;
    }

    protected boolean shouldRemove() {
        return true;
    }

    protected boolean isBottomlessWell() {
        return false;
    }

    public boolean isTamed(LivingEntity owner) {
        if (!this.canTame()) return true;

        ITenShadowsData cap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();

        for (EntityType<?> type : this.getTypes()) {
            if (cap.hasTamed(owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), type)) return true;
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
    public boolean isValid(LivingEntity owner) {
        if (!super.isValid(owner)) return false;

        if (!JJKAbilities.hasToggled(owner, this) && this.isTenShadows()) {
            ITenShadowsData cap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();

            if (cap.getMode() != TenShadowsMode.SUMMON) return false;

            Registry<EntityType<?>> registry = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);

            for (Ability ability : JJKAbilities.getToggled(owner)) {
                if (!(ability instanceof Summon<?> summon)) continue;

                for (EntityType<?> type : this.getTypes()) {
                    if (summon.getTypes().contains(type)) return false;
                    if (summon.getFusions().contains(type)) return false;
                }
                for (EntityType<?> fusion : this.getFusions()) {
                    if (!cap.hasTamed(registry, fusion)) return false;
                    if (summon.getTypes().contains(fusion)) return false;
                    if (summon.getFusions().contains(fusion)) return false;
                }
            }

            List<EntityType<?>> fusions = this.getFusions();

            int dead = 0;

            for (int i = 0; i < fusions.size(); i++) {
                if (this.isBottomlessWell()) {
                    if (cap.isDead(registry, fusions.get(i)) || !cap.hasTamed(registry, fusions.get(i))) {
                        return false;
                    }
                } else {
                    if (this.isSpecificFusion()) {
                        if ((i == 0) == cap.isDead(registry, fusions.get(i))) {
                            return false;
                        }
                    } else {
                        if (cap.isDead(registry, fusions.get(i))) {
                            dead++;
                        }
                    }
                }
            }

            if (!this.isSpecificFusion() && (dead == 0 || dead == fusions.size())) {
                return false;
            }
        }
        return !this.isDead(owner);
    }

    protected boolean isDead(LivingEntity owner, EntityType<?> type) {
        if (!this.canDie()) return false;
        ITenShadowsData cap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
        return cap.isDead(owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE), type);
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return ActivationType.TOGGLED;
    }

    @Override
    public void run(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        if (this.getActivationType(owner) == ActivationType.INSTANT) {
            this.spawn(owner, false);
        }
    }

    @Override
    public Status isTriggerable(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if ((this.isTenShadows() || this.getActivationType(owner) == ActivationType.TOGGLED) && cap.hasSummonOfClass(this.clazz)) {
            return Status.FAILURE;
        }
        return super.isTriggerable(owner);
    }

    @Override
    public Status isStillUsable(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            if (!cap.hasSummonOfClass(this.clazz)) {
                return Status.FAILURE;
            }
        }
        return super.isStillUsable(owner);
    }

    protected abstract T summon(LivingEntity owner);

    public void spawn(LivingEntity owner, boolean clone) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        T summon = this.summon(owner);

        if (summon instanceof TenShadowsSummon) {
            ((TenShadowsSummon) summon).setClone(clone);
        }
        owner.level().addFreshEntity(summon);

        cap.addSummon(summon);
    }

    @Override
    public void onEnabled(LivingEntity owner) {
        if (owner.level().isClientSide) return;

        this.spawn(owner, false);

        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @Override
    public void onDisabled(LivingEntity owner) {
        ISorcererData cap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (this.shouldRemove()) {
            cap.unsummonByClass(this.clazz);
        } else {
            cap.removeSummonByClass(this.clazz);
        }

        if (owner instanceof ServerPlayer player) {
            PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
        }
    }

    @Override
    public float getRealCost(LivingEntity owner) {
        return this.isTenShadows() && this.isTamed(owner) && JJKAbilities.hasToggled(owner, JJKAbilities.CHIMERA_SHADOW_GARDEN.get()) ? 0.0F : super.getRealCost(owner);
    }
}
