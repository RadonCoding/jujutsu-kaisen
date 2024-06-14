package radon.jujutsu_kaisen.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.AppenderAdmin;
import org.lwjgl.opengl.GL11;

public class FakeEntityRenderer {
    public static boolean isFakeRender;
    public static boolean isCustomWalkAnimation;
    public static float walkAnimationPosition;
    public static float walkAnimationSpeed;

    private final Entity entity;

    private final int tickCount;

    private float yRot;
    private float xRot;

    private final boolean sneak;

    private float yHeadRot;
    public float yBodyRot;

    private float attackAnim;

    private float position = 0.0F;
    private float speed = 0.0F;

    private float alpha = 1.0F;
    
    public FakeEntityRenderer(Entity entity) {
        this.entity = entity;

        this.tickCount = entity.tickCount;

        this.yRot = entity.getYRot();
        this.xRot = entity.getXRot();

        this.sneak = entity.isShiftKeyDown();

        if (entity instanceof LivingEntity living) {
            this.yHeadRot = living.yHeadRot;
            this.yBodyRot = living.yBodyRot;

            this.attackAnim = living.attackAnim;

            this.position = living.walkAnimation.position();
            this.speed = living.walkAnimation.speed();
        }
    }

    public void setFullRotation(float yaw, float pitch) {
        this.yRot = yaw;
        this.xRot = pitch;
        this.yHeadRot = yaw;
        this.yBodyRot = yaw;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void render(Vec3 pos, float partialTicks) {
        PoseStack poseStack = new PoseStack();

        Minecraft mc = Minecraft.getInstance();

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(this.entity);

        Vec3 offset = renderer.getRenderOffset(this.entity, partialTicks);

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();

        poseStack.translate((pos.x - cam.x) + offset.x, (pos.y - cam.y) + offset.y, (pos.z - cam.z) + offset.z);

        this.render(poseStack, partialTicks);
    }
    
    public void render(PoseStack poseStack, float partialTicks) {
        boolean invisible = this.entity.isInvisible();

        this.entity.setInvisible(false);

        int tickCount = this.entity.tickCount;

        this.entity.tickCount = this.tickCount;

        float yRot = this.entity.getYRot();
        float yRotO = this.entity.yRotO;

        float xRot = this.entity.getXRot();
        float xRotO = this.entity.xRotO;

        this.entity.setYRot(this.yRot);
        this.entity.yRotO = this.yRot;

        this.entity.setXRot(this.xRot);
        this.entity.xRotO = this.xRot;

        boolean sneak = this.entity.isShiftKeyDown();

        this.entity.setShiftKeyDown(this.sneak);

        int hurtTime = 0;
        int deathTime = 0;

        float yHeadRot = 0.0F;
        float yHeadRotO = 0.0F;

        float yBodyRot = 0.0F;
        float yBodyRotO = 0.0F;

        float attackAnim = 0.0F;
        float oAttackAnim = 0.0F;

        if (this.entity instanceof LivingEntity living) {
            hurtTime = living.hurtTime;

            living.hurtTime = 0;

            deathTime = living.deathTime;

            living.deathTime = 0;

            yHeadRot = living.yHeadRot;
            yHeadRotO = living.yHeadRotO;

            living.yHeadRot = this.yHeadRot;
            living.yHeadRotO = this.yHeadRot;

            yBodyRot = living.yBodyRot;
            yBodyRotO = living.yBodyRotO;

            living.yBodyRot = this.yBodyRot;
            living.yBodyRotO = this.yBodyRot;

            attackAnim = living.attackAnim;
            oAttackAnim = living.oAttackAnim;

            living.attackAnim = this.attackAnim;
            living.oAttackAnim = this.attackAnim;

            isCustomWalkAnimation = true;
            walkAnimationPosition = this.position;
            walkAnimationSpeed = this.speed;
        }

        Minecraft mc = Minecraft.getInstance();

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        EntityRenderer<? super Entity> renderer = dispatcher.getRenderer(this.entity);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        isFakeRender = true;

        renderer.render(this.entity, this.entity.getYRot(), partialTicks, poseStack, buffer,
                dispatcher.getPackedLightCoords(this.entity, partialTicks));

        isFakeRender = false;

        // For some reason this makes transparency work
        if (this.alpha < 1.0F) buffer.getBuffer(RenderType.translucent());

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (this.entity instanceof LivingEntity living) {
            isCustomWalkAnimation = false;

            living.oAttackAnim = oAttackAnim;
            living.attackAnim = attackAnim;

            living.yBodyRotO = yBodyRotO;
            living.yBodyRot = yBodyRot;

            living.yHeadRotO = yHeadRotO;
            living.yHeadRot = yHeadRot;

            living.hurtTime = hurtTime;

            living.deathTime = deathTime;
        }

        this.entity.setShiftKeyDown(sneak);

        this.entity.xRotO = xRotO;
        this.entity.setXRot(xRot);

        this.entity.yRotO = yRotO;
        this.entity.setYRot(yRot);

        this.entity.tickCount = tickCount;

        this.entity.setInvisible(invisible);

        buffer.endLastBatch();
    }
}
