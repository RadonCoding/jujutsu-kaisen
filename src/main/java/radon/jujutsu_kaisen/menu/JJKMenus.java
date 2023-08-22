package radon.jujutsu_kaisen.menu;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKMenus {
    public static DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, JujutsuKaisen.MOD_ID);

    public static RegistryObject<MenuType<AltarMenu>> ALTAR = MENUS.register("altar", () ->
            new MenuType<>(AltarMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static RegistryObject<MenuType<VeilRodMenu>> VEIL_ROD = MENUS.register("veil_rod", () ->
            new MenuType<>((pContainerId, pPlayerInventory) -> new VeilRodMenu(pContainerId), FeatureFlags.DEFAULT_FLAGS));

    public static RegistryObject<MenuType<BountyMenu>> BOUNTY = MENUS.register("bounty", () ->
            new MenuType<>(BountyMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
