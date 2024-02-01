package radon.jujutsu_kaisen.ability.disaster_flames;

import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Imbuement;
import radon.jujutsu_kaisen.ability.boogie_woogie.SwapSelf;

public class DisasterFlamesImbuement extends Imbuement {
    @Override
    public void hit(LivingEntity owner, LivingEntity target) {
        JJKAbilities.DISASTER_FLAMES.get().run(owner, target);
    }

    @Override
    public int getCooldown() {
        return JJKAbilities.DISASTER_FLAMES_IMBUEMENT.get().getCooldown();
    }
}
