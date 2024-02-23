package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.io.IOException;

public class JJKShaders {
    private static ShaderInstance skyShader;

    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(JujutsuKaisen.MOD_ID, "sky"), DefaultVertexFormat.POSITION),
                shader -> skyShader = shader);
    }

    public static ShaderInstance getSkyShader() {
        return skyShader;
    }
}
