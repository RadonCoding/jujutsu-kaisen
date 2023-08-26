package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.io.IOException;

public class JJKShaders {
    private static ShaderInstance translucentParticleShader;

    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(JujutsuKaisen.MOD_ID, "translucent_particle"), DefaultVertexFormat.PARTICLE),
                shader -> translucentParticleShader = shader);
    }

    public static ShaderInstance getTranslucentParticleShader() {
        return translucentParticleShader;
    }
}
