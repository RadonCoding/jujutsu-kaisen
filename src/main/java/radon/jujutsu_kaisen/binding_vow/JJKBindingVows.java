package radon.jujutsu_kaisen.binding_vow;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.base.IAttack;
import radon.jujutsu_kaisen.ability.base.IChanneled;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ICharged;
import radon.jujutsu_kaisen.ability.base.IDomainAttack;
import radon.jujutsu_kaisen.ability.base.IDurationable;
import radon.jujutsu_kaisen.ability.base.ITenShadowsAttack;
import radon.jujutsu_kaisen.ability.base.IToggled;
import radon.jujutsu_kaisen.ability.shockwave.Shockwave;

public class JJKBindingVows {
    public static ResourceKey<Registry<BindingVow>> BINDING_VOW_KEY = ResourceKey.createRegistryKey(new ResourceLocation(JujutsuKaisen.MOD_ID, "binding_vow"));
    public static Registry<BindingVow> BINDING_VOW_REGISTRY = new RegistryBuilder<>(BINDING_VOW_KEY).create();
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
