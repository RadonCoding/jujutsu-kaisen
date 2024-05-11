package radon.jujutsu_kaisen.effect.registry;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.effect.CursedBudEffect;
import radon.jujutsu_kaisen.effect.JJKEffect;

public class JJKEffects {
    public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<MobEffect, MobEffect> STUN = EFFECTS.register("stun", () -> new JJKEffect(MobEffectCategory.NEUTRAL, 0xFFFFFF));
    public static DeferredHolder<MobEffect, MobEffect> UNLIMITED_VOID = EFFECTS.register("unlimited_void", () -> new JJKEffect(MobEffectCategory.HARMFUL, 0x000000));
    public static DeferredHolder<MobEffect, MobEffect> CURSED_BUD = EFFECTS.register("cursed_bud", () -> new CursedBudEffect(MobEffectCategory.HARMFUL, 0x00FF00));
    public static DeferredHolder<MobEffect, MobEffect> INVISIBILITY = EFFECTS.register("invisibility", () -> new JJKEffect(MobEffectCategory.BENEFICIAL, 0x00FF00));
    public static DeferredHolder<MobEffect, MobEffect> TRANSFIGURED_SOUL = EFFECTS.register("transfigured_soul", () -> new JJKEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
}
