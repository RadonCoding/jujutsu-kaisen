package radon.jujutsu_kaisen.client.particle;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;


public class ParticleColors {
    public static Vector3f DARK_BLUE = Vec3.fromRGB24(2511359).toVector3f();
    public static Vector3f LIGHT_BLUE = Vec3.fromRGB24(6205439).toVector3f();
    public static Vector3f DARK_RED = Vec3.fromRGB24(16721446).toVector3f();
    public static Vector3f LIGHT_RED = Vec3.fromRGB24(16735838).toVector3f();
    public static Vector3f DARK_PURPLE = Vec3.fromRGB24(7283199).toVector3f();
    public static Vector3f LIGHT_PURPLE = Vec3.fromRGB24(10379007).toVector3f();
    public static Vector3f RCT = Vec3.fromRGB24(16776670).toVector3f();
    public static Vector3f SIMPLE_DOMAIN = Vec3.fromRGB24(9756159).toVector3f();
    public static Vector3f FALLING_BLOSSOM_EMOTION = Vec3.fromRGB24(8454141).toVector3f();
    public static Vector3f CURSED_ENERGY_SORCERER = Vec3.fromRGB24(6527999).toVector3f();
    public static Vector3f CURSED_ENERGY_CURSE = Vec3.fromRGB24(10890961).toVector3f();
    public static Vector3f PURPLE_LIGHTNING = Vec3.fromRGB24(9267447).toVector3f();
    public static Vector3f BLACK_FLASH = Vec3.fromRGB24(16188677).toVector3f();
    public static Vector3f YELLOW_FIRE = Vec3.fromRGB24(16745472).toVector3f();
    public static Vector3f RED_FIRE = Vec3.fromRGB24(16729088).toVector3f();
    public static Vector3f SMOKE = Vec3.fromRGB24(2368548).toVector3f();

    public static Vector3f getCursedEnergyColor(LivingEntity entity) {
        ISorcererData cap = entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
        return getCursedEnergyColor(cap.getType());
    }

    public static Vector3f getCursedEnergyColorBright(LivingEntity entity) {
        Vector3f color = new Vector3f(getCursedEnergyColor(entity));
        Vector3f white = new Vector3f(1.0F, 1.0F, 1.0F);
        return color.lerp(white, 0.5F);
    }

    public static Vector3f getCursedEnergyColor(JujutsuType type) {
        return type == JujutsuType.CURSE ? CURSED_ENERGY_CURSE : CURSED_ENERGY_SORCERER;
    }
}
