package pl.twojnick.cheaty12333;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;

public class ESPModule {

    public boolean enabled = false;

    public boolean players = true;
    public boolean mobs = true;
    public boolean items = true;

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public void toggle() {
        enabled = !enabled;
        System.out.println("[Cheaty12333] ESP: " + (enabled ? "ON" : "OFF"));
    }

    public void render(WorldRenderContext context) {
        if (!enabled || mc.world == null || mc.player == null) return;

        MatrixStack matrices = context.matrixStack();
        Vec3d camPos = context.camera().getPos();

        matrices.push();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumerProvider.Immediate vertices = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer buffer = vertices.getBuffer(RenderLayer.getLines());

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (!shouldRender(entity)) continue;

            Color color = getColor(entity);
            drawBox(matrices, buffer, entity, color);
        }

        vertices.draw(); // ważne!
        matrices.pop();
    }

    private boolean shouldRender(Entity e) {
        if (e instanceof PlayerEntity) return players;
        if (e instanceof LivingEntity && !(e instanceof PlayerEntity)) return mobs;
        if (e instanceof ItemEntity) return items;
        return false;
    }

    private Color getColor(Entity e) {
        if (e instanceof PlayerEntity) return Color.RED;
        if (e instanceof LivingEntity) return Color.YELLOW;
        return Color.CYAN;
    }

    // Nowa, działająca metoda rysowania boxa w 1.21.4+
    private void drawBox(MatrixStack matrices, VertexConsumer buffer, Entity entity, Color color) {
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = 0.6f; // przezroczystość

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();

        // Rysujemy 12 krawędzi boxa ręcznie (działa stabilnie w 1.21+)
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Dolna podstawa
        line(buffer, posMatrix, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
        line(buffer, posMatrix, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
        line(buffer, posMatrix, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        line(buffer, posMatrix, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

        // Górna podstawa
        line(buffer, posMatrix, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
        line(buffer, posMatrix, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
        line(buffer, posMatrix, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        line(buffer, posMatrix, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

        // Pionowe krawędzie
        line(buffer, posMatrix, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
        line(buffer, posMatrix, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
        line(buffer, posMatrix, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
        line(buffer, posMatrix, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    private void line(VertexConsumer buffer, Matrix4f matrix, float x1, float y1, float z1,
                      float x2, float y2, float z2, float r, float g, float b, float a) {
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(0, 0, 0).next();
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(0, 0, 0).next();
    }
}