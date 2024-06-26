package radon.jujutsu_kaisen.entity.curse;


import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;
import radon.jujutsu_kaisen.entity.registry.JJKEntityDataSerializers;

import java.util.Optional;

public class AbsorbedPlayerEntity extends CursedSpirit {
    private static final EntityDataAccessor<Optional<GameProfile>> DATA_PLAYER = SynchedEntityData.defineId(AbsorbedPlayerEntity.class,
            JJKEntityDataSerializers.GAME_PROFILE.get());

    public AbsorbedPlayerEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    public @NotNull Component getName() {
        return Component.literal(this.getPlayer().getName());
    }

    public GameProfile getPlayer() {
        return this.entityData.get(DATA_PLAYER).orElseThrow();
    }

    public void setPlayer(GameProfile profile) {
        this.entityData.set(DATA_PLAYER, Optional.of(profile));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder pBuilder) {
        super.defineSynchedData(pBuilder);

        pBuilder.define(DATA_PLAYER, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.put("player", ExtraCodecs.GAME_PROFILE.encode(this.getPlayer(),
                this.registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).getOrThrow());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setPlayer(ExtraCodecs.GAME_PROFILE.parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE),
                pCompound.getCompound("player")).getOrThrow());
    }

    @Override
    public void init() {

    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public boolean hasMeleeAttack() {
        return true;
    }

    @Override
    public boolean hasArms() {
        return true;
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public boolean canChant() {
        return true;
    }

    @Override
    public float getExperience() {
        return 0;
    }

    @Override
    @Nullable
    public CursedTechnique getTechnique() {
        return null;
    }
}
