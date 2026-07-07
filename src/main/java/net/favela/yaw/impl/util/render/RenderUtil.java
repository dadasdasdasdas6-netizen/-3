package net.favela.yaw.impl.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;

import static net.favela.yaw.impl.util.wrapper.Wrapper.MC;

public class RenderUtil {

    public static void drawBoxOutline(PoseStack stack, AABB box, Color color, float lineWidth) {
        double h = (lineWidth * 0.02) / 2.0;
        double x1 = box.minX, y1 = box.minY, z1 = box.minZ;
        double x2 = box.maxX, y2 = box.maxY, z2 = box.maxZ;

        drawBoxFilled(stack, new AABB(x1 - h, y1 - h, z1 - h, x2 + h, y1 + h, z1 + h), color);
        drawBoxFilled(stack, new AABB(x1 - h, y2 - h, z1 - h, x2 + h, y2 + h, z1 + h), color);
        drawBoxFilled(stack, new AABB(x1 - h, y1 - h, z2 - h, x2 + h, y1 + h, z2 + h), color);
        drawBoxFilled(stack, new AABB(x1 - h, y2 - h, z2 - h, x2 + h, y2 + h, z2 + h), color);

        drawBoxFilled(stack, new AABB(x1 - h, y1 - h, z1 - h, x1 + h, y2 + h, z1 + h), color);
        drawBoxFilled(stack, new AABB(x2 - h, y1 - h, z1 - h, x2 + h, y2 + h, z1 + h), color);
        drawBoxFilled(stack, new AABB(x1 - h, y1 - h, z2 - h, x1 + h, y2 + h, z2 + h), color);
        drawBoxFilled(stack, new AABB(x2 - h, y1 - h, z2 - h, x2 + h, y2 + h, z2 + h), color);

        drawBoxFilled(stack, new AABB(x1 - h, y1 - h, z1 - h, x1 + h, y1 + h, z2 + h), color);
        drawBoxFilled(stack, new AABB(x2 - h, y1 - h, z1 - h, x2 + h, y1 + h, z2 + h), color);
        drawBoxFilled(stack, new AABB(x1 - h, y2 - h, z1 - h, x1 + h, y2 + h, z2 + h), color);
        drawBoxFilled(stack, new AABB(x2 - h, y2 - h, z1 - h, x2 + h, y2 + h, z2 + h), color);
    }

    public static void drawBoxFilled(PoseStack stack, AABB box, Color c) {
        Vec3 cam = MC.gameRenderer.mainCamera().position();

        float minX = (float) (box.minX - cam.x);
        float minY = (float) (box.minY - cam.y);
        float minZ = (float) (box.minZ - cam.z);
        float maxX = (float) (box.maxX - cam.x);
        float maxY = (float) (box.maxY - cam.y);
        float maxZ = (float) (box.maxZ - cam.z);

        RenderPipeline pipeline = Pipelines.GLOBAL_QUADS_PIPELINE;
        BufferBuilder bufferBuilder = new BufferBuilder(
                Drawer.allocator(), pipeline.getPrimitiveTopology(), pipeline.getVertexFormatBinding(0));
        PoseStack.Pose pose = stack.last();

        bufferBuilder.addVertex(pose, minX, minY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, minY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, minY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, minX, minY, maxZ).setColor(c.getRGB());

        bufferBuilder.addVertex(pose, minX, maxY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, minX, maxY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, maxY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, maxY, minZ).setColor(c.getRGB());

        bufferBuilder.addVertex(pose, minX, minY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, minX, maxY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, maxY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, minY, minZ).setColor(c.getRGB());

        bufferBuilder.addVertex(pose, maxX, minY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, maxY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, maxY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, minY, maxZ).setColor(c.getRGB());

        bufferBuilder.addVertex(pose, minX, minY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, minY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, maxX, maxY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, minX, maxY, maxZ).setColor(c.getRGB());

        bufferBuilder.addVertex(pose, minX, minY, minZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, minX, minY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, minX, maxY, maxZ).setColor(c.getRGB());
        bufferBuilder.addVertex(pose, minX, maxY, minZ).setColor(c.getRGB());

        Drawer.draw(pipeline, bufferBuilder.buildOrThrow());
    }

    public static void rect(GuiGraphicsExtractor ctx, double x, double y, double x2, double y2, int color) {
        ctx.fill((int) x, (int) y, (int) x2, (int) y2, color);
    }

    public static void drawRectOutline(GuiGraphicsExtractor ctx, int x, int y, int x2, int y2, int color) {
        ctx.outline(x, y, x2 - x, y2 - y, color);
    }

    public static void verticalGradient(GuiGraphicsExtractor ctx, double x, double y, double x2, double y2, Color top, Color bottom) {
        ctx.fillGradient((int) x, (int) y, (int) x2, (int) y2, top.getRGB(), bottom.getRGB());
    }

    public static void horizontalGradient(GuiGraphicsExtractor ctx, double x, double y, double x2, double y2, Color left, Color right) {
        int x0 = (int) x;
        int x1 = (int) x2;
        int yy0 = (int) y;
        int yy1 = (int) y2;
        int width = Math.max(1, x1 - x0);
        for (int i = 0; i < width; i++) {
            float f = width <= 1 ? 0f : i / (float) (width - 1);
            int r = (int) (left.getRed() + (right.getRed() - left.getRed()) * f);
            int g = (int) (left.getGreen() + (right.getGreen() - left.getGreen()) * f);
            int b = (int) (left.getBlue() + (right.getBlue() - left.getBlue()) * f);
            int a = (int) (left.getAlpha() + (right.getAlpha() - left.getAlpha()) * f);
            ctx.fill(x0 + i, yy0, x0 + i + 1, yy1, new Color(r, g, b, a).getRGB());
        }
    }
}