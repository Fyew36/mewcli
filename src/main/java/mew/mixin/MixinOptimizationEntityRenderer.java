package mew.mixin;

import mew.Mew;
import mew.module.modules.Optimization;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(value = {EntityRenderer.class}, priority = 9991)
public abstract class MixinOptimizationEntityRenderer {

    @Inject(method = {"renderRainSnow"}, at = {@At("HEAD")}, cancellable = true)
    private void onRenderRainSnow(float partialTicks, CallbackInfo ci) {
        Optimization opt = (Optimization) Mew.moduleManager.modules.get(Optimization.class);
        if (opt != null && opt.isEnabled() && opt.noWeather.getValue()) {
            ci.cancel();
        }
    }
}
