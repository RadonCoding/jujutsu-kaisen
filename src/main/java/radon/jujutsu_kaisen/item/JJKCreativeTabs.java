package radon.jujutsu_kaisen.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JujutsuKaisen.MOD_ID);
    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main", () ->
            CreativeModeTab.builder().icon(() -> new ItemStack(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get()))
                    .title(Component.translatable("creative_mode_tab.jujutsu_kaisen.main"))
                    .displayItems(((pParameters, pOutput) -> {
                        pOutput.accept(JJKItems.INVERTED_SPEAR_OF_HEAVEN.get());
                        pOutput.accept(JJKItems.PLAYFUL_CLOUD.get());
                        pOutput.accept(JJKItems.SPLIT_SOUL_KATANA.get());
                        pOutput.accept(JJKItems.CHAIN_OF_A_THOUSAND_MILES.get());
                        pOutput.accept(JJKItems.NYOI_STAFF.get());
                        pOutput.accept(JJKItems.SLAUGHTER_DEMON.get());
                        pOutput.accept(JJKItems.KAMUTOKE_DAGGER.get());
                        pOutput.accept(JJKItems.HITEN_STAFF.get());
                        pOutput.accept(JJKItems.POLEARM_STAFF.get());

                        pOutput.accept(JJKItems.JET_BLACK_SHADOW_SWORD.get());
                        pOutput.accept(JJKItems.YUTA_OKKOTSU_SWORD.get());
                        pOutput.accept(JJKItems.INVENTORY_CURSE.get());

                        pOutput.accept(JJKItems.SATORU_BLINDFOLD.get());
                        pOutput.accept(JJKItems.SATORU_CHESTPLATE.get());
                        pOutput.accept(JJKItems.SATORU_LEGGINGS.get());
                        pOutput.accept(JJKItems.SATORU_BOOTS.get());

                        pOutput.accept(JJKItems.YUJI_CHESTPLATE.get());
                        pOutput.accept(JJKItems.YUJI_LEGGINGS.get());
                        pOutput.accept(JJKItems.YUJI_BOOTS.get());

                        pOutput.accept(JJKItems.MEGUMI_CHESTPLATE.get());
                        pOutput.accept(JJKItems.MEGUMI_LEGGINGS.get());
                        pOutput.accept(JJKItems.MEGUMI_BOOTS.get());

                        pOutput.accept(JJKItems.TOGE_HELMET.get());
                        pOutput.accept(JJKItems.TOGE_CHESTPLATE.get());
                        pOutput.accept(JJKItems.TOGE_LEGGINGS.get());
                        pOutput.accept(JJKItems.TOGE_BOOTS.get());

                        pOutput.accept(JJKItems.YUTA_CHESTPLATE.get());
                        pOutput.accept(JJKItems.YUTA_LEGGINGS.get());
                        pOutput.accept(JJKItems.YUTA_BOOTS.get());

                        pOutput.accept(JJKItems.SUGURU_CHESTPLATE.get());
                        pOutput.accept(JJKItems.SUGURU_LEGGINGS.get());
                        pOutput.accept(JJKItems.SUGURU_BOOTS.get());

                        pOutput.accept(JJKItems.TOJI_FUSHIGURO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.SATORU_GOJO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.SUKUNA_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.YUTA_OKKOTSU_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.MEGUMI_FUSHIGURO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.MEGUNA_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.YUJI_IDATORI_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.TOGE_INUMAKI_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.SUGURU_GETO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.HEIAN_SUKUNA_SPAWN_EGG.get());

                        pOutput.accept(JJKItems.JOGO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.DAGON_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.HANAMI_SPAWN_EGG.get());

                        pOutput.accept(JJKItems.RUGBY_FIELD_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.FISH_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.CYCLOPS_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.KUCHISAKE_ONNA_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.ZOMBA_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.WORM_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.FELINE_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.RAINBOW_DRAGON_SPAWN_EGG.get());

                        pOutput.accept(JJKItems.DISPLAY_CASE.get());
                        pOutput.accept(JJKItems.ALTAR.get());
                        pOutput.accept(JJKItems.VEIL_ROD.get());

                        pOutput.accept(JJKItems.SUKUNA_FINGER.get());
                        pOutput.accept(JJKItems.CURSED_TOTEM.get());
                        pOutput.accept(JJKItems.CURSED_COMPASS.get());
                        pOutput.accept(JJKItems.CURSED_MUSIC_DISC.get());
                        pOutput.accept(JJKItems.CURSED_EYE_OF_ENDER.get());
                    }))
                    .build());
}
