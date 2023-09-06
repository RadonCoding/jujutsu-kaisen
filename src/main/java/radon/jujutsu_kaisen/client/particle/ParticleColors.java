package radon.jujutsu_kaisen.client.particle;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;



public class ParticleColors {
    public static Vector3f DARK_BLUE_COLOR = Vec3.fromRGB24(255).toVector3f();
    public static Vector3f LIGHT_BLUE_COLOR = Vec3.fromRGB24(38143).toVector3f();
    public static Vector3f RED_COLOR = Vec3.fromRGB24(16711680).toVector3f();
    public static Vector3f RCT_COLOR = Vec3.fromRGB24(16776670).toVector3f();
    public static Vector3f SIMPLE_DOMAIN = Vec3.fromRGB24(9756159).toVector3f();
    public static Vector3f CURSED_ENERGY_SORCERER_COLOR = Vec3.fromRGB24(4826595).toVector3f();
    public static Vector3f CURSED_ENERGY_CURSE_COLOR = Vec3.fromRGB24(10890961).toVector3f();
    public static Vector3f PINK_COLOR = Vec3.fromRGB24(15729660).toVector3f();
    public static Vector3f PURPLE_COLOR = Vec3.fromRGB24(6176255).toVector3f();
    public static Vector3f BLACK_COLOR = Vec3.fromRGB24(0).toVector3f();
    public static Vector3f VIOLET_LIGHTNING_COLOR = Vec3.fromRGB24(6175431).toVector3f();
    public static Vector3f WHITE_LIGHTNING_COLOR = Vec3.fromRGB24(14939135).toVector3f();

    public static Vector3f getCursedEnergyColor(LivingEntity entity) {
        ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return getCursedEnergyColor(cap.getType());
    }

    public static Vector3f getCursedEnergyColor(JujutsuType type) {
        return type == JujutsuType.CURSE ? CURSED_ENERGY_CURSE_COLOR : CURSED_ENERGY_SORCERER_COLOR;
    }
}
