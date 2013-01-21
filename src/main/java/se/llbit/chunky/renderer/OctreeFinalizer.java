/* Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.llbit.chunky.renderer;

import se.llbit.chunky.model.WaterModel;
import se.llbit.chunky.world.Block;
import se.llbit.chunky.world.BlockData;
import se.llbit.chunky.world.Chunk;
import se.llbit.chunky.world.ChunkPosition;
import se.llbit.math.Octree;
import se.llbit.math.Vector3i;

/**
 * @author Jesper Öqvist <jesper@llbit.se>
 */
public class OctreeFinalizer {
	/**
	 * Finalize a chunk in the octree.
	 * @param octree Octree to finalize
	 * @param origin Origin of the octree
	 * @param cp Position of the chunk to finalize
	 */
	public static void finalizeChunk(Octree octree, Vector3i origin, ChunkPosition cp) {
		for (int cy = 0-origin.y; cy < Chunk.Y_MAX-origin.y; ++cy) {
			for (int cz = 0; cz < 16; ++cz) {
				int z = cz + cp.z*16 - origin.z;
				for (int cx = 0; cx < 16; ++cx) {
					int x = cx + cp.x*16 - origin.x;
					int type = octree.get(x, cy, z);
					Block block = Block.values[type & 0xFF];
					
					// set non-visible blocks to be stone, in order to merge large patches
					if ((cx == 0 || cx == 15 || cz == 0 || cz == 15) &&
							cy > -origin.y && cy < Chunk.Y_MAX-origin.y-1 &&
							block != Block.STONE && block.isOpaque) {
						if (Block.values[0xFF & octree.get(x-1, cy, z)].isOpaque &&
								Block.values[0xFF & octree.get(x+1, cy, z)].isOpaque &&
								Block.values[0xFF & octree.get(x, cy-1, z)].isOpaque &&
								Block.values[0xFF & octree.get(x, cy+1, z)].isOpaque &&
								Block.values[0xFF & octree.get(x, cy, z-1)].isOpaque &&
								Block.values[0xFF & octree.get(x, cy, z+1)].isOpaque) {
							octree.set(Block.STONE.id, x, cy, z);
							continue;
						}
					}
					
					int fullBlock;
					int data;
					int level0;
					int level;
					int corner0;
					int corner1;
					int corner2;
					int corner3;
					
					Block other;
					switch (block.id) {
					case Block.WATER_ID:
						fullBlock = (type >> WaterModel.FULL_BLOCK) & 1;
						if (fullBlock != 0) break;
						
						level0 = 8 - (0xF & (type >> 8));
						corner0 = level0;
						corner1 = level0;
						corner2 = level0;
						corner3 = level0;
						
						data = octree.get(x-1, cy, z);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner3 += level;
						corner0 += level;
						
						data = octree.get(x-1, cy, z+1);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner0 += level;
						
						data = octree.get(x, cy, z+1);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner0 += level;
						corner1 += level;
						
						data = octree.get(x+1, cy, z+1);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner1 += level;
						
						data = octree.get(x+1, cy, z);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner1 += level;
						corner2 += level;
						
						data = octree.get(x+1, cy, z-1);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner2 += level;
						
						data = octree.get(x, cy, z-1);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner2 += level;
						corner3 += level;
						
						data = octree.get(x-1, cy, z-1);
						level = level0;
						if (Block.values[0xFF & data] == Block.WATER) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner3 += level;
						
						corner0 = Math.min(7, 8 - (corner0 / 4));
						corner1 = Math.min(7, 8 - (corner1 / 4));
						corner2 = Math.min(7, 8 - (corner2 / 4));
						corner3 = Math.min(7, 8 - (corner3 / 4));
						type |= (corner0 << 16);
						type |= (corner1 << 20);
						type |= (corner2 << 24);
						type |= (corner3 << 28);
						octree.set(type, x, cy, z);
						break;
					case Block.LAVA_ID:
						fullBlock = (type >> WaterModel.FULL_BLOCK) & 1;
						if (fullBlock != 0) break;
						
						level0 = 8 - (0xF & (type >> 8));
						corner0 = level0;
						corner1 = level0;
						corner2 = level0;
						corner3 = level0;
						
						data = octree.get(x-1, cy, z);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner3 += level;
						corner0 += level;
						
						data = octree.get(x-1, cy, z+1);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner0 += level;
						
						data = octree.get(x, cy, z+1);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner0 += level;
						corner1 += level;
						
						data = octree.get(x+1, cy, z+1);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner1 += level;
						
						data = octree.get(x+1, cy, z);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner1 += level;
						corner2 += level;
						
						data = octree.get(x+1, cy, z-1);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner2 += level;
						
						data = octree.get(x, cy, z-1);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner2 += level;
						corner3 += level;
						
						data = octree.get(x-1, cy, z-1);
						level = level0;
						if (Block.values[0xFF & data] == Block.LAVA) {
							fullBlock = (data >> WaterModel.FULL_BLOCK) & 1;
							level = 8 - (1-fullBlock) * (7 & (data >> 8));
						} else if (!Block.values[0xFF & data].isSolid) {
							level = 0;
						}
						corner3 += level;
						
						corner0 = Math.min(7, 8 - (corner0 / 4));
						corner1 = Math.min(7, 8 - (corner1 / 4));
						corner2 = Math.min(7, 8 - (corner2 / 4));
						corner3 = Math.min(7, 8 - (corner3 / 4));
						type |= (corner0 << 16);
						type |= (corner1 << 20);
						type |= (corner2 << 24);
						type |= (corner3 << 28);
						octree.set(type, x, cy, z);
						break;
					case Block.TRIPWIRE_ID:
						other = Block.values[0xFF & octree.get(x - 1, cy, z)];
						if (other == Block.TRIPWIRE || other == Block.TRIPWIREHOOK) {
							type |= 1 << 12;
						} else {
							other = Block.values[0xFF & octree.get(x + 1, cy, z)];
							if (other == Block.TRIPWIRE || other == Block.TRIPWIREHOOK) {
								type |= 1 << 12;
							}
						}
						octree.set(type, x, cy, z);
						break;
					case Block.REDSTONEWIRE_ID:
						Block above = Block.values[0xFF & octree.get(x, cy + 1, z)];
						Block west = Block.values[0xFF & octree.get(x - 1, cy, z)];
						Block east = Block.values[0xFF & octree.get(x + 1, cy, z)];
						Block north = Block.values[0xFF & octree.get(x, cy, z - 1)];
						Block south = Block.values[0xFF & octree.get(x, cy, z + 1)];
						
						if (above == Block.AIR) {
							Block westAbove = Block.values[0xFF & octree.get(x - 1, cy + 1, z)];
							if (west.isSolid && westAbove == Block.REDSTONEWIRE) {
								// wire on west block side
								type |= 1 << BlockData.RSW_WEST_CONNECTION;
								type |= 1 << BlockData.RSW_WEST_SIDE;
							}
							Block eastAbove = Block.values[0xFF & octree.get(x + 1, cy + 1, z)];
							if (east.isSolid && eastAbove == Block.REDSTONEWIRE) {
								// wire on east block side
								type |= 1 << BlockData.RSW_EAST_CONNECTION;
								type |= 1 << BlockData.RSW_EAST_SIDE;
							}
							Block northAbove = Block.values[0xFF & octree.get(x, cy + 1, z - 1)];
							if (north.isSolid && northAbove == Block.REDSTONEWIRE) {
								// wire on north block side
								type |= 1 << BlockData.RSW_NORTH_CONNECTION;
								type |= 1 << BlockData.RSW_NORTH_SIDE;
							}
							Block southAbove = Block.values[0xFF & octree.get(x, cy + 1, z + 1)];
							if (south.isSolid && southAbove == Block.REDSTONEWIRE) {
								// wire on south block side
								type |= 1 << BlockData.RSW_SOUTH_CONNECTION;
								type |= 1 << BlockData.RSW_SOUTH_SIDE;
							}
						}
						
						if (west.isRedstoneWireConnector()) {
							type |= 1 << BlockData.RSW_WEST_CONNECTION;
						} else if (west == Block.AIR) {
							Block westBelow = Block.values[0xFF & octree.get(x - 1, cy - 1, z)];
							if (westBelow == Block.REDSTONEWIRE) {
								type |= 1 << BlockData.RSW_WEST_CONNECTION;
							}
						}
						
						if (east.isRedstoneWireConnector()) {
							type |= 1 << BlockData.RSW_EAST_CONNECTION;
						} else if (east == Block.AIR) {
							Block eastBelow = Block.values[0xFF & octree.get(x + 1, cy - 1, z)];
							if (eastBelow == Block.REDSTONEWIRE) {
								type |= 1 << BlockData.RSW_EAST_CONNECTION;
							}
						}
						
						if (north.isRedstoneWireConnector() || south.isRedstoneWireConnector()) {
							type |= 1 << BlockData.RSW_NORTH_CONNECTION;
						} else if (north == Block.AIR) {
							Block northBelow = Block.values[0xFF & octree.get(x, cy - 1, z - 1)];
							if (northBelow == Block.REDSTONEWIRE) {
								type |= 1 << BlockData.RSW_NORTH_CONNECTION;
							}
						}
						
						if (south.isRedstoneWireConnector()) {
							type |= 1 << BlockData.RSW_SOUTH_CONNECTION;
						} else if (south == Block.AIR) {
							Block southBelow = Block.values[0xFF & octree.get(x, cy - 1, z + 1)];
							if (southBelow == Block.REDSTONEWIRE) {
								type |= 1 << BlockData.RSW_SOUTH_CONNECTION;
							}
						}
						
						octree.set(type, x, cy, z);
						break;
					case Block.MELONSTEM_ID:
						if (Block.values[0xFF & octree.get(x - 1, cy, z)] == Block.MELON)
							type |= 1 << 16;
						else if (Block.values[0xFF & octree.get(x + 1, cy, z)] == Block.MELON)
							type |= 2 << 16;
						else if (Block.values[0xFF & octree.get(x, cy, z - 1)] == Block.MELON)
							type |= 3 << 16;
						else if (Block.values[0xFF & octree.get(x, cy, z + 1)] == Block.MELON)
							type |= 4 << 16;
						octree.set(type, x, cy, z);
						break;
					case Block.PUMPKINSTEM_ID:
						if (Block.values[0xFF & octree.get(x - 1, cy, z)] == Block.PUMPKIN)
							type |= 1 << 16;
						else if (Block.values[0xFF & octree.get(x + 1, cy, z)] == Block.PUMPKIN)
							type |= 2 << 16;
						else if (Block.values[0xFF & octree.get(x, cy, z - 1)] == Block.PUMPKIN)
							type |= 3 << 16;
						else if (Block.values[0xFF & octree.get(x, cy, z + 1)] == Block.PUMPKIN)
							type |= 4 << 16;
						octree.set(type, x, cy, z);
						break;
					case Block.CHEST_ID:
						int dir = type >> 8;
						int tex = 0;
						if (dir < 4) {
							if (Block.values[0xFF & octree.get(x - 1, cy, z)] == Block.CHEST)
								tex = 1 + (dir-1) % 2;
							else if (Block.values[0xFF & octree.get(x + 1, cy, z)] == Block.CHEST)
								tex = 1 + dir % 2;
						} else {
							if (Block.values[0xFF & octree.get(x, cy, z - 1)] == Block.CHEST)
								tex = 1 + dir % 2;
							else if (Block.values[0xFF & octree.get(x, cy, z + 1)] == Block.CHEST)
								tex = 1 + (dir-1) % 2;
						}
						type |= tex << 16;
						octree.set(type, x, cy, z);
						break;
					case Block.IRONBARS_ID:
						other = Block.values[0xFF & octree.get(x, cy, z - 1)];
						if (other.isIronBarsConnector())
							type |= 1 << 8;
						other = Block.values[0xFF & octree.get(x, cy, z + 1)];
						if (other.isIronBarsConnector())
							type |= 2 << 8;
						other = Block.values[0xFF & octree.get(x + 1, cy, z)];
						if (other.isIronBarsConnector())
							type |= 4 << 8;
						other = Block.values[0xFF & octree.get(x - 1, cy, z)];
						if (other.isIronBarsConnector())
							type |= 8 << 8;
						octree.set(type, x, cy, z);
						break;
					case Block.GLASSPANE_ID:
						other = Block.values[0xFF & octree.get(x, cy, z - 1)];
						if (other.isGlassPaneConnector())
							type |= 1 << 8;
						other = Block.values[0xFF & octree.get(x, cy, z + 1)];
						if (other.isGlassPaneConnector())
							type |= 2 << 8;
						other = Block.values[0xFF & octree.get(x + 1, cy, z)];
						if (other.isGlassPaneConnector())
							type |= 4 << 8;
						other = Block.values[0xFF & octree.get(x - 1, cy, z)];
						if (other.isGlassPaneConnector())
							type |= 8 << 8;
						octree.set(type, x, cy, z);
						break;
					case Block.STONEWALL_ID:
						other = Block.values[0xFF & octree.get(x, cy, z - 1)];
						if (other.isStoneWallConnector())
							type |= 1 << BlockData.STONEWALL_CONN;
						other = Block.values[0xFF & octree.get(x, cy, z + 1)];
						if (other.isStoneWallConnector())
							type |= 2 << BlockData.STONEWALL_CONN;
						other = Block.values[0xFF & octree.get(x + 1, cy, z)];
						if (other.isStoneWallConnector())
							type |= 4 << BlockData.STONEWALL_CONN;
						other = Block.values[0xFF & octree.get(x - 1, cy, z)];
						if (other.isStoneWallConnector())
							type |= 8 << BlockData.STONEWALL_CONN;
						octree.set(type, x, cy, z);
						break;
					case Block.FENCE_ID:
						other = Block.values[0xFF & octree.get(x, cy, z - 1)];
						if (other.isFenceConnector())
							type |= 1 << 8;
						other = Block.values[0xFF & octree.get(x, cy, z + 1)];
						if (other.isFenceConnector())
							type |= 2 << 8;
						other = Block.values[0xFF & octree.get(x + 1, cy, z)];
						if (other.isFenceConnector())
							type |= 4 << 8;
						other = Block.values[0xFF & octree.get(x - 1, cy, z)];
						if (other.isFenceConnector())
							type |= 8 << 8;
						octree.set(type, x, cy, z);
						break;
					case Block.NETHERBRICKFENCE_ID:
						other = Block.values[0xFF & octree.get(x, cy, z - 1)];
						if (other.isNetherBrickFenceConnector())
							type |= 1 << 8;
						other = Block.values[0xFF & octree.get(x, cy, z + 1)];
						if (other.isNetherBrickFenceConnector())
							type |= 2 << 8;
						other = Block.values[0xFF & octree.get(x + 1, cy, z)];
						if (other.isNetherBrickFenceConnector())
							type |= 4 << 8;
						other = Block.values[0xFF & octree.get(x - 1, cy, z)];
						if (other.isNetherBrickFenceConnector())
							type |= 8 << 8;
						octree.set(type, x, cy, z);
						break;
					case Block.OAKWOODSTAIRS_ID:
					case Block.STONESTAIRS_ID:
					case Block.BRICKSTAIRS_ID:
					case Block.STONEBRICKSTAIRS_ID:
					case Block.NETHERBRICKSTAIRS_ID:
					case Block.SANDSTONESTAIRS_ID:
					case Block.SPRUCEWOODSTAIRS_ID:
					case Block.BIRCHWOODSTAIRS_ID:
					case Block.JUNGLEWOODSTAIRS_ID:
						// check if this is a corner stair block
						int rotation = 3 & (type >> BlockData.BLOCK_DATA_OFFSET);
						int bd;
						Block behind;
						switch (rotation) {
						case 0:
							// ascending east
							bd = octree.get(x+1, cy, z);
							behind = Block.get(bd);
							if (behind.isStair()) {
								switch (3 & (bd >> BlockData.BLOCK_DATA_OFFSET)) {
								case 2:
									// if behind ascends south we have s-e corner
									type |= BlockData.SOUTH_EAST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								case 3:
									// if behind ascends north we have n-e corner
									type |= BlockData.NORTH_EAST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								}
							}
							break;
						case 1:
							// ascending west
							bd = octree.get(x-1, cy, z);
							behind = Block.get(bd);
							if (behind.isStair()) {
								switch (3 & (bd >> BlockData.BLOCK_DATA_OFFSET)) {
								case 2:
									// if behind ascends south we have s-w corner
									type |= BlockData.SOUTH_WEST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								case 3:
									// if behind ascends north we have n-w corner
									type |= BlockData.NORTH_WEST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								}
							}
							break;
						case 2:
							// ascending south
							bd = octree.get(x, cy, z+1);
							behind = Block.get(bd);
							if (behind.isStair()) {
								switch (3 & (bd >> BlockData.BLOCK_DATA_OFFSET)) {
								case 0:
									// if behind ascends east we have s-e corner
									type |= BlockData.SOUTH_EAST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								case 1:
									// if behind ascends west we have s-w corner
									type |= BlockData.SOUTH_WEST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								}
							}
							break;
						case 3:
							// ascending north
							bd = octree.get(x, cy, z-1);
							behind = Block.get(bd);
							if (behind.isStair()) {
								switch (3 & (bd >> BlockData.BLOCK_DATA_OFFSET)) {
								case 0:
									// if behind ascends east we have n-e corner
									type |= BlockData.NORTH_EAST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								case 1:
									// if behind ascends west we have n-w corner
									type |= BlockData.NORTH_WEST << BlockData.CORNER_OFFSET;
									octree.set(type, x, cy, z);
									break;
								}
							}
							break;
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}

}
