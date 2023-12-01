package radon.jujutsu_kaisen.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class BurntEntity extends LivingEntity {
    private static final EntityDataAccessor<String> TRAPPED_ENTITY_TYPE = SynchedEntityData.defineId(BurntEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<CompoundTag> TRAPPED_ENTITY_DATA = SynchedEntityData.defineId(BurntEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_WIDTH = SynchedEntityData.defineId(BurntEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_HEIGHT = SynchedEntityData.defineId(BurntEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAPPED_ENTITY_SCALE = SynchedEntityData.defineId(BurntEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> CRACK_AMOUNT = SynchedEntityData.defineId(BurntEntity.class, EntityDataSerializers.INT);
    private EntityDimensions size = EntityDimensions.fixed(0.5F, 0.5F);

    public BurntEntity(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    public static BurntEntity create(LivingEntity owner) {
        BurntEntity statue = JJKEntities.BURNT.get().create(owner.level());
        CompoundTag nbt = new CompoundTag();

        if (!(owner instanceof Player)) {
            owner.saveWithoutId(nbt);
        }
        statue.setTrappedTag(nbt);
        statue.setTrappedEntityTypeString(ForgeRegistries.ENTITY_TYPES.getKey(owner.getType()).toString());
        statue.setTrappedEntityWidth(owner.getBbWidth());
        statue.setTrappedHeight(owner.getBbHeight());
        statue.setTrappedScale(owner.getScale());
        return statue;
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    public void push(@NotNull Entity entityIn) {
    }

    @Override
    public void baseTick() {

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TRAPPED_ENTITY_TYPE, "minecraft:pig");
        this.entityData.define(TRAPPED_ENTITY_DATA, new CompoundTag());
        this.entityData.define(TRAPPED_ENTITY_WIDTH, 0.5F);
        this.entityData.define(TRAPPED_ENTITY_HEIGHT, 0.5F);
        this.entityData.define(TRAPPED_ENTITY_SCALE, 1F);
        this.entityData.define(CRACK_AMOUNT, 0);
    }

    public EntityType<?> getTrappedEntityType() {
        return EntityType.byString(this.getTrappedEntityTypeString()).orElseThrow();
    }

    public String getTrappedEntityTypeString() {
        return this.entityData.get(TRAPPED_ENTITY_TYPE);
    }

    public void setTrappedEntityTypeString(String string) {
        this.entityData.set(TRAPPED_ENTITY_TYPE, string);
    }

    public CompoundTag getTrappedTag() {
        return this.entityData.get(TRAPPED_ENTITY_DATA);
    }

    public void setTrappedTag(CompoundTag tag) {
        this.entityData.set(TRAPPED_ENTITY_DATA, tag);
    }

    public float getTrappedWidth() {
        return this.entityData.get(TRAPPED_ENTITY_WIDTH);
    }

    public void setTrappedEntityWidth(float size) {
        this.entityData.set(TRAPPED_ENTITY_WIDTH, size);
    }

    public float getTrappedHeight() {
        return this.entityData.get(TRAPPED_ENTITY_HEIGHT);
    }

    public void setTrappedHeight(float size) {
        this.entityData.set(TRAPPED_ENTITY_HEIGHT, size);
    }

    public float getTrappedScale() {
        return this.entityData.get(TRAPPED_ENTITY_SCALE);
    }

    public void setTrappedScale(float size) {
        this.entityData.set(TRAPPED_ENTITY_SCALE, size);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putFloat("width", this.getTrappedWidth());
        tag.putFloat("height", this.getTrappedHeight());
        tag.putFloat("scale", this.getTrappedScale());
        tag.putString("type", this.getTrappedEntityTypeString());
        tag.put("entity", this.getTrappedTag());
    }

    @Override
    public float getScale() {
        return this.getTrappedScale();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setTrappedEntityWidth(tag.getFloat("width"));
        this.setTrappedHeight(tag.getFloat("height"));
        this.setTrappedScale(tag.getFloat("scale"));
        this.setTrappedEntityTypeString(tag.getString("type"));

        if (tag.contains("entity")) {
            this.setTrappedTag(tag.getCompound("entity"));
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return source == this.damageSources().fellOutOfWorld();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose poseIn) {
        return this.size;
    }

    @Override
    public void tick() {
        super.tick();

        this.setYRot(this.yBodyRot);
        this.yHeadRot = this.getYRot();

        if (Math.abs(this.getBbWidth() - getTrappedWidth()) > 0.01 || Math.abs(this.getBbHeight() - getTrappedHeight()) > 0.01) {
            double prevX = this.getX();
            double prevZ = this.getZ();
            this.size = EntityDimensions.scalable(getTrappedWidth(), getTrappedHeight());
            this.refreshDimensions();
            this.setPos(prevX, this.getY(), prevZ);
        }

        int count = (int) (this.getBbWidth() * this.getBbHeight()) * 8;

        for (int i = 0; i < count; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5D) * (this.getBbWidth() * 2) - this.getLookAngle().scale(0.35D).x();
            double y = this.getY() + this.random.nextDouble() * this.getBbHeight();
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * (this.getBbWidth() * 2) - this.getLookAngle().scale(0.35D).z();
            this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, this.random.nextDouble() * 0.1D, 0.0D);
        }
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return ImmutableList.of();
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {

    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return true;
    }
}