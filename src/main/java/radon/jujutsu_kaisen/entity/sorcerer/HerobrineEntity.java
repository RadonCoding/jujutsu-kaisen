package radon.jujutsu_kaisen.entity.sorcerer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.SorcererEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.Map;

public class HerobrineEntity extends SorcererEntity {
    public HerobrineEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor pLevel, @NotNull MobSpawnType pSpawnReason) {
        return this.getWalkTargetValue(this.blockPosition(), pLevel) >= 0.0F;
    }

    @Override
    public void setTarget(@Nullable LivingEntity pTarget) {
        super.setTarget(pTarget);

        if (pTarget != null) {
            if (!pTarget.getCapability(SorcererDataHandler.INSTANCE).isPresent()) return;

            ISorcererData src = pTarget.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            ISorcererData dst = this.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            dst.deserializeNBT(src.serializeNBT());
        }
    }

    @Override
    public void init(ISorcererData data) {
    }

    @Override
    public SorcererGrade getGrade() {
        return null;
    }

    @Override
    public @Nullable CursedTechnique getTechnique() {
        return null;
    }

    @Override
    public JujutsuType getJujutsuType() {
        return null;
    }

    @Override
    public @Nullable Ability getDomain() {
        return null;
    }
}
