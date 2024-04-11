package radon.jujutsu_kaisen.entity.idle_transfiguration.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

public abstract class TransfiguredSoulVariantEntity extends TransfiguredSoulEntity {
    public static final EntityDataAccessor<Variant> DATA_VARIANT = SynchedEntityData.defineId(TransfiguredSoulVariantEntity.class, EntityDataSerializer.simpleEnum(Variant.class));

    protected TransfiguredSoulVariantEntity(EntityType<? extends TamableAnimal> pType, Level pLevel) {
        super(pType, pLevel);
    }

    public TransfiguredSoulVariantEntity(EntityType<? extends TamableAnimal> pType, LivingEntity owner) {
        super(pType, owner);

        this.setVariant(HelperMethods.randomEnum(Variant.class));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SorcererEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0F)
                .add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(DATA_VARIANT, Variant.ONE);
    }

    public Variant getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    private void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("variant", this.getVariant().ordinal());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        this.setVariant(Variant.values()[pCompound.getInt("variant")]);
    }
}
