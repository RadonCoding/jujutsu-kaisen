package radon.jujutsu_kaisen.ability.ten_shadows.summon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.data.JJKAttachmentTypes;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.ten_shadows.NueTotalityEntity;
import radon.jujutsu_kaisen.util.HelperMethods;

import java.util.List;

public class NueTotality extends Summon<NueTotalityEntity> {
    public NueTotality() {
        super(NueTotalityEntity.class);
    }

    @Override
    public boolean isScalable(LivingEntity owner) {
        return false;
    }

    @Override
    public boolean shouldTrigger(PathfinderMob owner, @Nullable LivingEntity target) {
        ISorcererData data = owner.getData(JJKAttachmentTypes.SORCERER);

        if (data == null) return false;

        if (data.hasToggled(this)) {
            return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(20) != 0;
        }
        return target != null && !target.isDeadOrDying() && HelperMethods.RANDOM.nextInt(10 ) == 0;
    }

    @Override
    public boolean isTotality() {
        return true;
    }

    @Override
    public List<EntityType<?>> getFusions() {
        return List.of(JJKEntities.NUE.get(), JJKEntities.GREAT_SERPENT.get());
    }

    @Override
    public float getCost(LivingEntity owner) {
        return 0.2F;
    }

    @Override
    public List<EntityType<?>> getTypes() {
        return List.of(JJKEntities.NUE_TOTALITY.get());
    }

    @Override
    protected NueTotalityEntity summon(LivingEntity owner) {
        return new NueTotalityEntity(owner);
    }

    @Override
    public boolean isTenShadows() {
        return true;
    }

    @Override
    public int getCooldown() {
        return 25 * 20;
    }


}
