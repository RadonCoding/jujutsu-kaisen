package radon.jujutsu_kaisen.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import radon.jujutsu_kaisen.ability.DomainExpansion;

public interface IDomain extends IBarrier {
    @Nullable Entity getCenter();

    @Nullable Level getInside();

    float getScale();

    default float getStrength() {
        return IBarrier.super.getStrength();
    }
}
