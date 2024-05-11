package radon.jujutsu_kaisen.client.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderUtil {
    public static void renderEntityInInventoryFollowsAngle(
            GuiGraphics graphics,
            int x,
            int y,
            int scale,
            float yOffset,
            float angleXComponent,
            float angleYComponent,
            LivingEntity entity
    ) {
        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf cam = new Quaternionf().rotateX(angleYComponent * 20.0F * (float) (Math.PI / 180.0));
        pose.mul(cam);
        float f4 = entity.yBodyRot;
        float f5 = entity.getYRot();
        float f6 = entity.getXRot();
        float f7 = entity.yHeadRotO;
        float f8 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
        entity.setYRot(180.0F + angleXComponent * 40.0F);
        entity.setXRot(-angleYComponent * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        float f9 = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + yOffset * f9, 0.0F);
        float f10 = (float)scale / f9;
        InventoryScreen.renderEntityInInventory(graphics, x, y, f10, vector3f, pose, cam, entity);
        entity.yBodyRot = f4;
        entity.setYRot(f5);
        entity.setXRot(f6);
        entity.yHeadRotO = f7;
        entity.yHeadRot = f8;
        graphics.disableScissor();
    }
}
