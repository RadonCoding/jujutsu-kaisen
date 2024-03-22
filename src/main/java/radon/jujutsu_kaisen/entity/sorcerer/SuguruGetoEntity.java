package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import radon.jujutsu_kaisen.data.capability.JujutsuCapabilityHandler;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

// Attack non-sorcerers
public class SuguruGetoEntity extends SorcererEntity {
    public SuguruGetoEntity(EntityType<? extends PathfinderMob> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Override
    protected boolean isCustom() {
        return false;
    }

    @Override
    public float getExperience() {
        return SorcererGrade.SPECIAL_GRADE.getRequiredExperience();
    }

    @Override
    public @Nullable ICursedTechnique getTechnique() {
        return JJKCursedTechniques.CURSE_MANIPULATION.get();
    }

    @Override
    public JujutsuType getJujutsuType() {
        return JujutsuType.SORCERER;
    }


    @Nullable
    private AbsorbedCurse createCurse(EntityType<?> type) {
        if (!(type.create(this.level()) instanceof CursedSpirit curse)) return null;

        curse.init();

        IJujutsuCapability cap = curse.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return null;

        ISorcererData data = cap.getSorcererData();

        return new AbsorbedCurse(type.getDescription(), type, data.serializeNBT());
    }

    private void tryAddCurse(EntityType<?> type) {
        IJujutsuCapability cap = this.getCapability(JujutsuCapabilityHandler.INSTANCE);

        if (cap == null) return;

        ICurseManipulationData data = cap.getCurseManipulationData();

        AbsorbedCurse curse = this.createCurse(type);

        if (curse == null) return;

        data.addCurse(curse);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));

        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
            if (type == JJKEntities.ABSORBED_PLAYER.get()) continue;

            if (type.create(this.level()) instanceof CursedSpirit curse && curse.getGrade().ordinal() < SorcererGrade.SPECIAL_GRADE.ordinal()) {
                for (int i = 0; i < SorcererGrade.values().length - curse.getGrade().ordinal(); i++) {
                    this.tryAddCurse(type);
                }
            }
        }
        this.tryAddCurse(JJKEntities.KUCHISAKE_ONNA.get());
        this.tryAddCurse(JJKEntities.DINO_CURSE.get());
    }
}
