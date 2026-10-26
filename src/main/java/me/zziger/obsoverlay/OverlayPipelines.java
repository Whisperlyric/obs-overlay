package me.zziger.obsoverlay;

//#if MC >= 260300
//$$ // 26.3 moved the pipeline definitions into the renderpearl library
//$$ import com.mojang.renderpearl.api.GpuFormat;
//$$ import com.mojang.renderpearl.api.pipeline.BlendFunction;
//$$ import com.mojang.renderpearl.api.pipeline.ColorTargetState;
//$$ import com.mojang.renderpearl.api.pipeline.CompareOp;
//$$ import com.mojang.renderpearl.api.pipeline.DepthStencilState;
//$$ import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
//$$ import com.mojang.renderpearl.api.pipeline.RenderPipeline;
//#elseif MC >= 260200
//$$ // 26.2 rewrote pipeline definitions: samplers are declared via BindGroupLayout,
//$$ // vertex formats via withVertexBinding + PrimitiveTopology, ColorTargetState carries a GpuFormat
//$$ import com.mojang.blaze3d.GpuFormat;
//$$ import com.mojang.blaze3d.PrimitiveTopology;
//$$ import com.mojang.blaze3d.pipeline.BlendFunction;
//$$ import com.mojang.blaze3d.pipeline.ColorTargetState;
//$$ import com.mojang.blaze3d.pipeline.DepthStencilState;
//$$ import com.mojang.blaze3d.pipeline.RenderPipeline;
//$$ import com.mojang.blaze3d.platform.CompareOp;
//#else
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
//#endif
//#if MC >= 260200
//$$ import net.minecraft.client.renderer.BindGroupLayouts;
//#endif
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public final class OverlayPipelines {
    public static final RenderPipeline OVERLAY_COMPOSITE = register(
            RenderPipeline.builder()
                    .withLocation(Identifier.fromNamespaceAndPath("obs_overlay", "pipeline/overlay_composite"))
                    .withVertexShader("core/screenquad")
                    .withFragmentShader(Identifier.fromNamespaceAndPath("obs_overlay", "core/overlay"))
                    //#if MC >= 260200
                    //$$ .withBindGroupLayout(BindGroupLayouts.IN_SAMPLER)
                    //$$ .withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT), GpuFormat.RGBA8_UNORM, ColorTargetState.WRITE_COLOR))
                    //$$ .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                    //$$ .withCull(false)
                    //$$ .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                    //#else
                    .withSampler("InSampler")
                    .withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT), ColorTargetState.WRITE_COLOR))
                    .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
                    .withCull(false)
                    .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
                    //#endif
                    .build()
    );

    private static RenderPipeline register(RenderPipeline pipeline) {
        //#if MC >= 260300
        //$$ // RenderPipelines.register no longer exists in 26.3; pipelines are compiled
        //$$ // lazily through RenderSystem.getCompiledPipeline on first use
        //$$ return pipeline;
        //#else
        return RenderPipelines.register(pipeline);
        //#endif
    }
}