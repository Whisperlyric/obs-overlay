package me.zziger.obsoverlay.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.zziger.obsoverlay.OBSOverlay;
import me.zziger.obsoverlay.OBSOverlayConfig;
import me.zziger.obsoverlay.OverlayRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift = At.Shift.BEFORE))
    private void renderTestIcon(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        if (OBSOverlayConfig.get().showTestIcon && OBSOverlay.getIsInitialized()) {
            try {
                GuiGraphicsExtractor overlayGuiGraphicsExtractor = OBSOverlay.getAPI().getOverlayGuiGraphicsExtractor();
                if (overlayGuiGraphicsExtractor != null) {
                    overlayGuiGraphicsExtractor.blitSprite(
                            RenderPipelines.GUI_TEXTURED,
                            Identifier.withDefaultNamespace("icon/checkmark"),
                            0, 0, 16, 16
                    );
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
    private void redirectGuiRendering(GuiRenderer instance, GpuBufferSlice fogBuffer) {
        instance.render(fogBuffer);

        if (OBSOverlay.getIsInitialized()) {
            OverlayRenderer overlayRenderer = OBSOverlay.getRenderer();
            if (overlayRenderer != null) {
                var obs_overlay$customRenderer = overlayRenderer.getOverlayGuiRenderer(instance);
                overlayRenderer.beginDraw();
                obs_overlay$customRenderer.render(fogBuffer);
                overlayRenderer.endDraw();
            }
        }
    }
}