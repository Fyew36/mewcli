package mew.mixin;

import mew.Mew;
import mew.module.modules.Optimization;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SideOnly(Side.CLIENT)
@Mixin(value = {World.class}, priority = 9991)
public abstract class MixinOptimizationWorld {

    @Inject(method = {"spawnParticle(Lnet/minecraft/util/EnumParticleTypes;ZDDDDDDD[I)V"}, at = {@At("HEAD")}, cancellable = true)
    private void onSpawnParticle(EnumParticleTypes particleType, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int[] parameters, CallbackInfo ci) {
        Optimization opt = (Optimization) Mew.moduleManager.modules.get(Optimization.class);
        if (opt != null && opt.isEnabled() && opt.noParticles.getValue()) {
            ci.cancel();
        }
    }
}
