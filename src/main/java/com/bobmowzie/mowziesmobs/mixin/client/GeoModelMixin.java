package com.bobmowzie.mowziesmobs.mixin.client;

import com.bobmowzie.mowziesmobs.server.entity.MowzieEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/** Calls from the inventory rendering sometimes are returned early and this seems to cause issues in animating the entity in the inventory */
@Mixin(value = GeoModel.class, remap = false)
public abstract class GeoModelMixin {
    @Shadow private long lastRenderedInstance;

    // FIXME 1.21 :: currently the rendering seems to switch between doing the animation and doing no animation (the umvuthana mob is less likely to be impacted)
    // FIXME 1.21 :: depending on when you open the inventory (a higher tick rate seems to help?) this issue does not occur
    // FIXME 1.21 :: maybe a problem with the 'adjustTick' logic or sth. similar?
    @WrapOperation(method = "handleAnimations", at = @At(value = "FIELD", target = "Lsoftware/bernie/geckolib/model/GeoModel;lastRenderedInstance:J", ordinal = 0))
    private <T extends GeoAnimatable> long test(GeoModel<?> instance, Operation<Long> original, @Local(argsOnly = true) T animatable) {
        if (animatable instanceof MowzieEntity entity && entity.renderingInGUI) {
            // Don't return early
            return -lastRenderedInstance;
        }

        return original.call(instance);
    }
}
