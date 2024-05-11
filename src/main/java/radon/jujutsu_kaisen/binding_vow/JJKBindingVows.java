package radon.jujutsu_kaisen.binding_vow;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.IAttack;
import radon.jujutsu_kaisen.ability.IChanneled;
import radon.jujutsu_kaisen.ability.Ability;
import radon.jujutsu_kaisen.ability.ICharged;
import radon.jujutsu_kaisen.ability.IDomainAttack;
import radon.jujutsu_kaisen.ability.IDurationable;
import radon.jujutsu_kaisen.ability.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.IToggled;

public class JJKBindingVows {
    public static ResourceKey<Registry<BindingVow>> BINDING_VOW_KEY = ResourceKey.createRegistryKey(new ResourceLocation(JujutsuKaisen.MOD_ID, "binding_vow"));
    public static Registry<BindingVow> BINDING_VOW_REGISTRY = new RegistryBuilder<>(BINDING_VOW_KEY).sync(true).create();
    public static DeferredRegister<BindingVow> BINDING_VOWS = DeferredRegister.create(BINDING_VOW_REGISTRY, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<BindingVow, BindingVow> OVERTIME = BINDING_VOWS.register("overtime", Overtime::new);
    public static DeferredHolder<BindingVow, BindingVow> RECOIL = BINDING_VOWS.register("recoil", Recoil::new);

    public static ResourceLocation getKey(BindingVow ability) {
        return BINDING_VOW_REGISTRY.getKey(ability);
    }

    public static BindingVow getValue(ResourceLocation key) {
        return BINDING_VOW_REGISTRY.get(key);
    }
}
