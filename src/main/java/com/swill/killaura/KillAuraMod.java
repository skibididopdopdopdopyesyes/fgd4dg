package com.swill.killaura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Random;

public class KillAuraMod implements ClientModInitializer {
    private static boolean enabled = false;
    private static long lastAttack = 0;
    private static final Random RANDOM = new Random();

    @Override
    public void onInitializeClient() {
        KeyBinding key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "KillAura", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "Swill Cheats"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (key.wasPressed()) enabled = !enabled;
            if (!enabled || client.player == null) return;
            Box area = client.player.getBoundingBox().expand(4.5);
            List<LivingEntity> targets = client.world.getEntitiesByClass(LivingEntity.class, area,
                    e -> e != client.player && e.isAlive() && e.getHealth() > 0);
            if (targets.isEmpty()) return;
            LivingEntity target = targets.stream()
                    .min((a, b) -> Double.compare(a.getHealth(), b.getHealth()))
                    .orElse(null);
            if (target == null) return;
            long now = System.currentTimeMillis();
            if (now - lastAttack >= (50 + RANDOM.nextInt(100))) {
                client.interactionManager.attackEntity(client.player, target);
                client.player.swingHand(Hand.MAIN_HAND);
                lastAttack = now;
            }
        });
    }
}
