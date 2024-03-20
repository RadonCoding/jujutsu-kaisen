package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import radon.jujutsu_kaisen.JujutsuKaisen;

public class MissionCard {
    private static final ResourceLocation MISSION_CARD = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/mission_card.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
    
    public static final int WINDOW_WIDTH = 104;
    public static final int WINDOW_HEIGHT = 128;
    public static final int WINDOW_INSIDE_X = 9;
    public static final int INSIDE_Y = 18;
    public static final int OUTER_PADDING = 10;
    public static final int INNER_PADDING = 6;
    public static final int INSIDE_WIDTH = 86;
    public static final int INSIDE_HEIGHT = 101;
    public static final int TITLE_X = 8;
    public static final int TITLE_Y = 6;
    
    private static final int BACKGROUND_TILE_WIDTH = 16;
    private static final int BACKGROUND_TILE_HEIGHT = 16;
    
    private final Minecraft minecraft;
    private final MissionsScreen.Mission mission;
    
    public MissionCard(Minecraft minecraft, MissionsScreen.Mission mission) {
        this.minecraft = minecraft;
        this.mission = mission;
    }
    
    public void drawInside(GuiGraphics graphics, int offsetX, int offsetY) {
        graphics.enableScissor(offsetX, offsetY, offsetX + MissionCard.INSIDE_WIDTH, offsetY + MissionCard.INSIDE_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate((float) offsetX, (float) offsetY, 0.0F);

        for (int i1 = -1; i1 <= MissionCard.INSIDE_WIDTH / BACKGROUND_TILE_WIDTH; ++i1) {
            for (int j1 = -1; j1 <= MissionCard.INSIDE_HEIGHT / BACKGROUND_TILE_HEIGHT; ++j1) {
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
            String text = formatted.getString();
            formatted = FormattedText.of(String.format("%s...", text.substring(0, text.length() - 3)));
        }
        graphics.drawString(this.minecraft.font, formatted.getString(), offsetX + TITLE_X, offsetY + TITLE_Y, 0x404040, false);

        for (FormattedCharSequence line : this.minecraft.font.split(this.mission.description(), INSIDE_WIDTH - (INNER_PADDING * 2))) {
            graphics.drawString(this.minecraft.font, line,
                    offsetX + WINDOW_INSIDE_X + INNER_PADDING,
                    offsetY + INSIDE_Y + INNER_PADDING,
                    0xFFFFFF, true);
            offsetY += this.minecraft.font.lineHeight + 2;
        }
    }
}
