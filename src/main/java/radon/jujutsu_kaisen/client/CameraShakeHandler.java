package radon.jujutsu_kaisen.client;

import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import radon.jujutsu_kaisen.JujutsuKaisen;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CameraShakeHandler {
    @Nullable
    private static ShakeEvent current;

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (current == null) return;

        float time = (float) (event.getCamera().getEntity().tickCount + event.getPartialTick());
        float shakeX = Mth.cos(time * current.speed) * current.intensity;
        float shakeY = Mth.sin(time * current.speed) * current.intensity;
        event.setPitch(event.getPitch() + shakeX);
        event.setYaw(event.getYaw() + shakeY);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.type != TickEvent.Type.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;

        if (current == null) return;

        current.duration--;

        if (current.duration <= 0) {
            current = null;
        }
    }

    public static void shakeCamera(float intensity, float speed, int duration) {
        current = new ShakeEvent(intensity, speed, duration);
    }

    public static class ShakeEvent {
        public float intensity;
        public float speed;
        public int duration;

        public ShakeEvent(float intensity, float speed, int duration) {
            this.intensity = intensity;
            this.speed = speed;
            this.duration = duration;
        }
    }
}