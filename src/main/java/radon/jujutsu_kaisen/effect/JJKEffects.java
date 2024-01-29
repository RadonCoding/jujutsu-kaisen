package radon.jujutsu_kaisen.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.effect.base.JJKEffect;

public class JJKEffects {
    public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<MobEffect> STUN = EFFECTS.register("stun", () -> new JJKEffect(MobEffectCategory.NEUTRAL, 0xFFFFFF));
    public static RegistryObject<MobEffect> UNLIMITED_VOID = EFFECTS.register("unlimited_void", () -> new JJKEffect(MobEffectCategory.HARMFUL, 0x000000));
    public static RegistryObject<MobEffect> CURSED_BUD = EFFECTS.register("cursed_bud", () -> new CursedBudEffect(MobEffectCategory.HARMFUL, 0x00FF00));
    public static RegistryObject<MobEffect> INVISIBILITY = EFFECTS.register("invisibility", () -> new JJKEffect(MobEffectCategory.BENEFICIAL, 0x00FF00));
    public static RegistryObject<MobEffect> TRANSFIGURED_SOUL = EFFECTS.register("transfigured_soul", () -> new JJKEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
}
