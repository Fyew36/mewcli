package mew.ui;

import mew.ui.components.Component;
import mew.ui.components.BindComponent;
import mew.ui.components.CategoryComponent;
import mew.ui.components.ModuleComponent;
import mew.util.Timer;
import mew.module.modules.GuiModule;
import mew.util.shader.BlurUtils;
import mew.util.shader.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClickGui extends GuiScreen {
    private static ClickGui instance;
    private Timer backgroundFade;
    private Timer blurSmooth;
    private ScaledResolution sr;
    public static ArrayList<CategoryComponent> categories;
    private int actualScreenWidth;
    private int actualScreenHeight;
    private static boolean isNotFirstOpen;
    private boolean pendingScaleRefresh;
    public ClickGui() {
        categories = new ArrayList<>();
        int xOffset = 5;
        String[] values = new String[]{"Combat", "Movement", "Render", "Player", "Misc", "Latency", "Minigames", "Target"};

        for (int i = 0; i < values.length; ++i) {
            String c = values[i];
            CategoryComponent categoryComponent = new CategoryComponent(c);
            categoryComponent.setY(5, false);
            categoryComponent.setX(xOffset, false);
            categories.add(categoryComponent);
            xOffset += 98;
        }
    }

    public void initMain() {
        (this.blurSmooth = this.backgroundFade = new Timer(500.0F)).start();
    }

    @Override
    public void initGui() {
        super.initGui();
        instance = this;
        this.sr = new ScaledResolution(mc);
        this.actualScreenWidth = this.sr.getScaledWidth();
        this.actualScreenHeight = this.sr.getScaledHeight();

        for (CategoryComponent categoryComponent : categories) {
            categoryComponent.setScreenSize(this.width, this.height);
            categoryComponent.limitPositions();
            categoryComponent.reloadModules();
        }
    }

    private List<CategoryComponent> getCategoriesInRenderOrder() {
        List<CategoryComponent> renderOrder = new ArrayList<>(categories);
        renderOrder.sort(Comparator.comparingLong(c -> c.lastInteractedTime));
        return renderOrder;
    }

    private CategoryComponent getTopmostUnderCursor(List<CategoryComponent> renderOrder, int x, int y) {
        for (int i = renderOrder.size() - 1; i >= 0; i--) {
            if (renderOrder.get(i).overRect(x, y)) {
                return renderOrder.get(i);
            }
        }
        return null;
    }

    public void drawScreen(int x, int y, float p) {
        GuiModule guiModule = (GuiModule) mew.Mew.moduleManager.modules.get(GuiModule.class);
        if (guiModule != null && guiModule.blur.getValue()) {
            BlurUtils.prepareBlur();
            RoundedUtils.drawRound(0, 0, this.width, this.height, 0.0f, true, Color.black);
            BlurUtils.blurEnd(2, 3.0f);
        }

        drawRect(0, 0, this.width, this.height, new Color(0, 0, 0, 130).getRGB());

        List<CategoryComponent> renderOrder = getCategoriesInRenderOrder();
        CategoryComponent topmostUnderCursor = getTopmostUnderCursor(renderOrder, x, y);
        for (CategoryComponent c : renderOrder) {
            c.render(this.fontRendererObj);
            c.mousePosition(x, y, c == topmostUnderCursor);

            for (Component m : c.getModules()) {
                m.drawScreen(x, y);
            }
        }

        GL11.glColor3f(1.0f, 1.0f, 1.0f);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        List<CategoryComponent> inputOrder = new ArrayList<>(categories);
        inputOrder.sort((a, b) -> Long.compare(b.lastInteractedTime, a.lastInteractedTime));
        CategoryComponent topmostCategory = null;
        for (CategoryComponent category : inputOrder) {
            if (category.overRect(mouseX, mouseY)) {
                topmostCategory = category;
                break;
            }
        }

        if (topmostCategory != null) {
            topmostCategory.markInteracted();
        }

        if (mouseButton == 0) {
            for (CategoryComponent category : categories) {
                category.overTitle(false);
            }
            if (topmostCategory != null && topmostCategory.draggable(mouseX, mouseY)) {
                topmostCategory.overTitle(true);
                topmostCategory.xx = mouseX - topmostCategory.getX();
                topmostCategory.yy = mouseY - topmostCategory.getY();
                topmostCategory.dragging = true;
            }
        }

        if (mouseButton == 1 && topmostCategory != null && topmostCategory.overTitle(mouseX, mouseY)) {
            topmostCategory.mouseClicked(!topmostCategory.isOpened());
        }

        if (topmostCategory != null && topmostCategory.isOpened() && !topmostCategory.getModules().isEmpty() && !topmostCategory.overTitle(mouseX, mouseY)) {
            for (Component component : topmostCategory.getModules()) {
                if (component.onClick(mouseX, mouseY, mouseButton)) {
                    break;
                }
            }
        }
    }

    public void mouseReleased(int x, int y, int button) {
        if (button == 0) {
            for (CategoryComponent category : categories) {
                category.overTitle(false);
                if (category.isOpened() && !category.getModules().isEmpty()) {
                    for (Component module : category.getModules()) {
                        module.mouseReleased(x, y, button);
                    }
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheelInput = Mouse.getDWheel();
        if (wheelInput != 0) {
            int mouseX = Mouse.getEventX() * this.width / mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / mc.displayHeight - 1;

            for (CategoryComponent category : categories) {
                category.onScroll(wheelInput, mouseX, mouseY);
            }
        }
    }

    @Override
    public void keyTyped(char t, int k) {
        if (k == Keyboard.KEY_ESCAPE) {
            if (!binding()) {
                this.mc.displayGuiScreen(null);
                return;
            }
        }

        for (CategoryComponent category : categories) {
            if (category.isOpened() && !category.getModules().isEmpty()) {
                for (Component module : category.getModules()) {
                    module.keyTyped(t, k);
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        for (CategoryComponent c : categories) {
            c.dragging = false;
            c.onGuiClosed();
            for (Component m : c.getModules()) {
                m.onGuiClosed();
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private boolean binding() {
        for (CategoryComponent c : categories) {
            for (Component m : c.getModules()) {
                if (m instanceof ModuleComponent) {
                    for (Component component : ((ModuleComponent) m).settings) {
                        if (component instanceof BindComponent && ((BindComponent) component).isBinding) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void onSliderChange() {
        for (CategoryComponent c : categories) {
            for (Component m : c.getModules()) {
                if (m instanceof ModuleComponent) {
                    ((ModuleComponent) m).onSliderChange();
                }
            }
        }
    }

    public void requestScaleRefresh() {
        this.pendingScaleRefresh = true;
    }

    public static double getActiveRenderScale() {
        return 1.0D;
    }

    public static ClickGui getInstance() {
        return instance;
    }

    public static boolean isModern() {
        return false;
    }
}
