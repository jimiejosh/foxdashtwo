package com.kobaj.level;

import org.simpleframework.xml.Element;

import android.graphics.Color;

import com.kobaj.foxdashtwo.R;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.RectFExtended;
import com.kobaj.math.android.RectF;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;
import com.kobaj.opengldrawable.Quad.QuadCompressed;
import com.kobaj.opengldrawable.Quad.QuadEmpty;
import com.kobaj.screen.screenaddons.InfiniteJig;

public class LevelObject extends LevelEntityActive
{
	@Element
	public EnumLevelObject this_object;
	
	public final EnumDrawFrom draw_from = EnumDrawFrom.top_left;
	@Element
	public double x_pos; // screen coordinates, note these values don't change/update
	@Element
	public double y_pos; // screen coordinates
	@Element
	public double z_plane; // any value
	@Element
	public double degree;
	@Element
	public String id = "unset"; // identifier.
	
	public double scale = 1.0;
	
	@Element
	public int eid;
	@Element
	public EnumLayerTypes layer;
	@Element
	public double width; // desired width and height
	@Element
	public double height;
	@Element
	public boolean mirror_up_down = false;
	@Element
	public boolean mirror_left_right = false;
	
	// special
	public boolean collide_with_player = false;
	public boolean ignore_coord_map = false;
	
	public Quad quad_object;
	
	// usually read only
	// screen coords
	public double quad_width; // actual width and height
	public double quad_height;
	
	// these dont change,
	public double x_pos_shader;
	public double y_pos_shader;
	
	// these variables (should be in their own class, but thats besides the point)
	// are relative to the physics objects
	public RectFExtended y_water_drop_path;
	
	// having a checkpoint
	public InfiniteJig my_checkpoint = null;
	
	// helps with our "quadtree"
	public int sort_index = -1;
	
	// moveing objects only need to be activated once
	private boolean kickstarted = false;
	
	public void onInitialize()
	{
		if (this_object == EnumLevelObject.l1_background_cave_1)
			quad_object = new QuadCompressed(R.raw.l1_background_cave_1, R.raw.l1_background_cave_1_alpha, 1597, 1024);
		else if (this_object == EnumLevelObject.l1_background_cave_2)
			quad_object = new QuadCompressed(R.raw.l1_background_cave_2, R.raw.l1_background_cave_2_alpha, 1471, 1024);
		else if (this_object == EnumLevelObject.l1_ceiling_rocks_1)
			quad_object = new QuadCompressed(R.raw.l1_ceiling_rocks_1, R.raw.l1_ceiling_rocks_1_alpha, 247, 226);
		else if (this_object == EnumLevelObject.l1_ceiling_rocks_2)
			quad_object = new QuadCompressed(R.raw.l1_ceiling_rocks_2, R.raw.l1_ceiling_rocks_2_alpha, 696, 347);
		else if (this_object == EnumLevelObject.l1_ceiling_rocks_3)
			quad_object = new QuadCompressed(R.raw.l1_ceiling_rocks_3, R.raw.l1_ceiling_rocks_3_alpha, 676, 280);
		else if (this_object == EnumLevelObject.l1_decoration_fungus_1)
			quad_object = new QuadCompressed(R.raw.l1_decoration_fungus_1, R.raw.l1_decoration_fungus_1_alpha, 73, 354);
		else if (this_object == EnumLevelObject.l1_decoration_fungus_2)
			quad_object = new QuadCompressed(R.raw.l1_decoration_fungus_2, R.raw.l1_decoration_fungus_2_alpha, 299, 278);
		else if (this_object == EnumLevelObject.l1_decoration_fungus_3)
			quad_object = new QuadCompressed(R.raw.l1_decoration_fungus_3, R.raw.l1_decoration_fungus_3_alpha, 57, 50);
		else if (this_object == EnumLevelObject.l1_decoration_fungus_4)
			quad_object = new QuadCompressed(R.raw.l1_decoration_fungus_4, R.raw.l1_decoration_fungus_4_alpha, 82, 78);
		else if (this_object == EnumLevelObject.l1_decoration_shroom_1)
			quad_object = new QuadCompressed(R.raw.l1_decoration_shroom_1, R.raw.l1_decoration_shroom_1_alpha, 256, 367);
		else if (this_object == EnumLevelObject.l1_decoration_shroom_2)
			quad_object = new QuadCompressed(R.raw.l1_decoration_shroom_2, R.raw.l1_decoration_shroom_2_alpha, 107, 111);
		else if (this_object == EnumLevelObject.l1_decoration_shroom_3)
			quad_object = new QuadCompressed(R.raw.l1_decoration_shroom_3, R.raw.l1_decoration_shroom_3_alpha, 111, 113);
		else if (this_object == EnumLevelObject.l1_decoration_shroom_4)
			quad_object = new QuadCompressed(R.raw.l1_decoration_shroom_4, R.raw.l1_decoration_shroom_4_alpha, 183, 229);
		else if (this_object == EnumLevelObject.l1_decoration_water_2)
			quad_object = new QuadCompressed(R.raw.l1_decoration_water_2, R.raw.l1_decoration_water_2_alpha, 141, 71);
		else if (this_object == EnumLevelObject.l1_decoration_water_3)
			quad_object = new QuadCompressed(R.raw.l1_decoration_water_3, R.raw.l1_decoration_water_3_alpha, 192, 83);
		else if (this_object == EnumLevelObject.l1_ground_platform_1)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_1, R.raw.l1_ground_platform_1_alpha, 749, 301);
		else if (this_object == EnumLevelObject.l1_ground_platform_2)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_2, R.raw.l1_ground_platform_2_alpha, 679, 366);
		else if (this_object == EnumLevelObject.l1_ground_platform_3)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_3, R.raw.l1_ground_platform_3_alpha, 600, 359);
		else if (this_object == EnumLevelObject.l1_ground_platform_4)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_4, R.raw.l1_ground_platform_4_alpha, 385, 220);
		else if (this_object == EnumLevelObject.l1_ground_platform_5)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_5, R.raw.l1_ground_platform_5_alpha, 254, 223);
		else if (this_object == EnumLevelObject.l1_ground_platform_6)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_6, R.raw.l1_ground_platform_6_alpha, 408, 265);
		else if (this_object == EnumLevelObject.l1_ground_platform_connector)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_connector, R.raw.l1_ground_platform_connector_alpha, 114, 65);
		else if (this_object == EnumLevelObject.l1_ground_platform_small_1)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_small_1, R.raw.l1_ground_platform_small_1_alpha, 469, 181);
		else if (this_object == EnumLevelObject.l1_ground_platform_small_2)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_small_2, R.raw.l1_ground_platform_small_2_alpha, 530, 173);
		else if (this_object == EnumLevelObject.l1_ground_platform_small_3)
			quad_object = new QuadCompressed(R.raw.l1_ground_platform_small_3, R.raw.l1_ground_platform_small_3_alpha, 326, 195);
		
		else if (this_object == EnumLevelObject.l2_background_2)
			quad_object = new QuadCompressed(R.raw.l2_background_2, R.raw.l2_background_2_alpha, 2008, 1024);
		else if (this_object == EnumLevelObject.l2_background_hill_1)
			quad_object = new QuadCompressed(R.raw.l2_background_hill_1, R.raw.l2_background_hill_1_alpha, 547, 260);
		else if (this_object == EnumLevelObject.l2_background_hill_2)
			quad_object = new QuadCompressed(R.raw.l2_background_hill_2, R.raw.l2_background_hill_2_alpha, 519, 283);
		else if (this_object == EnumLevelObject.l2_background_rocks_1)
			quad_object = new QuadCompressed(R.raw.l2_background_rocks_1, R.raw.l2_background_rocks_1_alpha, 592, 264);
		else if (this_object == EnumLevelObject.l2_background_rocks_2)
			quad_object = new QuadCompressed(R.raw.l2_background_rocks_2, R.raw.l2_background_rocks_2_alpha, 652, 226);
		else if (this_object == EnumLevelObject.l2_decoration_accent_grass)
			quad_object = new QuadCompressed(R.raw.l2_decoration_accent_grass, R.raw.l2_decoration_accent_grass_alpha, 130, 84);
		else if (this_object == EnumLevelObject.l2_decoration_dead_grass_1)
			quad_object = new QuadCompressed(R.raw.l2_decoration_dead_grass_1, R.raw.l2_decoration_dead_grass_1_alpha, 438, 198);
		else if (this_object == EnumLevelObject.l2_decoration_dead_grass_2)
			quad_object = new QuadCompressed(R.raw.l2_decoration_dead_grass_2, R.raw.l2_decoration_dead_grass_2_alpha, 314, 334);
		else if (this_object == EnumLevelObject.l2_ground_grassplatform_1)
			quad_object = new QuadCompressed(R.raw.l2_ground_grassplatform_1, R.raw.l2_ground_grassplatform_1_alpha, 425, 151);
		else if (this_object == EnumLevelObject.l2_ground_grassplatform_2)
			quad_object = new QuadCompressed(R.raw.l2_ground_grassplatform_2, R.raw.l2_ground_grassplatform_2_alpha, 400, 229);
		else if (this_object == EnumLevelObject.l2_ground_grassplatform_3)
			quad_object = new QuadCompressed(R.raw.l2_ground_grassplatform_3, R.raw.l2_ground_grassplatform_3_alpha, 400, 229);
		else if (this_object == EnumLevelObject.l2_ground_grassplatform_4)
			quad_object = new QuadCompressed(R.raw.l2_ground_grassplatform_4, R.raw.l2_ground_grassplatform_4_alpha, 683, 242);
		else if (this_object == EnumLevelObject.l2_ground_greenplatform_1)
			quad_object = new QuadCompressed(R.raw.l2_ground_greenplatform_1, R.raw.l2_ground_greenplatform_1_alpha, 205, 77);
		else if (this_object == EnumLevelObject.l2_ground_greenplatform_2)
			quad_object = new QuadCompressed(R.raw.l2_ground_greenplatform_2, R.raw.l2_ground_greenplatform_2_alpha, 323, 69);
		else if (this_object == EnumLevelObject.l2_ground_greenplatform_3)
			quad_object = new QuadCompressed(R.raw.l2_ground_greenplatform_3, R.raw.l2_ground_greenplatform_3_alpha, 280, 72);
		else if (this_object == EnumLevelObject.l2_ground_platform_1)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_1, R.raw.l2_ground_platform_1_alpha, 411, 216);
		else if (this_object == EnumLevelObject.l2_ground_platform_2)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_2, R.raw.l2_ground_platform_2_alpha, 281, 218);
		else if (this_object == EnumLevelObject.l2_ground_platform_3)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_3, R.raw.l2_ground_platform_3_alpha, 437, 231);
		else if (this_object == EnumLevelObject.l2_ground_platform_4)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_4, R.raw.l2_ground_platform_4_alpha, 281, 166);
		else if (this_object == EnumLevelObject.l2_ground_platform_5)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_5, R.raw.l2_ground_platform_5_alpha, 438, 168);
		else if (this_object == EnumLevelObject.l2_ground_platform_6)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_6, R.raw.l2_ground_platform_6_alpha, 411, 167);
		else if (this_object == EnumLevelObject.l2_ground_platform_edge)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_edge, R.raw.l2_ground_platform_edge_alpha, 60, 164);
		else if (this_object == EnumLevelObject.l2_ground_platform_end)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_end, R.raw.l2_ground_platform_end_alpha, 66, 256);
		else if (this_object == EnumLevelObject.l2_ground_platform_medium)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_medium, R.raw.l2_ground_platform_medium_alpha, 350, 255);
		else if (this_object == EnumLevelObject.l2_ground_platform_small_1)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_small_1, R.raw.l2_ground_platform_small_1_alpha, 193, 178);
		else if (this_object == EnumLevelObject.l2_ground_platform_small_2)
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_small_2, R.raw.l2_ground_platform_small_2_alpha, 168, 179);
		else if (this_object == EnumLevelObject.l2_ground_transition_1)
			quad_object = new QuadCompressed(R.raw.l2_ground_transition_1, R.raw.l2_ground_transition_1_alpha, 330, 79);
		
		else if (this_object == EnumLevelObject.l3_background_swamp)
			quad_object = new QuadCompressed(R.raw.l3_background_swamp, R.raw.l3_background_swamp_alpha, 1820, 1024);
		else if (this_object == EnumLevelObject.l3_background_tree_1)
			quad_object = new QuadCompressed(R.raw.l3_background_tree_1, R.raw.l3_background_tree_1_alpha, 751, 791);
		else if (this_object == EnumLevelObject.l3_background_tree_2)
			quad_object = new QuadCompressed(R.raw.l3_background_tree_2, R.raw.l3_background_tree_2_alpha, 895, 856);
		else if (this_object == EnumLevelObject.l3_decoration_bones_hand)
			quad_object = new QuadCompressed(R.raw.l3_decoration_bones_hand, R.raw.l3_decoration_bones_hand_alpha, 88, 54);
		else if (this_object == EnumLevelObject.l3_decoration_bones_skull)
			quad_object = new QuadCompressed(R.raw.l3_decoration_bones_skull, R.raw.l3_decoration_bones_skull_alpha, 88, 76);
		else if (this_object == EnumLevelObject.l3_decoration_bones_smallbone)
			quad_object = new QuadCompressed(R.raw.l3_decoration_bones_smallbone, R.raw.l3_decoration_bones_smallbone_alpha, 184, 49);
		else if (this_object == EnumLevelObject.l3_decoration_bones_totem)
			quad_object = new QuadCompressed(R.raw.l3_decoration_bones_totem, R.raw.l3_decoration_bones_totem_alpha, 164, 373);
		else if (this_object == EnumLevelObject.l3_decoration_bush)
			quad_object = new QuadCompressed(R.raw.l3_decoration_bush, R.raw.l3_decoration_bush_alpha, 652, 293);
		else if (this_object == EnumLevelObject.l3_decoration_sticks)
			quad_object = new QuadCompressed(R.raw.l3_decoration_sticks, R.raw.l3_decoration_sticks_alpha, 133, 148);
		else if (this_object == EnumLevelObject.l3_decoration_tree)
			quad_object = new QuadCompressed(R.raw.l3_decoration_tree, R.raw.l3_decoration_tree_alpha, 662, 574);
		else if (this_object == EnumLevelObject.l3_decoration_tree_front)
			quad_object = new QuadCompressed(R.raw.l3_decoration_tree_front, R.raw.l3_decoration_tree_front_alpha, 580, 643);
		else if (this_object == EnumLevelObject.l3_ground_extra_platform_1)
			quad_object = new QuadCompressed(R.raw.l3_ground_extra_platform_1, R.raw.l3_ground_extra_platform_1_alpha, 94, 159);
		else if (this_object == EnumLevelObject.l3_ground_extra_platform_2)
			quad_object = new QuadCompressed(R.raw.l3_ground_extra_platform_2, R.raw.l3_ground_extra_platform_2_alpha, 786, 190);
		else if (this_object == EnumLevelObject.l3_ground_extra_platform_3)
			quad_object = new QuadCompressed(R.raw.l3_ground_extra_platform_3, R.raw.l3_ground_extra_platform_3_alpha, 782, 179);
		else if (this_object == EnumLevelObject.l3_ground_platform_connector)
			quad_object = new QuadCompressed(R.raw.l3_ground_platform_connector, R.raw.l3_ground_platform_connector_alpha, 138, 95);
		else if (this_object == EnumLevelObject.l3_ground_platform_long)
			quad_object = new QuadCompressed(R.raw.l3_ground_platform_long, R.raw.l3_ground_platform_long_alpha, 690, 210);
		else if (this_object == EnumLevelObject.l3_ground_platform_small)
			quad_object = new QuadCompressed(R.raw.l3_ground_platform_small, R.raw.l3_ground_platform_small_alpha, 313, 328);
		else if (this_object == EnumLevelObject.l3_ground_platform_very_small)
			quad_object = new QuadCompressed(R.raw.l3_ground_platform_very_small, R.raw.l3_ground_platform_very_small_alpha, 111, 70);
		
		else if (this_object == EnumLevelObject.l4_background_sky)
			quad_object = new QuadCompressed(R.raw.l4_background_sky, R.raw.l4_background_sky_alpha, 1962, 1024);
		else if (this_object == EnumLevelObject.l4_background_sky_2)
			quad_object = new QuadCompressed(R.raw.l4_background_sky_2, R.raw.l4_background_sky_2_alpha, 1962, 1024);
		else if (this_object == EnumLevelObject.l4_decoration_rocks_1)
			quad_object = new QuadCompressed(R.raw.l4_decoration_rocks_1, R.raw.l4_decoration_rocks_1_alpha, 405, 196);
		else if (this_object == EnumLevelObject.l4_decoration_rocks_2)
			quad_object = new QuadCompressed(R.raw.l4_decoration_rocks_2, R.raw.l4_decoration_rocks_2_alpha, 482, 340);
		else if (this_object == EnumLevelObject.l4_decoration_rocks_3)
			quad_object = new QuadCompressed(R.raw.l4_decoration_rocks_3, R.raw.l4_decoration_rocks_3_alpha, 481, 669);
		else if (this_object == EnumLevelObject.l4_decoration_rocks_4)
			quad_object = new QuadCompressed(R.raw.l4_decoration_rocks_4, R.raw.l4_decoration_rocks_4_alpha, 354, 296);
		else if (this_object == EnumLevelObject.l4_ground_platform_connector)
			quad_object = new QuadCompressed(R.raw.l4_ground_platform_connector, R.raw.l4_ground_platform_connector_alpha, 219, 352);
		else if (this_object == EnumLevelObject.l4_ground_platform_large_1)
			quad_object = new QuadCompressed(R.raw.l4_ground_platform_large_1, R.raw.l4_ground_platform_large_1_alpha, 715, 367);
		else if (this_object == EnumLevelObject.l4_ground_platform_large_2)
			quad_object = new QuadCompressed(R.raw.l4_ground_platform_large_2, R.raw.l4_ground_platform_large_2_alpha, 629, 343);
		else if (this_object == EnumLevelObject.l4_ground_platform_small)
			quad_object = new QuadCompressed(R.raw.l4_ground_platform_small, R.raw.l4_ground_platform_small_alpha, 265, 144);
		else if (this_object == EnumLevelObject.l4_ground_platform_very_small)
			quad_object = new QuadCompressed(R.raw.l4_ground_platform_very_small, R.raw.l4_ground_platform_very_small_alpha, 208, 106);
		else if (this_object == EnumLevelObject.l4_decoration_mountainside_1)
			quad_object = new QuadCompressed(R.raw.l4_decoration_mountainside_1, R.raw.l4_decoration_mountainside_1_alpha, 874, 1329);
		else if (this_object == EnumLevelObject.l4_decoration_mountainside_2)
			quad_object = new QuadCompressed(R.raw.l4_decoration_mountainside_2, R.raw.l4_decoration_mountainside_2_alpha, 581, 1347);
		else if (this_object == EnumLevelObject.l4_decoration_mountainside_filler)
			quad_object = new QuadCompressed(R.raw.l4_decoration_mountainside_filler, R.raw.l4_decoration_mountainside_filler_alpha, 37, 125);
		else if (this_object == EnumLevelObject.l4_decoration_clouds_1) 
			 quad_object = new QuadCompressed(R.raw.l4_decoration_clouds_1, R.raw.l4_decoration_clouds_1_alpha, 755, 268); 
		else if (this_object == EnumLevelObject.l4_decoration_clouds_2) 
			 quad_object = new QuadCompressed(R.raw.l4_decoration_clouds_2, R.raw.l4_decoration_clouds_2_alpha, 410, 159); 
		
		else if (this_object == EnumLevelObject.l5_background_coast)
			quad_object = new QuadCompressed(R.raw.l5_background_coast, R.raw.l5_background_coast_alpha, 2048, 850);
		else if (this_object == EnumLevelObject.l5_background_sky)
			quad_object = new QuadCompressed(R.raw.l5_background_sky, R.raw.l5_background_sky_alpha, 2048, 467);
		else if (this_object == EnumLevelObject.l5_ground_branch_bridge_left_end)
			quad_object = new QuadCompressed(R.raw.l5_ground_branch_bridge_left_end, R.raw.l5_ground_branch_bridge_left_end_alpha, 221, 149);
		else if (this_object == EnumLevelObject.l5_decoration_left_curl)
			quad_object = new QuadCompressed(R.raw.l5_decoration_left_curl, R.raw.l5_decoration_left_curl_alpha, 84, 235);
		else if (this_object == EnumLevelObject.l5_decoration_right_curl)
			quad_object = new QuadCompressed(R.raw.l5_decoration_right_curl, R.raw.l5_decoration_right_curl_alpha, 69, 148);
		else if (this_object == EnumLevelObject.l5_decoration_right_sag)
			quad_object = new QuadCompressed(R.raw.l5_decoration_right_sag, R.raw.l5_decoration_right_sag_alpha, 171, 154);
		else if (this_object == EnumLevelObject.l5_decoration_right_upright)
			quad_object = new QuadCompressed(R.raw.l5_decoration_right_upright, R.raw.l5_decoration_right_upright_alpha, 207, 205);
		else if (this_object == EnumLevelObject.l5_decoration_smog)
			quad_object = new QuadCompressed(R.raw.l5_decoration_smog, R.raw.l5_decoration_smog_alpha, 703, 205);
		else if (this_object == EnumLevelObject.l5_ground_branch_bridge)
			quad_object = new QuadCompressed(R.raw.l5_ground_branch_bridge, R.raw.l5_ground_branch_bridge_alpha, 437, 100);
		else if (this_object == EnumLevelObject.l5_ground_branch_bridge_connector)
			quad_object = new QuadCompressed(R.raw.l5_ground_branch_bridge_connector, R.raw.l5_ground_branch_bridge_connector_alpha, 193, 124);
		else if (this_object == EnumLevelObject.l5_ground_branch_bridge_right_end)
			quad_object = new QuadCompressed(R.raw.l5_ground_branch_bridge_right_end, R.raw.l5_ground_branch_bridge_right_end_alpha, 308, 144);
		else if (this_object == EnumLevelObject.l5_ground_branch_bridge_support)
			quad_object = new QuadCompressed(R.raw.l5_ground_branch_bridge_support, R.raw.l5_ground_branch_bridge_support_alpha, 513, 172);
		else if (this_object == EnumLevelObject.l5_ground_platform_puddle)
			quad_object = new QuadCompressed(R.raw.l5_ground_platform_puddle, R.raw.l5_ground_platform_puddle_alpha, 609, 583);
		else if (this_object == EnumLevelObject.l5_ground_platform_small)
			quad_object = new QuadCompressed(R.raw.l5_ground_platform_small, R.raw.l5_ground_platform_small_alpha, 645, 222);
		
		else if (this_object == EnumLevelObject.l6_background_canyon)
			quad_object = new QuadCompressed(R.raw.l6_background_canyon, R.raw.l6_background_canyon_alpha, 2048, 861);
		else if (this_object == EnumLevelObject.l6_background_canyon_2)
			quad_object = new QuadCompressed(R.raw.l6_background_canyon_2, R.raw.l6_background_canyon_2_alpha, 2048, 861);
		else if (this_object == EnumLevelObject.l6_background_canyon_cave)
			quad_object = new QuadCompressed(R.raw.l6_background_canyon_cave, R.raw.l6_background_canyon_cave_alpha, 915, 885);
		else if (this_object == EnumLevelObject.l6_background_canyon_crack)
			quad_object = new QuadCompressed(R.raw.l6_background_canyon_crack, R.raw.l6_background_canyon_crack_alpha, 570, 1079);
		else if (this_object == EnumLevelObject.l6_background_clouds)
			quad_object = new QuadCompressed(R.raw.l6_background_clouds, R.raw.l6_background_clouds_alpha, 827, 827);
		else if (this_object == EnumLevelObject.l6_decoration_brush_1)
			quad_object = new QuadCompressed(R.raw.l6_decoration_brush_1, R.raw.l6_decoration_brush_1_alpha, 199, 159);
		else if (this_object == EnumLevelObject.l6_decoration_brush_2)
			quad_object = new QuadCompressed(R.raw.l6_decoration_brush_2, R.raw.l6_decoration_brush_2_alpha, 278, 134);
		else if (this_object == EnumLevelObject.l6_decoration_rock)
			quad_object = new QuadCompressed(R.raw.l6_decoration_rock, R.raw.l6_decoration_rock_alpha, 270, 176);
		else if (this_object == EnumLevelObject.l6_decoration_tree_dead)
			quad_object = new QuadCompressed(R.raw.l6_decoration_tree_dead, R.raw.l6_decoration_tree_dead_alpha, 463, 528);
		else if (this_object == EnumLevelObject.l6_ground_platform_extra_1)
			quad_object = new QuadCompressed(R.raw.l6_ground_platform_extra_1, R.raw.l6_ground_platform_extra_1_alpha, 520, 294);
		else if (this_object == EnumLevelObject.l6_ground_platform_extra_2)
			quad_object = new QuadCompressed(R.raw.l6_ground_platform_extra_2, R.raw.l6_ground_platform_extra_2_alpha, 192, 369);
		else if (this_object == EnumLevelObject.l6_ground_platform_extra_3)
			quad_object = new QuadCompressed(R.raw.l6_ground_platform_extra_3, R.raw.l6_ground_platform_extra_3_alpha, 496, 245);
		else if (this_object == EnumLevelObject.l6_ground_platform_extra_4)
			quad_object = new QuadCompressed(R.raw.l6_ground_platform_extra_4, R.raw.l6_ground_platform_extra_4_alpha, 162, 249);
		else if (this_object == EnumLevelObject.l6_ground_platform_flat)
			quad_object = new QuadCompressed(R.raw.l6_ground_platform_flat, R.raw.l6_ground_platform_flat_alpha, 681, 282);
		else if (this_object == EnumLevelObject.l6_ground_platform_huge)
			quad_object = new QuadCompressed(R.raw.l6_ground_platform_huge, R.raw.l6_ground_platform_huge_alpha, 1883, 852);
		
		// specials
		else if (this_object == EnumLevelObject.l1_decoration_water_1)
		{
			quad_object = new QuadCompressed(R.raw.l1_decoration_water_1, R.raw.l1_decoration_water_1_alpha, 78, 78);
			
			RectF previous = quad_object.phys_rect_list.get(0).main_rect;//
			quad_object.phys_rect_list.add(new RectFExtended(previous.left + Functions.screenWidthToShaderWidth(32), //
					previous.top - Functions.screenHeightToShaderHeight(30),//
					previous.right - Functions.screenWidthToShaderWidth(36),//
					previous.bottom + Functions.screenHeightToShaderHeight(31)));//
			
			quad_object.phys_rect_list.remove(0);
			
			Constants.sound.addSound(R.raw.sound_single_water_drop);
			
			this.ignore_coord_map = true;
		}
		else if (this_object == EnumLevelObject.l2_ground_platform_floating_1)
		{
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_floating_1, R.raw.l2_ground_platform_floating_1_alpha, 391, 198);
			RectF previous = quad_object.phys_rect_list.get(0).main_rect;//
			quad_object.phys_rect_list.add(new RectFExtended(previous.left + Functions.screenWidthToShaderWidth(45), //
					previous.top - Functions.screenHeightToShaderHeight(30),//
					previous.right - Functions.screenWidthToShaderWidth(45),//
					previous.bottom + Functions.screenHeightToShaderHeight(50)));//
			
			quad_object.phys_rect_list.remove(0);
			
			collide_with_player = true;
			this.ignore_coord_map = true;
		}
		else if (this_object == EnumLevelObject.l2_ground_platform_floating_2)
		{
			quad_object = new QuadCompressed(R.raw.l2_ground_platform_floating_2, R.raw.l2_ground_platform_floating_2_alpha, 322, 144);
			RectF previous = quad_object.phys_rect_list.get(0).main_rect;//
			quad_object.phys_rect_list.add(new RectFExtended(previous.left + Functions.screenWidthToShaderWidth(45), //
					previous.top - Functions.screenHeightToShaderHeight(12),//
					previous.right - Functions.screenWidthToShaderWidth(45),//
					previous.bottom + Functions.screenHeightToShaderHeight(50)));//
			
			quad_object.phys_rect_list.remove(0);
			
			collide_with_player = true;
			this.ignore_coord_map = true;
		}
		else if (this_object == EnumLevelObject.l4_ground_platform_floating)
		{
			quad_object = new QuadCompressed(R.raw.l4_ground_platform_floating, R.raw.l4_ground_platform_floating_alpha, 658, 260);
			RectF previous = quad_object.phys_rect_list.get(0).main_rect;//
			quad_object.phys_rect_list.add(new RectFExtended(previous.left + Functions.screenWidthToShaderWidth(45), //
					previous.top - Functions.screenHeightToShaderHeight(60),//
					previous.right - Functions.screenWidthToShaderWidth(45),//
					previous.bottom + Functions.screenHeightToShaderHeight(50)));//
			
			quad_object.phys_rect_list.remove(0);
			
			collide_with_player = true;
			this.ignore_coord_map = true;
		}
		
		/* any level objects */
		else if (this_object == EnumLevelObject.lx_background_fade_1)
			quad_object = new QuadCompressed(R.raw.black, R.raw.lx_background_fade_1_alpha, 512, 512);
		else if (this_object == EnumLevelObject.lx_decoration_spikes_1)
			quad_object = new QuadCompressed(R.raw.lx_decoration_spikes_1, R.raw.lx_decoration_spikes_1_alpha, 81, 320);
		else if (this_object == EnumLevelObject.lx_decoration_spikes_2)
			quad_object = new QuadCompressed(R.raw.lx_decoration_spikes_2, R.raw.lx_decoration_spikes_2_alpha, 89, 337);
		else if (this_object == EnumLevelObject.lx_decoration_spikes_3)
			quad_object = new QuadCompressed(R.raw.lx_decoration_spikes_3, R.raw.lx_decoration_spikes_3_alpha, 86, 337);
		else if (this_object == EnumLevelObject.lx_decoration_spikes_4) 
			quad_object = new QuadCompressed(R.raw.lx_decoration_spikes_4, R.raw.lx_decoration_spikes_4_alpha, 200, 400);
		else if (this_object == EnumLevelObject.lx_decoration_spikes_5) // TODO switch this back to 4
			quad_object = new QuadCompressed(R.raw.lx_decoration_spikes_5, R.raw.lx_decoration_spikes_5_alpha, 200, 400);
		else if (this_object == EnumLevelObject.lx_decoration_spikes_6) // TODO switch this back to 4
			quad_object = new QuadCompressed(R.raw.lx_decoration_spikes_6, R.raw.lx_decoration_spikes_6_alpha, 327, 217);
		
		/* check points */
		else if (this_object == EnumLevelObject.lx_pickup_checkpoint)
		{
			quad_object = new QuadColorShape(45, 0xFF9999FF, true, 0);
			
			my_checkpoint = new InfiniteJig();
			my_checkpoint.onInitialize();
			my_checkpoint.set_vp(true);
			
			collide_with_player = true;
			this.ignore_coord_map = true;
		}
		
		/* everything else */
		else if (this_object == EnumLevelObject.transparent || this_object == EnumLevelObject.invisible_wall)
			quad_object = new QuadEmpty((int) quad_width, (int) quad_height);
		else if (this_object == EnumLevelObject.color)
			quad_object = new QuadCompressed(R.raw.white, R.raw.white, (int) quad_width, (int) quad_height);
		else if (this_object == EnumLevelObject.test)
			quad_object = new QuadColorShape(0, 200, 200, 0, Color.WHITE, 0);
		else if (quad_object == null)
			quad_object = new QuadColorShape(0, 200, 200, 0, Color.RED, 0);
		
		// set these before
		quad_width = quad_object.width;
		quad_height = quad_object.height;
		
		// translate
		translate_object_to_original_position();
		scale = ((double) width) / ((double) quad_width);
		if (scale <= 0)
			scale = 1;
		
		// note how these are set AFTER
		this.x_pos_shader = quad_object.x_pos_shader;
		this.y_pos_shader = quad_object.y_pos_shader;
		
		quad_object.reverseLeftRight(this.mirror_left_right);
		quad_object.reverseUpDown(this.mirror_up_down);
		
		// get things within range
		degree = ((degree % 360.0) + 360.0) % 360.0;
		
		// modifications to the quad
		if (degree != 0)
			quad_object.setRotationZ(degree);
		if (scale != 1)
			quad_object.setScale(scale);
	}
	
	public void onUnInitialize()
	{
		if (my_checkpoint != null)
			my_checkpoint.onUnInitialize();
		
		quad_object.onUnInitialize();
	}
	
	private void translate_object_to_original_position()
	{
		// crazy translation to account for scale discrepencies between editor and game
		double translate_scale_x = (quad_width - width) / 2.0;
		double translate_scale_y = (quad_height - height) / 2.0;
		
		quad_object.setXYPos(Functions.screenXToShaderX(x_pos - translate_scale_x), Functions.screenYToShaderY(y_pos + translate_scale_y), draw_from);
		quad_object.x_acc_shader = 0;
		quad_object.y_acc_shader = 0;
		quad_object.x_vel_shader = 0;
		quad_object.y_vel_shader = 0;
		
		// modification to the checkpoint
		if (my_checkpoint != null)
		{
			my_checkpoint.x_pos = quad_object.x_pos_shader;
			my_checkpoint.y_pos = quad_object.y_pos_shader;
		}
	}
	
	public void onUpdate(double delta)
	{
		if (this_object == EnumLevelObject.l2_ground_platform_floating_1)
		{
			if (!kickstarted)
			{
				kickstarted = true;
				quad_object.x_acc_shader = Constants.floating_move_lr_acc;
			}
			
			Constants.physics.addSpringX(Constants.floating_spring / 32.0, 0, Constants.floating_lr_distance, quad_object.x_pos_shader - x_pos_shader, quad_object);
			Constants.physics.addSpringY(Constants.floating_spring, Constants.floating_damper, 0, quad_object.y_pos_shader - y_pos_shader, quad_object);
			Constants.physics.integratePhysics(delta, quad_object);
		}
		else if (this_object == EnumLevelObject.l2_ground_platform_floating_2 //
				|| this_object == EnumLevelObject.l4_ground_platform_floating)
		{
			Constants.physics.addSpringY(Constants.floating_spring, Constants.floating_damper / 2.0, 0, quad_object.y_pos_shader - y_pos_shader, quad_object);
			Constants.physics.integratePhysics(delta, quad_object);
		}
		else if (this_object == EnumLevelObject.l1_decoration_water_1)
		{
			if (Functions.onShader(this.y_water_drop_path))
			{
				Constants.physics.addGravity(quad_object);
				Constants.physics.integratePhysics(delta, quad_object); // make it drop slower
				if (quad_object.phys_rect_list.get(0).main_rect.bottom < this.y_water_drop_path.main_rect.bottom)
				{
					translate_object_to_original_position();
					Constants.sound.play(R.raw.sound_single_water_drop);
				}
			}
		}
		
		if (my_checkpoint != null)
			my_checkpoint.onUpdate(delta);
	}
	
	public void onDrawObject()
	{
		if (active && (this_object != EnumLevelObject.transparent) && (this_object != EnumLevelObject.invisible_wall))
		{
			quad_object.onDrawAmbient();
			
			if (my_checkpoint != null)
			{
				my_checkpoint.onDrawLoading();
			}
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		boolean sameSame = false;
		
		if (other != null && other instanceof LevelObject)
		{
			sameSame = this.id.equals(((LevelObject) other).id);
		}
		
		return sameSame;
	}
}
