package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.CursedSpirit;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
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
    public @Nullable CursedTechnique getTechnique() {
        return CursedTechnique.CURSE_MANIPULATION;
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

    private void tryAddCurse(ISorcererData data, EntityType<?> type) {
        AbsorbedCurse curse = this.createCurse(type);

        if (curse == null) return;

        data.addCurse(curse);
    }

    @Override
    public void init(ISorcererData data) {
        super.init(data);

        Collection<RegistryObject<EntityType<?>>> registry = JJKEntities.ENTITIES.getEntries();

        for (RegistryObject<EntityType<?>> entry : registry) {
            EntityType<?> type = entry.get();

            if (type == JJKEntities.ABSORBED_PLAYER.get()) continue;

            if (type.create(this.level()) instanceof CursedSpirit curse && curse.getGrade().ordinal() < SorcererGrade.SPECIAL_GRADE.ordinal()) {
                for (int i = 0; i < SorcererGrade.values().length - curse.getGrade().ordinal(); i++) {
                    this.tryAddCurse(data, type);
                }
            }
        }
        this.tryAddCurse(data, JJKEntities.KUCHISAKE_ONNA.get());
        this.tryAddCurse(data, JJKEntities.DINO_CURSE.get());
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();

        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(JJKItems.PLAYFUL_CLOUD.get()));
    }
}
