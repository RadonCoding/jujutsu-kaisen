package radon.jujutsu_kaisen.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKEffects {
    public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JujutsuKaisen.MOD_ID);

    public static RegistryObject<MobEffect> STUN = EFFECTS.register("stun", () -> new StunEffect(MobEffectCategory.HARMFUL, 0));
    public static RegistryObject<MobEffect> UNLIMITED_VOID = EFFECTS.register("unlimited_void", () -> new StunEffect(MobEffectCategory.HARMFUL, 0));
}
