package radon.jujutsu_kaisen.client.particle;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;

import java.util.concurrent.atomic.AtomicReference;

public class ParticleColors {
    public static Vector3f DARK_BLUE_COLOR = Vec3.fromRGB24(255).toVector3f();
    public static Vector3f LIGHT_BLUE_COLOR = Vec3.fromRGB24(38143).toVector3f();
    public static Vector3f RED_COLOR = Vec3.fromRGB24(16711680).toVector3f();
    public static Vector3f PURPLE_COLOR = Vec3.fromRGB24(12781547).toVector3f();
    public static Vector3f CURSED_ENERGY_SORCERER_COLOR = Vec3.fromRGB24(4842464).toVector3f();
    public static Vector3f CURSED_ENERGY_CURSE_COLOR = Vec3.fromRGB24(10890961).toVector3f();

    public static Vector3f getCursedEnergyColor(LivingEntity entity) {
        AtomicReference<Vector3f> color = new AtomicReference<>();
        entity.getCapability(SorcererDataHandler.INSTANCE).ifPresent(cap ->
            color.set(getCursedEnergyColor(cap.isCurse())));
        return color.get();
    }

    public static Vector3f getCursedEnergyColor(boolean curse) {
        return curse ? CURSED_ENERGY_CURSE_COLOR : CURSED_ENERGY_SORCERER_COLOR;
    }
}
