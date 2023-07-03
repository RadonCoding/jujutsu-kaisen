package radon.jujutsu_kaisen.damage;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

public class JujutsuDamageSource extends EntityDamageSource {

    public JujutsuDamageSource(String pDamageTypeId, Entity pEntity) {
        super(pDamageTypeId, pEntity);
    }
}
