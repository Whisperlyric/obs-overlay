package me.zziger.obsoverlay;

import com.mojang.blaze3d.ProjectionType;
//#if MC >= 260300
//$$ // 26.3 moved the whole GPU abstraction into the renderpearl library; only
//$$ // RenderSystem / RenderTarget / TextureTarget stay in blaze3d
//$$ import com.mojang.renderpearl.api.GpuFormat;
//$$ import com.mojang.renderpearl.api.buffers.GpuBuffer;
//$$ import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
//$$ import com.mojang.renderpearl.api.commands.CommandEncoder;
//$$ import com.mojang.renderpearl.api.commands.RenderPass;
//$$ import com.mojang.renderpearl.api.textures.FilterMode;
//$$ import com.mojang.renderpearl.api.textures.GpuTexture;
//$$ import com.mojang.renderpearl.backend.opengl.GlStateManager;
//#elseif MC >= 260200
//$$ import com.mojang.blaze3d.GpuFormat;
//$$ import com.mojang.blaze3d.buffers.GpuBuffer;
//$$ import com.mojang.blaze3d.buffers.GpuBufferSlice;
//$$ import com.mojang.blaze3d.systems.CommandEncoder;
//$$ import com.mojang.blaze3d.systems.RenderPass;
//$$ import com.mojang.blaze3d.textures.FilterMode;
//$$ import com.mojang.blaze3d.textures.GpuTexture;
//$$ import com.mojang.blaze3d.opengl.GlStateManager;
//#else
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.opengl.GlStateManager;
//#endif
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zziger.obsoverlay.component.IOverlayComponent;
import me.zziger.obsoverlay.mixin.accessor.GuiRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
//#if MC >= 260200
//$$ import org.joml.Vector4f;
//#endif

import java.io.Closeable;
import java.nio.ByteBuffer;
//#if MC >= 260200
//$$ import java.util.Optional;
//$$ import java.util.OptionalDouble;
//#else
import java.util.OptionalInt;
//#endif

public class OverlayRenderer implements Closeable {
    private boolean framebufferOverridden = false;
    private OverlayFramebuffer overlayFramebuffer;

    private final GuiRenderState overlayGuiState = new GuiRenderState();
    private GuiGraphicsExtractor overlayGuiGraphicsExtractor;
    private GuiRenderer overlayGuiRenderer;

    private static GpuBufferSlice identityProjectionBuffer;

    OverlayRenderer() {
        OverlayHook.init();
        OverlayHook.subscribe(this::renderFrame);
        initializeFramebuffers();
        initializeIdentityProjection();
    }

    private static void initializeIdentityProjection() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(64);
        new Matrix4f().get(buffer);
        GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(
                () -> "Identity Projection",
                GpuBuffer.USAGE_UNIFORM,
                buffer
        );
        identityProjectionBuffer = gpuBuffer.slice();
    }

    public void close() {
        OverlayHook.unsubscribe(this::renderFrame);
    }

    private void initializeFramebuffers() {
        Minecraft client = Minecraft.getInstance();
        //#if MC >= 260300
        //$$ // 26.3 TextureTarget takes color+depth formats instead of a useDepth flag
        //$$ RenderTarget simpleFramebuffer = new TextureTarget("Overlay Target", client.getWindow().getWidth(), client.getWindow().getHeight(), GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
        //#elseif MC >= 260200
        //$$ RenderTarget simpleFramebuffer = new TextureTarget("Overlay Target", client.getWindow().getWidth(), client.getWindow().getHeight(), true, GpuFormat.RGBA8_UNORM);
        //#else
        RenderTarget simpleFramebuffer = new TextureTarget("Overlay Target", client.getWindow().getWidth(), client.getWindow().getHeight(), true);
        //#endif
        clearFramebuffer(simpleFramebuffer);
        this.overlayFramebuffer = new OverlayFramebuffer(simpleFramebuffer);
    }

    private void markOverlayDirty() {
        if (overlayFramebuffer == null) return;
        overlayFramebuffer.dirty = true;
    }

    private static void clearFramebuffer(RenderTarget target) {
        GpuTexture colorTexture = target.getColorTexture();
        if (colorTexture == null) return;

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        //#if MC >= 260300
        //$$ if (target.getDepthTexture() != null) {
        //#else
        if (target.useDepth && target.getDepthTexture() != null) {
            //#endif
            //#if MC >= 260200
            //$$ encoder.clearColorAndDepthTextures(colorTexture, new Vector4f(0, 0, 0, 0), target.getDepthTexture(), 1.0);
            //#else
            encoder.clearColorAndDepthTextures(colorTexture, 0, target.getDepthTexture(), 1.0);
            //#endif
        } else {
            //#if MC >= 260200
            //$$ encoder.clearColorTexture(colorTexture, new Vector4f(0, 0, 0, 0));
            //#else
            encoder.clearColorTexture(colorTexture,0);
            //#endif
        }
    }

    public @NotNull GuiRenderer getOverlayGuiRenderer(GuiRenderer copyFrom) {
        if (overlayGuiRenderer != null) return overlayGuiRenderer;

        var accessor = (GuiRendererAccessor) copyFrom;
        //#if MC >= 260200
        //$$ this.overlayGuiRenderer = new GuiRenderer(
        //$$         this.overlayGuiState,
        //$$         accessor.getFeatureRenderDispatcher(),
        //$$         accessor.getPictureInPictureRenderers().values().stream().toList()
        //$$ );
        //#else
        this.overlayGuiRenderer = new GuiRenderer(
                this.overlayGuiState,
                accessor.getBufferSource(),
                accessor.getSubmitNodeCollector(),
                accessor.getFeatureRenderDispatcher(),
                accessor.getPictureInPictureRenderers().values().stream().toList()
        );
        //#endif

        return overlayGuiRenderer;
    }

    public RenderTarget getGuiRenderTarget() {
        if (this.framebufferOverridden && overlayFramebuffer != null) return overlayFramebuffer.object;
        //#if MC >= 260200
        //$$ // Minecraft.getMainRenderTarget() moved to GameRenderer.mainRenderTarget() in 26.2
        //$$ return Minecraft.getInstance().gameRenderer.mainRenderTarget();
        //#else
        else return Minecraft.getInstance().getMainRenderTarget();
        //#endif
    }

    public GuiGraphicsExtractor getGuiGraphicsExtractor() {
        return this.overlayGuiGraphicsExtractor;
    }

    public @NotNull GuiGraphicsExtractor getGuiGraphicsExtractor(IOverlayComponent component, GuiGraphicsExtractor original) {
        if (!component.isOverlayEnabled()) return original;
        if (component.isHidden()) return DummyGuiGraphicsExtractor.INSTANCE;
        GuiGraphicsExtractor GuiGraphicsExtractor = getGuiGraphicsExtractor();
        return GuiGraphicsExtractor != null ? GuiGraphicsExtractor : original;
    }

    public void beginDraw() {
        if (overlayFramebuffer == null) return;
        framebufferOverridden = true;
        markOverlayDirty();
    }

    public void endDraw() {
        if (overlayFramebuffer == null) return;
        framebufferOverridden = false;
    }

    public void onResolutionChanged(Minecraft client) {
        if (overlayFramebuffer == null) return;
        overlayFramebuffer.object.resize(
                client.getWindow().getWidth(),
                client.getWindow().getHeight()
        );
    }

    //#if MC < 260200
    private static void renderQuad(RenderTarget framebuffer) {
        if (framebuffer.getColorTexture() == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (width <= 0 || height <= 0) return;

        GlStateManager._viewport(0, 0, width, height);

        // screenquad.vsh applies ProjMat * ModelViewMat to vertex positions.
        // Minecraft's GUI rendering leaves non-identity matrices, which distort
        // the fullscreen triangle. Reset both to identity for the overlay pass.
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(identityProjectionBuffer, ProjectionType.ORTHOGRAPHIC);
        Matrix4fStack mvStack = RenderSystem.getModelViewStack();
        Matrix4f savedModelView = new Matrix4f(mvStack);
        mvStack.identity();

        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                () -> "Overlay Screen",
                new OverlayScreenTextureView(width, height),
                OptionalInt.empty()
        )) {
            renderPass.setPipeline(OverlayPipelines.OVERLAY_COMPOSITE);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture(
                    "InSampler",
                    framebuffer.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)
            );
            renderPass.draw(0, 3);
        }

        // Restore original matrices
        mvStack.set(savedModelView);
        RenderSystem.restoreProjectionMatrix();
    }
    //#endif

    //#if MC >= 260200
    //$$ /**
    //$$  * 26.2+: composites the overlay framebuffer over the main render target right
    //$$  * after the GUI pass, inside GameRenderer.render. The pre-26.2 end-of-frame
    //$$  * blit to FBO 0 is no longer viable: the game presents the frame by blitting
    //$$  * an internal texture to the window surface, so anything drawn to FBO 0 at
    //$$  * swap time is overwritten by the swapchain image.
    //$$  */
    //$$ public void compositeOverlay() {
    //$$     if (overlayFramebuffer == null || !overlayFramebuffer.dirty) return;
    //$$     overlayFramebuffer.dirty = false;
    //$$
    //$$     RenderTarget framebuffer = overlayFramebuffer.object;
    //$$     if (framebuffer.getColorTexture() == null) return;
    //$$
    //$$     RenderTarget mainTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
    //$$
    //$$     RenderSystem.backupProjectionMatrix();
    //$$     RenderSystem.setProjectionMatrix(identityProjectionBuffer, ProjectionType.ORTHOGRAPHIC);
    //$$     Matrix4fStack mvStack = RenderSystem.getModelViewStack();
    //$$     Matrix4f savedModelView = new Matrix4f(mvStack);
    //$$     mvStack.identity();
    //$$
    //$$     try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
    //$$             () -> "Overlay Screen",
    //$$             mainTarget.getColorTextureView(),
    //$$             Optional.empty(),
    //#if MC >= 260300
    //$$             mainTarget.getDepthTextureView(),
    //#else
    //$$             mainTarget.useDepth ? mainTarget.getDepthTextureView() : null,
    //#endif
    //$$             OptionalDouble.empty()
    //$$     )) {
    //#if MC >= 260300
    //$$         // 26.3: pipelines must be compiled through RenderSystem before use
    //$$         renderPass.setPipeline(RenderSystem.getCompiledPipeline(OverlayPipelines.OVERLAY_COMPOSITE));
    //#else
    //$$         renderPass.setPipeline(OverlayPipelines.OVERLAY_COMPOSITE);
    //#endif
    //$$         RenderSystem.bindDefaultUniforms(renderPass);
    //#if MC >= 260300
    //$$         // 26.3 renamed RenderPass.bindTexture to setUniform
    //$$         renderPass.setUniform(
    //#else
    //$$         renderPass.bindTexture(
    //#endif
    //$$                 "InSampler",
    //$$                 framebuffer.getColorTextureView(),
    //$$                 RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)
    //$$         );
    //$$         // screenquad: 3 vertices, 1 instance, first vertex 0, base instance 0
    //$$         renderPass.draw(3, 1, 0, 0);
    //$$     }
    //$$
    //$$     mvStack.set(savedModelView);
    //$$     RenderSystem.restoreProjectionMatrix();
    //$$ }
    //#endif

    public void beginFrame() {
        if (overlayFramebuffer == null) return;
        clearFramebuffer(overlayFramebuffer.object);
        resetGuiExtraction();
    }

    private void resetGuiExtraction() {
        Minecraft minecraft = Minecraft.getInstance();
        int mouseX = (int) minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
        int mouseY = (int) minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());

        this.overlayGuiState.reset();
        this.overlayGuiGraphicsExtractor = new GuiGraphicsExtractor(minecraft, overlayGuiState, mouseX, mouseY);
    }

    public void renderFrame() {
        if (overlayFramebuffer == null || !overlayFramebuffer.dirty) return;
        overlayFramebuffer.dirty = false;
        //#if MC < 260200
        renderQuad(overlayFramebuffer.object);
        //#endif
    }
}
