package radon.jujutsu_kaisen.damage;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class IndirectJujutsuDamageSource extends IndirectEntityDamageSource {
    public IndirectJujutsuDamageSource(String pDamageTypeId, Entity pSource, @Nullable Entity pIndirectEntity) {
        super(pDamageTypeId, pSource, pIndirectEntity);
    }
}
