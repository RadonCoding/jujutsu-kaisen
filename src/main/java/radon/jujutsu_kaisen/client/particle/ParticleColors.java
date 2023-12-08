package radon.jujutsu_kaisen.client.particle;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;


public class ParticleColors {
    public static Vector3f DARK_BLUE_COLOR = Vec3.fromRGB24(2511359).toVector3f();
    public static Vector3f LIGHT_BLUE_COLOR = Vec3.fromRGB24(6205439).toVector3f();
    public static Vector3f DARK_RED_COLOR = Vec3.fromRGB24(16721446).toVector3f();
    public static Vector3f LIGHT_RED_COLOR = Vec3.fromRGB24(16735838).toVector3f();
    public static Vector3f DARK_PURPLE_COLOR = Vec3.fromRGB24(7283199).toVector3f();
    public static Vector3f LIGHT_PURPLE_COLOR = Vec3.fromRGB24(10379007).toVector3f();
    public static Vector3f RCT_COLOR = Vec3.fromRGB24(16776670).toVector3f();
    public static Vector3f SIMPLE_DOMAIN = Vec3.fromRGB24(9756159).toVector3f();
    public static Vector3f CURSED_ENERGY_SORCERER_COLOR = Vec3.fromRGB24(5108735).toVector3f();
    public static Vector3f CURSED_ENERGY_CURSE_COLOR = Vec3.fromRGB24(10890961).toVector3f();
    public static Vector3f PURPLE_LIGHTNING_COLOR = Vec3.fromRGB24(9267447).toVector3f();
    public static Vector3f BLACK_FLASH = Vec3.fromRGB24(16188677).toVector3f();
    public static Vector3f YELLOW_FIRE_COLOR = Vec3.fromRGB24(16745472).toVector3f();
    public static Vector3f RED_FIRE_COLOR = Vec3.fromRGB24(16729088).toVector3f();
    public static Vector3f SMOKE_COLOR = Vec3.fromRGB24(2368548).toVector3f();

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
        return type == JujutsuType.CURSE ? CURSED_ENERGY_CURSE_COLOR : CURSED_ENERGY_SORCERER_COLOR;
    }
}
