package mew.mixin;

import mew.Mew;
import mew.module.modules.Optimization;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(value = {ItemRenderer.class}, priority = 9991)
public abstract class MixinOptimizationItemRenderer {

    @Inject(method = {"renderOverlays"}, at = {@At("HEAD")}, cancellable = true)
    private void onRenderOverlays(float partialTicks, CallbackInfo ci) {
        Optimization opt = (Optimization) Mew.moduleManager.modules.get(Optimization.class);
        if (opt != null && opt.isEnabled() && opt.noOverlays.getValue()) {
            ci.cancel();
        }
    }
}
