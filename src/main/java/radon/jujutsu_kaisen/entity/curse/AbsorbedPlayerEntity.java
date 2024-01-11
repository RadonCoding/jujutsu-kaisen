package radon.jujutsu_kaisen.entity.curse;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.JJKEntityDataSerializers;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.util.RotationUtil;

import java.util.Optional;

public class AbsorbedPlayerEntity extends CursedSpirit {
    private static final EntityDataAccessor<Optional<CompoundTag>> DATA_PLAYER = SynchedEntityData.defineId(AbsorbedPlayerEntity.class, JJKEntityDataSerializers.OPTIONAL_COMPOUND_TAG.get());

    public AbsorbedPlayerEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public AbsorbedPlayerEntity(LivingEntity owner, @Nullable GameProfile profile) {
        super(JJKEntities.ABSORBED_PLAYER.get(), owner.level());

        this.setTame(true);
        this.setOwner(owner);

        if (profile != null) {
            this.entityData.set(DATA_PLAYER, Optional.of(NbtUtils.writeGameProfile(new CompoundTag(), profile)));
        }

        Vec3 pos = owner.position().subtract(RotationUtil.getTargetAdjustedLookAngle(owner)
                .multiply(this.getBbWidth(), 0.0D, this.getBbWidth()));
        this.moveTo(pos.x, pos.y, pos.z, RotationUtil.getTargetAdjustedYRot(owner), RotationUtil.getTargetAdjustedXRot(owner));
    }

    @Override
    public @NotNull Component getName() {
        return Component.literal(this.getPlayer().getName());
    }

    public GameProfile getPlayer() {
        return NbtUtils.readGameProfile(this.entityData.get(DATA_PLAYER).orElseThrow());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_PLAYER, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        this.entityData.get(DATA_PLAYER).ifPresent(player -> pCompound.put("player", player));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("player")) {
            this.entityData.set(DATA_PLAYER, Optional.of(pCompound.getCompound("player")));
        }
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public boolean canPerformSorcery() {
        return true;
    }

    @Override
    public float getExperience() {
        return 0;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }
}
