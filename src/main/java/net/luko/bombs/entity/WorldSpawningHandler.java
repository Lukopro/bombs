package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = Bombs.MODID)
public class WorldSpawningHandler {
    private static final int TICK_INTERVAL = 1200;
    private static final double SPAWN_CHANCE = 0.002;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if(tickCounter < TICK_INTERVAL) return;
        tickCounter = 0;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for(ServerLevel level : server.getAllLevels()){
            //trySpawnInLevel(level);
        }
    }

    private static void trySpawnInLevel(ServerLevel level){
        if(level.dimension() != ServerLevel.OVERWORLD || level.players().isEmpty()) return;

        if(level.random.nextDouble() >= SPAWN_CHANCE * level.players().size()) return;

        spawnBigGroupNearPlayer(level);
    }

    private static void spawnBigGroupNearPlayer(ServerLevel level){
        Player randomPlayer = level.players().get(level.random.nextInt(level.players().size()));
        BlockPos playerPos = randomPlayer.blockPosition();
        BlockPos basePos = findSpawnPos(level, playerPos, 64);

        if(basePos == null) return;

        int groupCount = 2 + level.random.nextInt(2);
        int spawned = 0;
        int attempts = 0;

        while(spawned < groupCount || attempts < groupCount * 10){
            attempts++;

            BlockPos groupPos = basePos.offset(level.random.nextInt(20) - 10, 0, level.random.nextInt(20) - 10);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, groupPos);

            if(isSpawnAreaClear(level, surface.below(), 1.6)){
                spawnSmallGroup(level, groupPos);
            }
        }
    }

    private static void spawnSmallGroup(ServerLevel level, BlockPos pos){
        HonseEntity honse = ModEntities.HONSE.get().create(level);
        if(honse == null) return;
        honse.moveTo(pos.getX() + 0.5,
                pos.getY() + 0.1,
                pos.getZ() + 0.5,
                level.random.nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(honse);

        for(int i = 0; i < 2; i++){
            ProspectorEntity prospector = ModEntities.PROSPECTOR.get().create(level);
            if(prospector == null) return;
            prospector.giveSpawnItems();

            BlockPos spawnOffset = pos.offset(level.random.nextInt(3) - 1, 0, level.random.nextInt(3) - 1);
            prospector.moveTo(spawnOffset.getX() + 0.5,
                    spawnOffset.getY() + 0.1,
                    spawnOffset.getZ() + 0.5,
                    level.random.nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(prospector);

            if(honse.getPassengers().size() < 2){
                prospector.startRiding(honse, true);
            }
        }
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos origin, int minDistance){
        for(int i = 0; i < 10; i++){
            int dx = level.random.nextInt(256) - 128;
            int dz = level.random.nextInt(256) - 128;

            if(Math.abs(dx) < minDistance && Math.abs(dz) < minDistance) continue;

            BlockPos pos = origin.offset(dx, 0, dz);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            if(!isSpawnAreaClear(level, surface.below(), 1.4)){
                return surface;
            }
        }

        return null;
    }

    private static boolean isSpawnAreaClear(ServerLevel level, BlockPos center, double requiredWidth){
        int radius = (int)Math.ceil(requiredWidth / 2.0);
        for(int dx = -radius; dx <= radius; dx++){
            for(int dz = -radius; dz <= radius; dz++){
                BlockPos checkPos = center.offset(dx, 0, dz);
                BlockState state = level.getBlockState(checkPos);
                BlockState above = level.getBlockState(checkPos.above());
                BlockState above2 = level.getBlockState(checkPos.above(2));

                if(!state.isSolidRender(level, checkPos) || !above.isAir() || !above2.isAir()) return false;
            }
        }
        return true;
    }
}
