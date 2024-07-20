package radon.jujutsu_kaisen.client.gui.screen.radial;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.ability.curse_manipulation.util.CurseManipulationUtil;
import radon.jujutsu_kaisen.client.util.RenderUtil;
import radon.jujutsu_kaisen.data.curse_manipulation.AbsorbedCurse;

import java.util.ArrayList;
import java.util.List;

public class CurseDisplayItem extends DisplayItem {
    private final AbsorbedCurse curse;

    public CurseDisplayItem(Minecraft minecraft, RadialScreen screen, Runnable select, AbsorbedCurse curse) {
        super(minecraft, screen, select);

        this.curse = curse;
    }

    public AbsorbedCurse getCurse() {
        return this.curse;
    }

    @Override
    public void drawHover(GuiGraphics graphics, int x, int y) {
        List<Component> lines = new ArrayList<>();

        Component costText = Component.translatable(String.format("gui.%s.ability_overlay.cost", JujutsuKaisen.MOD_ID),
                CurseManipulationUtil.getCurseCost(this.curse));
        lines.add(costText);

        Component experienceText = Component.translatable(String.format("gui.%s.ability_overlay.experience", JujutsuKaisen.MOD_ID),
                CurseManipulationUtil.getCurseExperience(this.curse));
        lines.add(experienceText);

        for (Component line : lines) {
            graphics.drawCenteredString(this.minecraft.font, line, x, y - ((lines.size() - 1) * this.minecraft.font.lineHeight), 0xFFFFFF);
            y += this.minecraft.font.lineHeight;
        }
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        if (!(CurseManipulationUtil.createCurse(this.minecraft.player, this.curse) instanceof LivingEntity instance))
            return;

        int scale = Math.round(Math.max(3.0F, 10.0F - instance.getBbHeight()));
        RenderUtil.renderEntityInInventoryFollowsAngle(graphics, x, y,
                scale, 0.0F, -1.0F, -0.5F, instance);
    }

    @Override
    public void mouseClicked(int button) {

    }

    @Override
    public boolean isActive() {
        return false;
    }
}