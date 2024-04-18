package radon.jujutsu_kaisen.entity.sorcerer;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.mimicry.IMimicryData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.JJKEntityDataSerializers;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.base.TenShadowsSummon;
import radon.jujutsu_kaisen.util.EntityUtil;

import java.util.*;

public class SukunaEntity extends SorcererEntity {
    private static final int TAMING_CHANCE = 10 * 20;

    private static final EntityDataAccessor<String> DATA_ENTITY = SynchedEntityData.defineId(SukunaEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Optional<CompoundTag>> DATA_PLAYER = SynchedEntityData.defineId(SukunaEntity.class, JJKEntityDataSerializers.OPTIONAL_COMPOUND_TAG.get());

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private LivingEntity cachedOwner;

    protected int fingers;
    private boolean vessel;

    @Nullable
    private GameType original;

    public SukunaEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);

        Arrays.fill(this.armorDropChances, 0.0F);
        Arrays.fill(this.handDropChances, 0.0F);
    }

    public SukunaEntity(LivingEntity owner, int fingers, boolean vessel) {
        this(JJKEntities.SUKUNA.get(), owner.level());

        this.setOwner(owner);

        this.fingers = fingers;
        this.vessel = vessel;

        this.setEntity(owner.getType());

        if (owner instanceof Player player) {
            this.setPlayer(player.getGameProfile());
        }
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    protected boolean targetsSorcerers() {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.getTarget() != null) return;

        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ISorcererData data = cap.getSorcererData();

        if (!data.hasActiveTechnique(JJKCursedTechniques.TEN_SHADOWS.get())) return;

        for (Entity entity : data.getSummons()) {
            if (entity instanceof TenShadowsSummon) return;
        }

        if (this.random.nextInt(TAMING_CHANCE) == 0) {
            Summon<?> mahoraga = JJKAbilities.MAHORAGA.get();

            if (!mahoraga.isTamed(this)) {
                AbilityHandler.trigger(this, mahoraga);
                return;
            }

            for (Ability ability : JJKCursedTechniques.TEN_SHADOWS.get().getAbilities()) {
                if (!(ability instanceof Summon<?> summon) || summon.isTamed(this)) continue;

                AbilityHandler.trigger(this, ability);
            }
        }
    }

    public EntityType<?> getEntity() {
        return EntityType.byString(this.entityData.get(DATA_ENTITY)).orElseThrow();
    }

    public void setEntity(EntityType<?> type) {
        this.entityData.set(DATA_ENTITY, BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
    }

    public Optional<GameProfile> getPlayer() {
        Optional<CompoundTag> player = this.entityData.get(DATA_PLAYER);
        return player.map(NbtUtils::readGameProfile);
    }

    public void setPlayer(GameProfile profile) {
        this.entityData.set(DATA_PLAYER, Optional.of(NbtUtils.writeGameProfile(new CompoundTag(), profile)));
    }

    public GameType getOriginal(ServerPlayer player) {
        return this.original == null ? player.server.getDefaultGameType() : this.original;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_ENTITY, "");
        this.entityData.define(DATA_PLAYER, Optional.empty());
    }

    @Override
    public boolean is(@NotNull Entity pEntity) {
        return this == pEntity || pEntity == this.getOwner();
    }

    @Override
    public void tick() {
        LivingEntity owner = this.getOwner();

        if (!this.level().isClientSide && this.vessel && this.getEntity() == EntityType.PLAYER && (owner == null || owner.isRemoved() || !owner.isAlive())) {
            this.discard();
        } else {
            super.tick();

            if (this.level().isClientSide || this.isRemoved()) return;

            if (owner instanceof ServerPlayer player) {
                if (this.original == null) {
                    this.original = player.gameMode.getGameModeForPlayer();
                }
                player.setGameMode(GameType.SPECTATOR);
                player.setCamera(this);
            } else if (owner != null) {
                owner.discard();
            }
        }
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    @Override
    public float getExperience() {
        float min = SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
        float max = SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 4.0F;
        return min + (this.fingers * ((max - min) / 20));
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return JJKCursedTechniques.SHRINE.get();
    }

    @Override
    public Set<Ability> getUnlocked() {
        return Set.of(JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get(),
                JJKAbilities.RCT1.get(),  JJKAbilities.RCT2.get(), JJKAbilities.RCT3.get(), JJKAbilities.ABILITY_MODE.get());
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (this.ownerUUID != null) {
            pCompound.putUUID("owner", this.ownerUUID);
        }

        pCompound.putInt("fingers", this.fingers);
        pCompound.putBoolean("vessel", this.vessel);

        if (this.original != null) {
            pCompound.putInt("original", this.original.ordinal());
        }

        pCompound.putString("entity", BuiltInRegistries.ENTITY_TYPE.getKey(this.getEntity()).toString());
        this.getPlayer().ifPresent(player -> pCompound.put("player", NbtUtils.writeGameProfile(new CompoundTag(), player)));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.hasUUID("owner")) {
            this.ownerUUID = pCompound.getUUID("owner");
        }

        this.fingers = pCompound.getInt("fingers");
        this.vessel = pCompound.getBoolean("vessel");

        if (pCompound.contains("original")) {
            this.original = GameType.values()[pCompound.getInt("original")];
        }

        this.entityData.set(DATA_ENTITY, pCompound.getString("entity"));

        if (pCompound.contains("player")) {
            this.setPlayer(NbtUtils.readGameProfile(pCompound.getCompound("player")));
        }
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        if (pOwner != null) {
            this.ownerUUID = pOwner.getUUID();
            this.cachedOwner = pOwner;
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            this.cachedOwner = (LivingEntity) ((ServerLevel) this.level()).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        IJujutsuCapability src = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (src == null) return;

        ISorcererData sorcererSrc = src.getSorcererData();
        ITenShadowsData tenShadowsSrc = src.getTenShadowsData();
        IMimicryData mimicrySrc = src.getMimicryData();
        ICurseManipulationData curseManipulationSrc = src.getCurseManipulationData();

        IJujutsuCapability dst = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (dst == null) return;

        ISorcererData sorcererDst = dst.getSorcererData();
        ITenShadowsData tenShadowsDst = dst.getTenShadowsData();
        IMimicryData mimicryDst = dst.getMimicryData();
        ICurseManipulationData curseManipulationDst = dst.getCurseManipulationData();

        sorcererDst.unlockAll(sorcererSrc.getUnlocked());
        sorcererDst.setTraits(sorcererSrc.getTraits());

        ICursedTechnique technique = sorcererSrc.getTechnique();

        if (technique != null) {
            sorcererDst.addAdditional(technique);
        }
        tenShadowsDst.setTamed(tenShadowsSrc.getTamed());
        tenShadowsDst.setDead(tenShadowsSrc.getDead());
        mimicryDst.copy(mimicrySrc.getCopied());
        curseManipulationDst.absorb(curseManipulationSrc.getAbsorbed());
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (owner instanceof ServerPlayer player) {
            player.setGameMode(this.original == null ? player.server.getDefaultGameType() : this.original);
        }

        IJujutsuCapability srcCap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (srcCap == null) return;

        ITenShadowsData srcData = srcCap.getTenShadowsData();

        IJujutsuCapability dstCap = owner.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (dstCap == null) return;

        ITenShadowsData dstData = dstCap.getTenShadowsData();

        dstData.setTamed(srcData.getTamed());
        dstData.setDead(srcData.getDead());
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);

        LivingEntity owner = this.getOwner();

        if (owner != null) {
            owner.kill();
        }

        if (!(this instanceof HeianSukunaEntity)) {
            if (!this.vessel) {
                EntityUtil.convertTo(this, new HeianSukunaEntity(this.level(), this.fingers), true, false);
            }
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);

        LivingEntity owner = (LivingEntity) this.level().getEntity(pPacket.getData());

        if (owner != null) {
            this.setOwner(owner);
        }
    }
}
