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

import java.util.*;

public class MissionsScreen extends Screen {
    private static final ResourceLocation WINDOW = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/window.png");
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
    private static final ResourceLocation MISSION_CARD = new ResourceLocation(JujutsuKaisen.MOD_ID, "textures/gui/missions/mission_card.png");
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

    public static final int MISSION_CARD_WIDTH = 104;
    public static final int MISSION_CARD_HEIGHT = 128;

    private static final int MISSION_CARD_OFFSET_X = 20;
    public static final int MISSION_CARD_PADDING = 10;

    private static final int MISSION_GRADE_BUTTON_WIDTH = 52;
    private static final int MISSION_GRADE_BUTTON_HEIGHT = 52;

    private final Map<MissionGrade, List<Mission>> missions;

    private ExtendedSlider missionButtonsSlider;
    private ExtendedSlider missionCardsSlider;

    private MissionGrade grade;

    public MissionsScreen() {
        super(GameNarrator.NO_TITLE);

        this.missions = new LinkedHashMap<>();

        List<Mission> d = new ArrayList<>();
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));
        d.add(new Mission(Component.literal("D-tier mission title"), Component.literal("D-tier mission description")));

        List<Mission> c = new ArrayList<>();
        c.add(new Mission(Component.literal("C-tier mission title"), Component.literal("C-tier mission description")));

        List<Mission> b = new ArrayList<>();
        b.add(new Mission(Component.literal("B-tier mission title"), Component.literal("B-tier mission description")));

        List<Mission> a = new ArrayList<>();
        a.add(new Mission(Component.literal("A-tier mission title"), Component.literal("A-tier mission description")));

        List<Mission> s = new ArrayList<>();
        s.add(new Mission(Component.literal("S-tier mission title"), Component.literal("S-tier mission description")));

        this.missions.put(MissionGrade.D, d);
        this.missions.put(MissionGrade.C, c);
        this.missions.put(MissionGrade.B, b);
        this.missions.put(MissionGrade.A, a);
        this.missions.put(MissionGrade.S, s);

        this.grade = MissionGrade.D;
    }

    private void setGrade(MissionGrade grade) {
        this.grade = grade;

        this.rebuildWidgets();
    }

    @Override
    protected void init() {
        super.init();

        int windowOffsetX = WINDOW_OFFSET_X;
        int windowOffsetY = (this.height - WINDOW_HEIGHT) / 2;

        int missionCardOffsetX = windowOffsetX + WINDOW_WIDTH + MISSION_CARD_OFFSET_X;
        int missionCardOffsetY = (this.height - MISSION_CARD_HEIGHT) / 2;

        this.missionButtonsSlider = new VerticalSlider(windowOffsetX + WINDOW_INSIDE_X + WINDOW_INSIDE_WIDTH - 8, windowOffsetY + WINDOW_INSIDE_Y,
                8, WINDOW_INSIDE_HEIGHT, Component.empty(), Component.empty(), 0.0D,
                (MissionGrade.values().length * MISSION_GRADE_BUTTON_HEIGHT) - WINDOW_INSIDE_HEIGHT,
                0, 0.1D, 0, false);
        this.addRenderableWidget(this.missionButtonsSlider);

        this.missionCardsSlider = new ExtendedSlider(missionCardOffsetX, missionCardOffsetY + MISSION_CARD_HEIGHT + MISSION_CARD_PADDING,
                this.width - missionCardOffsetX - MISSION_CARD_OFFSET_X, 8, Component.empty(), Component.empty(), 0.0D,
                Math.max(0, (this.missions.get(this.grade).size() * (MISSION_CARD_WIDTH + MISSION_CARD_PADDING) - MISSION_CARD_PADDING) - (this.width - missionCardOffsetX - MISSION_CARD_OFFSET_X)),
                0, 0.1D, 0, false);
        this.addRenderableWidget(this.missionCardsSlider);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int windowOffsetX = WINDOW_OFFSET_X;
        int windowOffsetY = (this.height - WINDOW_HEIGHT) / 2;

        int missionCardOffsetX = windowOffsetX + WINDOW_WIDTH + MISSION_CARD_OFFSET_X;
        int missionCardOffsetY = (this.height - MISSION_CARD_HEIGHT) / 2;

        this.renderBackground(pGuiGraphics);
        this.renderWindowInside(pGuiGraphics, windowOffsetX, windowOffsetY);
        this.renderWindow(pGuiGraphics, windowOffsetX, windowOffsetY);
        this.renderMissionButtons(pGuiGraphics, windowOffsetX, windowOffsetY);
        this.renderMissionCards(pGuiGraphics, missionCardOffsetX, missionCardOffsetY);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    private void renderBackground(GuiGraphics graphics) {
        this.renderTransparentBackground(graphics);
    }

    private void renderWindowInside(GuiGraphics graphics, int offsetX, int offsetY) {
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

    private void renderWindow(GuiGraphics graphics, int offsetX, int offsetY) {
        RenderSystem.enableBlend();
        graphics.blit(WINDOW, offsetX, offsetY, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    private void renderMissionButtons(GuiGraphics graphics, int offsetX, int offsetY) {
        int x = offsetX + WINDOW_INSIDE_X;
        int y = offsetY + WINDOW_INSIDE_Y;

        graphics.enableScissor(x, y, x + WINDOW_INSIDE_WIDTH, y + WINDOW_INSIDE_HEIGHT);
        graphics.pose().pushPose();
        graphics.pose().translate(0.0D, -this.missionButtonsSlider.getValue(), 0.0D);

        for (int i = 0; i < MissionGrade.values().length; i++) {
            graphics.blit(MISSION_GRADE_BUTTON, x, y + i * MISSION_GRADE_BUTTON_HEIGHT, 0, 0,
                    MISSION_GRADE_BUTTON_WIDTH, MISSION_GRADE_BUTTON_HEIGHT, MISSION_GRADE_BUTTON_WIDTH, MISSION_GRADE_BUTTON_HEIGHT);
        }
        graphics.pose().popPose();
        graphics.disableScissor();
    }

    private void renderMissionCards(GuiGraphics graphics, int offsetX, int offsetY) {
        RenderSystem.enableBlend();

        graphics.enableScissor(offsetX, 0, this.width - MISSION_CARD_OFFSET_X, this.height);
        graphics.pose().pushPose();
        graphics.pose().translate(-this.missionCardsSlider.getValue(), 0.0F, 0.0D);

        List<MissionsScreen.Mission> missions = this.missions.get(this.grade);

        for (int i = 0; i < missions.size(); i++) {
            graphics.blit(MISSION_CARD, offsetX + i * (MISSION_CARD_WIDTH + MISSION_CARD_PADDING), offsetY,
                    0, 0, MISSION_CARD_WIDTH, MISSION_CARD_HEIGHT);
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
