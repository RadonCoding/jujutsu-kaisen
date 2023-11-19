package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

import java.util.List;

public class Mahoraga extends Summon<MahoragaEntity> {
    public Mahoraga() {
        super(MahoragaEntity.class);
    }

    @Override
    public boolean isScalable() {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        if (target == null) return false;

        ISorcererData ownerCap = owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

        if (this.isTamed(owner)) {
            if (JJKAbilities.hasToggled(owner, this)) {
                return true;
            } else {
                if (target.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
                    ISorcererData targetCap = target.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();

                    if (targetCap.hasToggled(JJKAbilities.INFINITY.get())) {
                        if (ownerCap.isAdaptedTo(JJKAbilities.INFINITY.get())) {
                            return true;
                        }
                    } else if (targetCap.getTechnique() != null && ownerCap.isAdaptedTo(targetCap.getTechnique())) {
                        return true;
                    }
                }
            }
        }
        return target.getHealth() > owner.getHealth() * 4 || owner.getHealth() / owner.getMaxHealth() <= 0.1F;
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
    protected MahoragaEntity summon(int index, LivingEntity owner) {
        return new MahoragaEntity(owner, this.isTamed(owner));
    }


}
