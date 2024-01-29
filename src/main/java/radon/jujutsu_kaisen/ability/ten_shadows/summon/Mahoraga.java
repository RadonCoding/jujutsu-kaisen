package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class Mahoraga extends Summon<MahoragaEntity> {
    public Mahoraga() {
        super(MahoragaEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        ITenShadowsData ownerCap = owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();

        if (!this.isTamed(owner)) {
            return target.getHealth() > owner.getHealth() * 4 || owner.getHealth() / owner.getMaxHealth() <= 0.1F;
        }

        if (JJKAbilities.hasToggled(owner, this)) {
            return HelperMethods.RANDOM.nextInt(20) != 0;
        }

        if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

            for (CursedTechnique technique : targetCap.getTechniques()) {
                if (ownerCap.isAdaptedTo(technique)) {
                    return true;
                }
            }
        }
        return HelperMethods.RANDOM.nextInt(10) == 0;
    }

    @Override
    public ActivationType getActivationType(LivingEntity owner) {
        return this.isTamed(owner) ? ActivationType.TOGGLED : ActivationType.INSTANT;
    }

    @Override
    public float getCost(LivingEntity owner) {
        return this.isTamed(owner) ? 0.3F : 1000.0F;
    }

    @Override
    public int getCooldown() {
        return 30 * 20;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.MAHORAGA.get());
    }

    @Override
    protected boolean canTame() {
        return true;
    }

    @Override
    public boolean canDie() {
        return true;
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    protected MahoragaEntity summon(LivingEntity owner) {
        return new MahoragaEntity(owner, this.isTamed(owner));
    }


}
