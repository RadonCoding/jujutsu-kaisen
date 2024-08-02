package radon.jujutsu_kaisen.entity;


import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IDomain extends IBarrier {
    @Nullable Level getVirtual();

    float getScale();

    default float getStrength() {
        return IBarrier.super.getStrength();
    }
}
