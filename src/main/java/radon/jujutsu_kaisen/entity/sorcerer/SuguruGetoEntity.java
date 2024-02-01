package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.CurseManipulationDataHandler;
import radon.jujutsu_kaisen.capability.data.curse_manipulation.ICurseManipulationData;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.cursed_technique.JJKCursedTechniques;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.cursed_technique.base.ICursedTechnique;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.item.JJKItems;

import java.util.Collection;

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
        ISorcererData cap = curse.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return new AbsorbedCurse(type.getDescription(), type, cap.serializeNBT());
    }

    private void tryAddCurse(EntityType<?> type) {
        ICurseManipulationData cap = this.getCapability(CurseManipulationDataHandler.INSTANCE).resolve().orElseThrow();

        AbsorbedCurse curse = this.createCurse(type);

        if (curse == null) return;

        cap.addCurse(curse);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));

        Collection<RegistryObject<EntityType<?>>> registry = JJKEntities.ENTITIES.getEntries();

        for (RegistryObject<EntityType<?>> entry : registry) {
            EntityType<?> type = entry.get();

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
