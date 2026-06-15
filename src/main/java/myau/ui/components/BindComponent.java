package myau.ui.components;

import myau.Myau;
import myau.module.modules.GuiModule;
import myau.module.modules.HUD;
import myau.ui.ClickGui;
import myau.ui.Component;
import myau.ui.dataset.BindStage;
import myau.util.KeyBindUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BindComponent implements Component {
    private boolean isBinding;
    private final ModuleComponent parentModule;
    private int offsetY;
    private int x;
    private int y;

    public BindComponent(ModuleComponent b, int offsetY) {
        this.parentModule = b;
        this.x = b.category.getX() + b.category.getWidth();
        this.y = b.category.getY() + b.offsetY;
        this.offsetY = offsetY;
    }

    public void draw(AtomicInteger offset) {
        if (ClickGui.isModern()) {
            int color = this.isBinding
                    ? new Color(100, 200, 255).getRGB()
                    : ((HUD) Myau.moduleManager.modules.get(HUD.class)).getColor(System.currentTimeMillis(), offset.get()).getRGB();

            String displayText = this.isBinding ? BindStage.binding : BindStage.bind + ": " + KeyBindUtil.getKeyName(this.parentModule.mod.getKey());

            Gui.drawRect(
                    this.parentModule.category.getX() + 4,
                    this.parentModule.category.getY() + this.offsetY + 1,
                    this.parentModule.category.getX() + this.parentModule.category.getWidth() - 4,
                    this.parentModule.category.getY() + this.offsetY + 11,
                    new Color(30, 30, 40, 100).getRGB()
            );

            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    displayText,
                    (float) ((this.parentModule.category.getX() + 6) * 2),
                    (float) ((this.parentModule.category.getY() + this.offsetY + 2) * 2),
                    color
            );
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 0.5D);
            String displayText = this.isBinding ? BindStage.binding : BindStage.bind + ": " + KeyBindUtil.getKeyName(this.parentModule.mod.getKey());
            int color = ((HUD) Myau.moduleManager.modules.get(HUD.class)).getColor(System.currentTimeMillis(), offset.get()).getRGB();
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(
                    displayText,
                    (float) ((this.parentModule.category.getX() + 4) * 2),
                    (float) ((this.parentModule.category.getY() + this.offsetY + 3) * 2),
                    color
            );
            GL11.glPopMatrix();
        }
    }

    @Override
    public void update(int mousePosX, int mousePosY) {
        boolean h = this.isHovered(mousePosX, mousePosY);
        this.y = this.parentModule.category.getY() + this.offsetY;
        this.x = this.parentModule.category.getX();
    }

    public void mouseDown(int x, int y, int button) {
        if (this.isHovered(x, y) && button == 0 && this.parentModule.panelExpand) {
            this.isBinding = !this.isBinding;
        } else if (this.isBinding && this.parentModule.panelExpand) {
            int keyIndex = button - 100;

            if (button == 0) {
                this.isBinding = false;
                return;
            }

            this.parentModule.mod.setKey(keyIndex);
            this.isBinding = false;
        }
    }

    @Override
    public void mouseReleased(int x, int y, int button) {

    }

    @Override
    public void keyTyped(char chatTyped, int keyCode) {
        if (this.isBinding) {
            if (keyCode == 1) {
                this.isBinding = false;
                return;
            }

            if (keyCode == 11) {
                if (this.parentModule.mod instanceof GuiModule) {
                    this.parentModule.mod.setKey(54);
                } else {
                    this.parentModule.mod.setKey(0);
                }
            } else {
                this.parentModule.mod.setKey(keyCode);
            }

            this.isBinding = false;
        }
    }

    @Override
    public void setComponentStartAt(int newOffsetY) {
        this.offsetY = newOffsetY;
    }

    public boolean isHovered(int x, int y) {
        return x > this.x && x < this.x + this.parentModule.category.getWidth() && y > this.y - 1 && y < this.y + 12;
    }

    public int getHeight() {
        return 12;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
