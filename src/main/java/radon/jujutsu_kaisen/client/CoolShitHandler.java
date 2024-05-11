package radon.jujutsu_kaisen.client;

import radon.jujutsu_kaisen.cursed_technique.CursedTechnique;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.mixin.client.IHumanoidModelAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = JujutsuKaisen.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CoolShitHandler {
    @Nullable
    private static Vec3 start;

    @Nullable
    private static Vec3 plane;

    @SubscribeEvent
    public static void onInteractionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (event.isUseItem()) {
            start = mc.player.getViewVector(mc.getPartialTick());
        } else if (event.isAttack()) {
            if (start != null && plane != null) {

            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (start == null) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            Vec3 end = mc.player.getViewVector(event.getPartialTick());
            Vec3 right = start.cross(mc.player.getViewVector(event.getPartialTick()));
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();

            GL11.glRotated(mc.player.getViewXRot(event.getPartialTick()), 1.0F, 0.0F, 0.0F);
            GL11.glRotated(mc.player.getViewYRot(event.getPartialTick()) + 180.0F, 0.0F, 1.0F, 0.0F);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();
            buffer.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION);
            buffer.vertex(start.x, start.y, start.z).endVertex();
            buffer.vertex(end.x, end.y, end.z).endVertex();
            tesselator.end();

            GL11.glPopMatrix();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            if (right.lengthSqr() > 0.001F) {
                plane = right.normalize();
            }
        }
    }
}
