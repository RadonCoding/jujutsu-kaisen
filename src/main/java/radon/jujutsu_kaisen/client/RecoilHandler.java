package radon.jujutsu_kaisen.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID,  bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RecoilHandler {
    private static float recoil;
    private static float progress;

    public static void fire() {
        recoil = 10.0F;
        progress = 0.0F;
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase != TickEvent.Phase.END || recoil <= 0.0F) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        assert mc.player != null;

        float recoilAmount = recoil * mc.getDeltaFrameTime() * 0.15F;
        float startProgress = progress / recoil;
        float endProgress = (progress + recoilAmount) / recoil;

        float pitch = mc.player.getXRot();

        if (startProgress < 0.2F) {
            mc.player.setXRot(pitch - ((endProgress - startProgress) / 0.2F) * recoil);
        }
        else {
            mc.player.setXRot(pitch + ((endProgress - startProgress) / 0.8F) * recoil);
        }

        progress += recoilAmount;

        if (progress >= recoil) {
            recoil = 0;
            progress = 0;
        }
    }
}
