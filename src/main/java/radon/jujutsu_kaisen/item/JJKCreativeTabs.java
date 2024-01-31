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
                        pOutput.accept(JJKItems.DRAGON_BONE.get());
                        pOutput.accept(JJKItems.CHAIN_OF_A_THOUSAND_MILES.get());
                        pOutput.accept(JJKItems.NYOI_STAFF.get());
                        pOutput.accept(JJKItems.SLAUGHTER_DEMON.get());
                        pOutput.accept(JJKItems.KAMUTOKE_DAGGER.get());
                        pOutput.accept(JJKItems.HITEN_STAFF.get());
                        pOutput.accept(JJKItems.POLEARM_STAFF.get());
                        pOutput.accept(JJKItems.STEEL_GAUNTLET.get());

                        pOutput.accept(JJKItems.GREEN_HANDLE_KATANA.get());
                        pOutput.accept(JJKItems.RED_HANDLE_KATANA.get());
                        pOutput.accept(JJKItems.JET_BLACK_SHADOW_SWORD.get());

                        pOutput.accept(JJKItems.INVENTORY_CURSE.get());
                        pOutput.accept(JJKItems.BLINDFOLD.get());

                        pOutput.accept(JJKItems.TOJI_FUSHIGURO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.SATORU_GOJO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.YUTA_OKKOTSU_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.MEGUMI_FUSHIGURO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.YUJI_IDATORI_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.TOGE_INUMAKI_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.SUGURU_GETO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.NAOYA_ZENIN_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.HAJIME_KASHIMO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.MAKI_ZENIN_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.AOI_TODO_SPAWN_EGG.get());

                        pOutput.accept(JJKItems.JOGO_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.JOGOAT_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.DAGON_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.HANAMI_SPAWN_EGG.get());

                        pOutput.accept(JJKItems.RUGBY_FIELD_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.FISH_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.CYCLOPS_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.KUCHISAKE_ONNA_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.ZOMBA_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.WORM_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.FELINE_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.FUGLY_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.BIRD_CURSE_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.FINGER_BEARER_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.RAINBOW_DRAGON_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.DINO_CURSE_SPAWN_EGG.get());

                        pOutput.accept(JJKItems.DISPLAY_CASE.get());
                        pOutput.accept(JJKItems.ALTAR.get());
                        pOutput.accept(JJKItems.VEIL_ROD.get());

                        pOutput.accept(JJKItems.SUKUNA_FINGER.get());
                        pOutput.accept(JJKItems.CURSED_TOTEM.get());
                        pOutput.accept(JJKItems.CURSED_COMPASS.get());
                        pOutput.accept(JJKItems.CURSED_MUSIC_DISC.get());
                        pOutput.accept(JJKItems.CURSED_EYE_OF_ENDER.get());

                        pOutput.accept(JJKItems.SORCERER_FLESH.get());
                        pOutput.accept(JJKItems.CURSE_FLESH.get());
                        pOutput.accept(JJKItems.MERGED_FLESH.get());
                    }))
                    .build());
}
