package com.example.pathcleaner;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class PathCleanerMod implements ModInitializer {
    private static final int MAX_BLOCKS = 100_000;

    private static final int[][] OFFSETS = new int[][] {
            { 1, 0, 0 },
            { -1, 0, 0 },
            { 0, 1, 0 },
            { 0, -1, 0 },
            { 0, 0, 1 },
            { 0, 0, -1 }
    };

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register(PathCleanerMod::onUseBlock);
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient()) {
            return ActionResult.PASS;
        }

        if (!player.getStackInHand(hand).isOf(Items.STICK)) {
            return ActionResult.PASS;
        }

        BlockPos startPos = hitResult.getBlockPos();
        BlockState startState = world.getBlockState(startPos);

        if (!startState.isOf(Blocks.DIRT_PATH)) {
            return ActionResult.PASS;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return ActionResult.PASS;
        }

        int convertedBlocks = convertConnectedPathBlocks(world, startPos);
        serverPlayer.sendMessage(Text.literal(convertedBlocks + "個の道ブロックを草ブロックに変換しました"), true);
        return ActionResult.SUCCESS;
    }

    private static int convertConnectedPathBlocks(World world, BlockPos startPos) {
        Queue<BlockPos> queue = new ArrayDeque<>();
        HashSet<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        int convertedBlocks = 0;

        while (!queue.isEmpty() && convertedBlocks < MAX_BLOCKS) {
            BlockPos currentPos = queue.remove();
            BlockState currentState = world.getBlockState(currentPos);

            if (!currentState.isOf(Blocks.DIRT_PATH)) {
                continue;
            }

            world.setBlockState(currentPos, Blocks.GRASS_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
            convertedBlocks++;

            for (int[] offset : OFFSETS) {
                BlockPos nextPos = currentPos.add(offset[0], offset[1], offset[2]);

                if (!visited.add(nextPos)) {
                    continue;
                }

                BlockState nextState = world.getBlockState(nextPos);
                if (nextState.isOf(Blocks.DIRT_PATH)) {
                    queue.add(nextPos);
                }
            }
        }

        return convertedBlocks;
    }
}