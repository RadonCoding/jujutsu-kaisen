package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.data.mission.Mission;
import radon.jujutsu_kaisen.data.mission.MissionGrade;

import java.util.List;

public class MissionCard {
    private static final ResourceLocation MISSION_CARD = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/mission_card.png");
    private static final ResourceLocation OUTLINE = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/outline.png");
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
    private final Mission mission;
    private final List<FormattedCharSequence> description;

    private double scroll;

    public MissionCard(Minecraft minecraft, Mission mission) {
        this.minecraft = minecraft;
        this.mission = mission;
        this.description = this.minecraft.font.split(this.mission.getType().getDescription(), WINDOW_INSIDE_WIDTH - (INNER_PADDING * 2));
    }

    public Mission getMission() {
        return this.mission;
    }

    public void scroll(double dragY) {
        int height = this.description.size() * (this.minecraft.font.lineHeight + 2) - 2 + this.minecraft.font.lineHeight;
        int displayed = WINDOW_INSIDE_HEIGHT - (INNER_PADDING * 2);

        if (height - displayed > 0) {
            this.scroll = Mth.clamp(this.scroll + dragY, -(height - displayed), 0.0D);
        }
    }

    public void drawTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        int width = WINDOW_WIDTH - TITLE_X * 2;

        if (this.minecraft.font.width(this.mission.getType().getTitle()) <= width) return;

        graphics.renderTooltip(this.minecraft.font, this.mission.getType().getTitle(), mouseX, mouseY);
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

    public void drawWindow(GuiGraphics graphics, int offsetX, int offsetY, boolean selected) {
        RenderSystem.enableBlend();
        graphics.blit(MISSION_CARD, offsetX, offsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (selected) {
            graphics.blit(OUTLINE, offsetX, offsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }

        int width = WINDOW_WIDTH - TITLE_X * 2;

        FormattedText formatted = this.mission.getType().getTitle();

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
                    0xffffff, true);
            offsetY += this.minecraft.font.lineHeight + 2;
        }

        offsetY += this.minecraft.font.lineHeight + 2;

        BlockPos pos = this.mission.getPos();

        for (FormattedCharSequence line : this.minecraft.font.split(FormattedText.of(String.format("XYZ: %d, %d, %d", pos.getX(), pos.getY(), pos.getZ())),
                WINDOW_INSIDE_WIDTH - (INNER_PADDING * 2))) {
            graphics.drawString(this.minecraft.font, line,
                    offsetX + WINDOW_INSIDE_X + INNER_PADDING,
                    offsetY + WINDOW_INSIDE_Y + INNER_PADDING,
                    0xededed, true);
            offsetY += this.minecraft.font.lineHeight + 2;
        }
        graphics.pose().popPose();
        graphics.disableScissor();
    }
}
