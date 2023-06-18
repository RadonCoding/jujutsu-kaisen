package radon.jujutsu_kaisen.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.util.HelperMethods;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class UnlimitedVoidOverlay {
    private static final String[] SYMBOLS = {"⍑", "ʖ", "ᓵ", "╎", "ᒷ", "⍊", "⍋", "ᒲ", "リ", "ᔑ", "ꖎ", "ᒣ", "ᓭ", "ᘉ", "⨅", "╎⨅", "ᓵ⍑", "⍙", "ᔑ⨅", "ꖌ", "⍜", "⍀", "∷", "⨇", "ᒲ⍑", "ꖇ", "㇣", "˥", "˩", "˧˥˧", "ʢ", "ʖ̇ ", "˩˥ ", "ʖ̬ ", "ʖ̥"};

    private static int time;

    public static void trigger(int duration) {
        time = duration;
    }

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
        if (event.phase != TickEvent.Phase.START) return;

        if (time-- > 0) {
            Minecraft mc = Minecraft.getInstance();
            mc.gui.setOverlayMessage(Component.literal(generateRandomSGAText(HelperMethods.RANDOM.nextInt(20, 100))), false);
        }
    }
}
