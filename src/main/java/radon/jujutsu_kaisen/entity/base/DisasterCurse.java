package radon.jujutsu_kaisen.entity.base;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererGrade;
import radon.jujutsu_kaisen.util.HelperMethods;

public abstract class DisasterCurse extends CursedSpirit {
    protected DisasterCurse(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor pLevel, @NotNull MobSpawnType pSpawnReason) {
        if (pSpawnReason == MobSpawnType.NATURAL || pSpawnReason == MobSpawnType.CHUNK_GENERATION) {
            if (this.random.nextInt(Mth.floor(RARITY * HelperMethods.getPower(this.getGrade().getRequiredExperience())) / (this.level().isNight() ? 2 : 1)) != 0) return false;
        }

        if (pLevel.getEntitiesOfClass(CursedSpirit.class, AABB.ofSize(this.position(), 64.0D,  16.0D, 64.0D)).size() > 0) return false;

        if (this.getGrade().ordinal() >= SorcererGrade.GRADE_1.ordinal()) {
            if (pLevel.getEntitiesOfClass(this.getClass(), AABB.ofSize(this.position(), 128.0D, 32.0D, 128.0D)).size() > 0) return false;
        }
        return this.getWalkTargetValue(this.blockPosition(), pLevel) >= 0.0F;
    }
}
