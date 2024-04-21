package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.HelperMethods;
import radon.jujutsu_kaisen.util.RotationUtil;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class UnlimitedVoidHandler {
    private static final String[] SYMBOLS = {"⍑", "ʖ", "ᓵ", "╎", "ᒷ", "⍊", "⍋", "ᒲ", "リ", "ᔑ", "ꖎ", "ᒣ", "ᓭ", "ᘉ", "⨅", "╎⨅", "ᓵ⍑", "⍙", "ᔑ⨅", "ꖌ", "⍜", "⍀", "∷", "⨇", "ᒲ⍑", "ꖇ", "㇣", "˥", "˩", "˧˥˧", "ʢ", "ʖ̇ ", "˩˥ ", "ʖ̬ ", "ʖ̥"};

    private static String generateRandomSGAText(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = HelperMethods.RANDOM.nextInt(SYMBOLS.length);
            sb.append(SYMBOLS[index]);
        }
        return sb.toString();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (!mc.player.hasEffect(JJKEffects.UNLIMITED_VOID.get())) return;

        mc.gui.setOverlayMessage(Component.literal(generateRandomSGAText(HelperMethods.RANDOM.nextInt(20, 100))), false);
    }
}
