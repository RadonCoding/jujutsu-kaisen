package radon.jujutsu_kaisen.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class JJKCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, JujutsuKaisen.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main", () ->
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
                        pOutput.accept(JJKItems.MIWA_KASUMI_SPAWN_EGG.get());
                        pOutput.accept(JJKItems.SORCERER_VILLAGER.get());

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
                        pOutput.accept(JJKItems.KO_GUY_SPAWN_EGG.get());

                        pOutput.accept(JJKItems.ALTAR.get());
                        pOutput.accept(JJKItems.VEIL_ROD.get());

                        pOutput.accept(JJKItems.SUKUNA_FINGER.get());
                    }))
                    .build());
}
