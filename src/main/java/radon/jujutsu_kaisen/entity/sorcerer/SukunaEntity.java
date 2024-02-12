package radon.jujutsu_kaisen.entity.sorcerer;

import com.mojang.authlib.GameProfile;
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
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
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

        this.entityData.set(DATA_ENTITY, EntityType.getKey(owner.getType()).toString());

        if (owner instanceof Player player) {
            this.entityData.set(DATA_PLAYER, Optional.of(NbtUtils.writeGameProfile(new CompoundTag(), player.getGameProfile())));
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

        if (!JJKAbilities.hasActiveTechnique(this, JJKCursedTechniques.TEN_SHADOWS.get())) return;

        ISorcererData data = this.getData(JJKAttachmentTypes.SORCERER);


        for (Entity entity : data.getSummons()) {
            if (entity instanceof TenShadowsSummon) return;
        }

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

    public EntityType<?> getKey() {
        return EntityType.byString(this.entityData.get(DATA_ENTITY)).orElseThrow();
    }

    public GameProfile getPlayer() {
        return NbtUtils.readGameProfile(this.entityData.get(DATA_PLAYER).orElseThrow());
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

        if (!this.level().isClientSide && this.vessel && this.getKey() == EntityType.PLAYER && (owner == null || owner.isRemoved() || !owner.isAlive())) {
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
        float max = SorcererGrade.SPECIAL_GRADE.getRequiredExperience() * 3.0F;
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience() + (this.fingers * (max / 20));
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return JJKCursedTechniques.DISMANTLE_AND_CLEAVE.get();
    }

    @Override
    public List<Ability> getUnlocked() {
        return List.of(JJKAbilities.MALEVOLENT_SHRINE.get(), JJKAbilities.DOMAIN_AMPLIFICATION.get(),
                JJKAbilities.RCT1.get(),  JJKAbilities.RCT2.get(), JJKAbilities.RCT3.get());
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

        pCompound.putString("entity",  this.entityData.get(DATA_ENTITY));
        this.entityData.get(DATA_PLAYER).ifPresent(player -> pCompound.put("player", player));
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
            this.entityData.set(DATA_PLAYER, Optional.of(pCompound.getCompound("player")));
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

        ISorcererData sorcererSrc = owner.getData(JJKAttachmentTypes.SORCERER);
        ITenShadowsData tenShadowsSrc = owner.getData(JJKAttachmentTypes.TEN_SHADOWS);

        if (sorcererSrc == null || tenShadowsSrc == null) return;

        ISorcererData sorcererDst = this.getData(JJKAttachmentTypes.SORCERER);
        ITenShadowsData tenShadowsDst = this.getData(JJKAttachmentTypes.TEN_SHADOWS);

        if (sorcererDst == null || tenShadowsDst == null) return;

        sorcererDst.setTraits(sorcererSrc.getTraits());
        sorcererDst.setAdditional(sorcererSrc.getTechnique());
        tenShadowsDst.setTamed(tenShadowsSrc.getTamed());
        tenShadowsDst.setDead(tenShadowsSrc.getDead());
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();

        LivingEntity owner = this.getOwner();

        if (owner == null) return;

        if (owner instanceof ServerPlayer player) {
            player.setGameMode(this.original == null ? player.server.getDefaultGameType() : this.original);
        }

        ITenShadowsData srcData = this.getData(JJKAttachmentTypes.TEN_SHADOWS);
        ITenShadowsData dstData = owner.getData(JJKAttachmentTypes.TEN_SHADOWS);

        if (srcData == null || dstData == null) return;

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
