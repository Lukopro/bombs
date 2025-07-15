package net.luko.bombs.entity;

import net.luko.bombs.Bombs;
import net.luko.bombs.config.BombsConfig;
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
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if(tickCounter < TICK_INTERVAL) return;
        tickCounter = 0;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for(ServerLevel level : server.getAllLevels()){
            trySpawnInLevel(level);
        }
    }

    private static void trySpawnInLevel(ServerLevel level){
        if(level.dimension() != ServerLevel.OVERWORLD || level.players().isEmpty()) return;

        double spawnChance = BombsConfig.PROSPECTOR_SPAWN_CHANCE.get() * (double)level.players().size();
        if(level.random.nextDouble() > spawnChance) return;

        spawnBigGroupNearPlayer(level);
    }

    private static void spawnBigGroupNearPlayer(ServerLevel level){
        Player randomPlayer = level.players().get(level.random.nextInt(level.players().size()));
        BlockPos playerPos = randomPlayer.blockPosition();
        BlockPos basePos = findSpawnPos(level, playerPos, 64);

        if(basePos == null) return;

        int groupMin = BombsConfig.PROSPECTOR_GROUP_MIN.get();
        int groupMax = BombsConfig.PROSPECTOR_GROUP_MAX.get();
        int groupCount = 0;
        if(groupMin > groupMax){
            Bombs.LOGGER.warn("Prospector group minimum spawn value is greater that the group maximum spawn value.");
            groupCount = groupMin;
        } else {
            groupCount = level.random.nextIntBetweenInclusive(groupMin, groupMax);
        }

        int spawned = 0;
        int attempts = 0;

        while(spawned < groupCount && attempts < groupCount * 10){
            attempts++;

            BlockPos groupPos = basePos.offset(level.random.nextInt(20) - 10, 0, level.random.nextInt(20) - 10);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, groupPos);

            if(isSpawnAreaClear(level, surface.below(), 1.6)){
                spawnSmallGroup(level, surface);
                spawned++;
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
            BlockPos surfaceOffset = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, spawnOffset);
            prospector.moveTo(surfaceOffset.getX() + 0.5,
                    surfaceOffset.getY() + 0.1,
                    surfaceOffset.getZ() + 0.5,
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
            if(!level.isLoaded(pos)){
                tickCounter = TICK_INTERVAL - 20;
                return null;
            }
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
            if(isSpawnAreaClear(level, surface.below(), 1.6)){
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