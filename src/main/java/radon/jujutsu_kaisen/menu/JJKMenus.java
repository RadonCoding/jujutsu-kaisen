package radon.jujutsu_kaisen.menu;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKMenus {
    public static DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, JujutsuKaisen.MOD_ID);

    public static DeferredHolder<MenuType<?>, MenuType<AltarMenu>> ALTAR = MENUS.register("altar", () ->
            new MenuType<>(AltarMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static DeferredHolder<MenuType<?>, MenuType<VeilRodMenu>> VEIL_ROD = MENUS.register("veil_rod", () ->
            new MenuType<>((pContainerId, pPlayerInventory) -> new VeilRodMenu(pContainerId), FeatureFlags.DEFAULT_FLAGS));

    public static DeferredHolder<MenuType<?>, MenuType<BountyMenu>> BOUNTY = MENUS.register("bounty", () ->
            new MenuType<>(BountyMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
