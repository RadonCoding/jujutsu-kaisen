package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import radon.jujutsu_kaisen.JujutsuKaisen;

import java.util.List;

public class MissionCard {
    private static final ResourceLocation MISSION_CARD = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/mission_card.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
    
    public static final int WINDOW_WIDTH = 104;
    public static final int WINDOW_HEIGHT = 128;
    public static final int WINDOW_INSIDE_X = 9;
    public static final int WINDOW_INSIDE_Y = 18;
    public static final int OUTER_PADDING = 10;
    public static final int INNER_PADDING = 6;
    public static final int WINDOW_INSIDE_WIDTH = 86;
    public static final int WINDOW_INSIDE_HEIGHT = 101;
    public static final int TITLE_X = 8;
    public static final int TITLE_Y = 6;
    
    private static final int BACKGROUND_TILE_WIDTH = 16;
    private static final int BACKGROUND_TILE_HEIGHT = 16;
    
    private final Minecraft minecraft;
    private final MissionsScreen.Mission mission;
    private final List<FormattedCharSequence> description;

    private double scroll;

    public MissionCard(Minecraft minecraft, MissionsScreen.Mission mission) {
        this.minecraft = minecraft;
        this.mission = mission;
        this.description = this.minecraft.font.split(this.mission.description(), WINDOW_INSIDE_WIDTH - (INNER_PADDING * 2));
    }

    public void scroll(double dragY) {
        int height = this.description.size() * (this.minecraft.font.lineHeight + 2) - 2;
        int displayed = WINDOW_INSIDE_HEIGHT - (INNER_PADDING * 2);

        if (height - displayed > 0) {
            this.scroll = Mth.clamp(this.scroll + dragY, -(height - displayed), 0.0D);
        }
    }

    public void drawTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        int width = WINDOW_WIDTH - TITLE_X * 2;

        if (this.minecraft.font.width(this.mission.title()) <= width) return;

        graphics.renderTooltip(this.minecraft.font, this.mission.title(), mouseX, mouseY);
    }

    public void drawInside(GuiGraphics graphics, int offsetX, int offsetY) {
        graphics.enableScissor(offsetX, offsetY, offsetX + WINDOW_INSIDE_WIDTH, offsetY + WINDOW_INSIDE_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate((float) offsetX, (float) offsetY, 0.0F);

        for (int i1 = -1; i1 <= WINDOW_INSIDE_WIDTH / BACKGROUND_TILE_WIDTH; ++i1) {
            for (int j1 = -1; j1 <= WINDOW_INSIDE_HEIGHT / BACKGROUND_TILE_HEIGHT; ++j1) {
                graphics.blit(BACKGROUND, BACKGROUND_TILE_WIDTH * i1, BACKGROUND_TILE_HEIGHT * j1, 0.0F, 0.0F,
                        BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT, BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT);
            }
        }
        graphics.pose().popPose();
        graphics.disableScissor();
    }
    
    public void drawWindow(GuiGraphics graphics, int offsetX, int offsetY) {
        RenderSystem.enableBlend();
        graphics.blit(MISSION_CARD, offsetX, offsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        int width = WINDOW_WIDTH - TITLE_X * 2;

        FormattedText formatted = this.mission.title();

        int delta = this.minecraft.font.width(formatted) - width;

        formatted = this.minecraft.font.substrByWidth(formatted, width);

        if (delta > 0) {
            String text = this.minecraft.font.substrByWidth(formatted, width).getString();
            formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
        }
        graphics.drawString(this.minecraft.font, formatted.getString(), offsetX + TITLE_X, offsetY + TITLE_Y, 0x404040, false);

        graphics.enableScissor(offsetX + WINDOW_INSIDE_X, offsetY + WINDOW_INSIDE_Y,
                offsetX + WINDOW_INSIDE_X + INNER_PADDING + WINDOW_INSIDE_WIDTH - INNER_PADDING, offsetY + WINDOW_INSIDE_Y + INNER_PADDING + WINDOW_INSIDE_HEIGHT - INNER_PADDING);
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, this.scroll, 0.0F);

        for (FormattedCharSequence line : this.description) {
            graphics.drawString(this.minecraft.font, line,
                    offsetX + WINDOW_INSIDE_X + INNER_PADDING,
                    offsetY + WINDOW_INSIDE_Y + INNER_PADDING,
                    0xFFFFFF, true);
            offsetY += this.minecraft.font.lineHeight + 2;
        }
        graphics.pose().popPose();
        graphics.disableScissor();
    }
}
