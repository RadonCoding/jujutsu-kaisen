package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mahoraga extends Summon<MahoragaEntity> {
    public Mahoraga() {
        super(MahoragaEntity.class);
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        AtomicBoolean result = new AtomicBoolean(owner.getHealth() / owner.getMaxHealth() <= 0.1F);

        if (this.isTamed(owner)) {
            if (JJKAbilities.hasToggled(owner, this)) {
                result.set(target != null);
            } else if (target != null) {
                owner.getCapability(SorcererDataHandler.INSTANCE).ifPresent(ownerCap -> {
                    target.getCapability(SorcererDataHandler.INSTANCE).ifPresent(targetCap -> {
                        result.set(targetCap.getTechnique() != null && ownerCap.isAdaptedTo(targetCap.getTechnique()));
                    });
                });
            }
        }
        return result.get();
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

    @Override
    public boolean isTechnique() {
        return true;
    }
}
