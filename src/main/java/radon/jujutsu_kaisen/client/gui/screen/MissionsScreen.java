package radon.jujutsu_kaisen.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.jetbrains.annotations.NotNull;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.client.gui.screen.widget.VerticalSlider;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.data.sorcerer.SorcererGrade;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MissionsScreen extends Screen {
    private static final ResourceLocation WINDOW = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/window.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
    private static final ResourceLocation MISSION_GRADE_BUTTON = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/mission_grade_button.png");

    public static final int WINDOW_WIDTH = 78;
    public static final int WINDOW_HEIGHT = 200;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    private static final int WINDOW_OFFSET_X = 20;
    private static final int WINDOW_OFFSET_Y = 20;
    private static final int WINDOW_INSIDE_WIDTH = 60;
    private static final int WINDOW_INSIDE_HEIGHT = 173;
    private static final int BACKGROUND_TILE_WIDTH = 16;
    private static final int BACKGROUND_TILE_HEIGHT = 16;

    private static final int MISSION_GRADE_BUTTON_WIDTH = 52;
    private static final int MISSION_GRADE_BUTTON_HEIGHT = 52;

    private final Map<MissionGrade, Set<Mission>> missions;

    private ExtendedSlider slider;

    public MissionsScreen() {
        super(GameNarrator.NO_TITLE);

        this.missions = new LinkedHashMap<>();

        Set<Mission> d = new HashSet<>();
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));

        Set<Mission> c = new HashSet<>();
        c.add(new Mission(Component.literal("C-tier mission title"), Component.literal("C-tier mission description")));

        Set<Mission> b = new HashSet<>();
        b.add(new Mission(Component.literal("B-tier mission title"), Component.literal("B-tier mission description")));

        Set<Mission> a = new HashSet<>();
        a.add(new Mission(Component.literal("A-tier mission title"), Component.literal("A-tier mission description")));

        Set<Mission> s = new HashSet<>();
        s.add(new Mission(Component.literal("S-tier mission title"), Component.literal("S-tier mission description")));

        this.missions.put(MissionGrade.D, s);
        this.missions.put(MissionGrade.C, s);
        this.missions.put(MissionGrade.B, s);
        this.missions.put(MissionGrade.A, s);
        this.missions.put(MissionGrade.S, s);
    }

    @Override
    protected void init() {
        super.init();

        int offsetX = WINDOW_OFFSET_X;
        int offsetY = (this.height - WINDOW_HEIGHT) / 2;

        double end = (MissionGrade.values().length * MISSION_GRADE_BUTTON_HEIGHT) - WINDOW_INSIDE_HEIGHT;

        this.slider = new VerticalSlider(offsetX + WINDOW_INSIDE_X + WINDOW_INSIDE_WIDTH - 8, offsetY + WINDOW_INSIDE_Y,
                8, WINDOW_INSIDE_HEIGHT, Component.empty(), Component.empty(),
                0.0D, end, 0, 0.1D, 0, false);
        this.addRenderableWidget(this.slider);
        this.setInitialFocus(this.slider);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int offsetX = WINDOW_OFFSET_X;
        int offsetY = (this.height - WINDOW_HEIGHT) / 2;

        this.renderBackground(pGuiGraphics);
        this.renderInside(pGuiGraphics, offsetX, offsetY);
        this.renderWindow(pGuiGraphics, offsetX, offsetY);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderBackground(GuiGraphics graphics) {
        this.renderTransparentBackground(graphics);
    }

    private void renderInside(GuiGraphics graphics, int offsetX, int offsetY) {
        int x = offsetX + WINDOW_INSIDE_X;
        int y = offsetY + WINDOW_INSIDE_Y;

        graphics.enableScissor(x, y, x + WINDOW_INSIDE_WIDTH, y + WINDOW_INSIDE_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate((float) x, (float) y, 0.0F);

        for (int i1 = -1; i1 <= WINDOW_INSIDE_WIDTH / BACKGROUND_TILE_WIDTH; ++i1) {
            for (int j1 = -1; j1 <= WINDOW_INSIDE_HEIGHT / BACKGROUND_TILE_HEIGHT; ++j1) {
                graphics.blit(BACKGROUND, BACKGROUND_TILE_WIDTH * i1, BACKGROUND_TILE_HEIGHT * j1, 0.0F, 0.0F,
                        BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT, BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT);
            }
        }

        graphics.pose().popPose();
        graphics.disableScissor();
    }

    public void renderWindow(GuiGraphics graphics, int offsetX, int offsetY) {
        RenderSystem.enableBlend();
        graphics.blit(WINDOW, offsetX, offsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        graphics.enableScissor(offsetX + WINDOW_INSIDE_X, offsetY + WINDOW_INSIDE_Y,
                offsetX + WINDOW_INSIDE_X + WINDOW_INSIDE_WIDTH, offsetY + WINDOW_INSIDE_Y + WINDOW_INSIDE_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate(0.0D, -this.slider.getValue(), 0.0D);

        for (int i = 0; i < MissionGrade.values().length; i++) {
            graphics.blit(MISSION_GRADE_BUTTON, offsetX + WINDOW_INSIDE_X, offsetY + WINDOW_INSIDE_Y + i * MISSION_GRADE_BUTTON_HEIGHT, 0, 0,
                    MISSION_GRADE_BUTTON_WIDTH, MISSION_GRADE_BUTTON_HEIGHT, MISSION_GRADE_BUTTON_WIDTH, MISSION_GRADE_BUTTON_HEIGHT);
        }
        graphics.pose().popPose();
        graphics.disableScissor();
    }

    private record Mission(Component title, Component description) {}

    private enum MissionGrade {
        D,
        C,
        B,
        A,
        S
    }
}
