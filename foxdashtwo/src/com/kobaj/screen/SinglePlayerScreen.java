package com.kobaj.screen;

import android.annotation.SuppressLint;
import android.graphics.Color;

import com.kobaj.account_settings.SinglePlayerSave;
import com.kobaj.account_settings.UserSettings;
import com.kobaj.foxdashtwo.GameActivity;
import com.kobaj.foxdashtwo.R;
import com.kobaj.input.EnumKeyCodes;
import com.kobaj.input.GameInputModifier;
import com.kobaj.level.EnumLayerTypes;
import com.kobaj.loader.AsyncSave;
import com.kobaj.loader.FileHandler;
import com.kobaj.loader.GLBitmapReader;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.networking.task.TaskSendScore;
import com.kobaj.networking.task.TaskSendScore.FinishedScoring;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.opengldrawable.Button.TextButton;
import com.kobaj.screen.screenaddons.BaseInteractionPhysics;
import com.kobaj.screen.screenaddons.BaseLoadingScreen;
import com.kobaj.screen.screenaddons.RotationLoadingJig;
import com.kobaj.screen.screenaddons.floatingframe.BaseFloatingFrame;
import com.kobaj.screen.screenaddons.floatingframe.BasePauseScreen;

public class SinglePlayerScreen extends BaseScreen implements FinishedScoring
{
	// handling death
	public enum EnumDeathStages
	{
		alive, kill, fade_to_black, dead, fade_to_color
	};
	
	public EnumDeathStages current_death_stage = EnumDeathStages.alive;
	
	// modification of input
	private GameInputModifier my_modifier;
	
	// test level
	private com.kobaj.level.Level the_level;
	
	// addons
	// private LevelDebugScreen debug_addon;
	private BaseLoadingScreen loading_addon;
	private BaseInteractionPhysics interaction_addon;
	private BasePauseScreen pause_addon;
	
	// wheather to fade and what to fade to.
	public boolean fade_in = true;
	public boolean fade_out = false;
	
	private boolean level_done = false;
	
	private int below_xfps_count = 300; // number of times to be triggered before throwing the popup
	private double below_xfps = 1.0 / 20.0 * 1000.0;
	
	// dealing with score presentation
	private double max_color_time = 2000;
	private double color_time = 0;
	private double game_time = 0;
	private double prev_best = 0;
	private double world_best = -1;
	
	// buttons for the end of the level!
	private TextButton title_screen_button;
	private TextButton replay_button;
	private boolean going_to_titlescreen = false;
	private boolean replay_reset = false;
	
	private String level_name;
	
	private RotationLoadingJig network_loader;
	
	private boolean self_playing = false;
	
	public int loading_amount = 0;
	
	public SinglePlayerScreen()
	{
		// initialize everything
		my_modifier = new GameInputModifier();
		loading_addon = new BaseLoadingScreen();
		interaction_addon = new BaseInteractionPhysics(); // has no quads
	}
	
	// loads the next level and returns true if successful.
	private boolean setNextLevel(String local_level_name)
	{
		// erase the last checkpoint since we are 'starting new';
		SinglePlayerSave.last_checkpoint = null;
		
		if (local_level_name != null)
			local_level_name = local_level_name.trim();
		
		if (local_level_name == null || local_level_name.equals(Constants.empty))
		{
			// change the first level
			the_level = FileHandler.readSerialResource(Constants.resources, R.raw.level_0, com.kobaj.level.Level.class);
			{
				this.level_name = local_level_name = "level_0";
				return true;
			}
		}
		
		// if not then try to load from R
		int level_R = Constants.resources.getIdentifier(local_level_name, "raw", "com.kobaj.foxdashtwo");
		if (level_R != 0)
		{
			the_level = FileHandler.readSerialResource(Constants.resources, level_R, com.kobaj.level.Level.class);
			if (the_level != null)
			{
				if (local_level_name.equals("level_5"))
					the_level.credits_level = true;
				
				if(local_level_name.equals("level_title"))
					self_playing = true;
				
				this.level_name = local_level_name;
				return true;
			}
		}
		
		// first see if it is a physical level on disk
		if (FileHandler.fileExists(local_level_name))
		{
			the_level = FileHandler.readSerialFile(local_level_name, com.kobaj.level.Level.class);
			if (the_level != null)
			{
				// split the path
				String[] paths = local_level_name.split("/");
				this.level_name = paths[paths.length - 1]; // last path is file name
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onLoad()
	{
		this.loading_amount = 0;
		
		// load our addons. Do the loader first
		loading_addon.onInitialize();
		
		loading_amount = 10;
		
		if (kill_early)
			return;
		
		this.setNextLevel(SinglePlayerSave.last_level);
		
		if(kill_early)
			return;
		
		loading_amount = 60;
		
		if (the_level != null)
		{
			the_level.onInitialize();
			
			// testing sounds
			the_level.startMusic(!self_playing);
		}
		else
		{
			TitleScreen crash = new TitleScreen();
			crash.crashed = true;
			GameActivity.mGLView.my_game.onChangeScreen(crash);
		}
		
		if(kill_early)
			return;
		
		// control input and other addons
		my_modifier.onInitialize();
		
		loading_amount = 70;
		
		if(kill_early)
			return;
		
		pause_addon = new BasePauseScreen();
		pause_addon.onInitialize();
		
		pause_addon.base_settings.base_input.adjustment.my_modifier = null;
		pause_addon.base_settings.base_input.adjustment.my_modifier = my_modifier;
		
		// debug_addon = new LevelDebugScreen(the_level, EnumDebugType.events);
		
		loading_amount = 80;
		
		if(kill_early)
			return;
		
		network_loader = new RotationLoadingJig();
		network_loader.onInitialize();
		network_loader.radius = Constants.spinning_jig_radius;
		network_loader.x_pos = Constants.two_third_width_pos - Constants.width_padding;
		network_loader.y_pos = Constants.two_fourth_height_pos - Constants.height_padding;
		
		loading_amount = 90;
		
		if(kill_early)
			return;
		
		// buttons
		title_screen_button = new TextButton(R.string.title_screen, true);
		replay_button = new TextButton(R.string.replay, true);
		
		title_screen_button.onInitialize();
		replay_button.onInitialize();
		BaseFloatingFrame.alignButtonsAlongXAxis(Constants.one_fourth_height_pos, title_screen_button, replay_button);
		
		loading_amount = 100;
		
		if(kill_early)
			return;
		
		GLBitmapReader.isLoaded();
		
		System.gc();
	}
	
	@Override
	public void onUnload()
	{
		my_modifier.onUnInitialize();
		loading_addon.onUnInitialize();
		pause_addon.onUnInitialize();
		
		if (the_level != null)
			the_level.onUnInitialize();
		
		network_loader.onUnInitialize();
		
		title_screen_button.onUnInitialize();
		replay_button.onUnInitialize();
	}
	
	@Override
	public void onUpdate(double delta)
	{
		// that music
		if (!self_playing)
			Constants.music_player.onUpdate();
		
		if (current_state != EnumScreenState.paused)
			onRunningUpdate(delta);
		else if (!pause_addon.onUpdate(delta))
			current_state = EnumScreenState.running;
		
		if (self_playing)
			return;
		
		// go into pause mode by hitting menu, search, back...
		if (Constants.input_manager.getKeyPressed(EnumKeyCodes.back) || //
				Constants.input_manager.getKeyPressed(EnumKeyCodes.menu) || //
				Constants.input_manager.getKeyPressed(EnumKeyCodes.search)) //
		{
			// this is possible because onUpdate is only called when in two states, running or paused
			if (current_state != EnumScreenState.paused)
			{
				pause_addon.reset();
				current_state = EnumScreenState.paused;
			}
			
			// MyGLRender.slowmo = !MyGLRender.slowmo;
		}
		
		// handle low fps situations
		if (delta > this.below_xfps && UserSettings.fbo_divider < 2 && !UserSettings.fbo_warned)
		{
			this.below_xfps_count--;
			if (below_xfps_count == 0)
			{
				UserSettings.fbo_warned = true;
				
				pause_addon.reset();
				pause_addon.low_fps_detected = true;
				current_state = EnumScreenState.paused;
			}
		}
		
		network_loader.onUpdate(delta);
	}
	
	private void onRunningUpdate(double delta)
	{
		// update all our objects and lights and things
		the_level.onUpdate(delta, !level_done);
		
		if (!level_done)
			game_time += delta;
		
		// interaction and player
		if (!level_done && !replay_reset)
			interaction_addon.onUpdate(delta, my_modifier, the_level);
		
		getPlayerPosition();
		
		// regardless we fade in
		if (fade_in)
		{
			fade_in = tween_fade_in.onUpdate(delta);
			if (fade_in == false)
				tween_fade_in.reset();
		}
		
		if (fade_out)
		{
			fade_out = tween_fade_out.onUpdate(delta);
			if (fade_out == false)
			{
				// see if we are going home
				if (going_to_titlescreen)
					GameActivity.mGLView.my_game.onCommitChangeScreen();
				
				tween_fade_out.reset();
			}
		}
		
		// handle death events
		if (this.current_death_stage == EnumDeathStages.alive)
		{
			if (the_level.kill)
				this.current_death_stage = EnumDeathStages.kill;
		}
		else if (this.current_death_stage == EnumDeathStages.kill)
		{
			this.current_death_stage = EnumDeathStages.fade_to_black;
			fade_out = true;
		}
		else if (this.current_death_stage == EnumDeathStages.fade_to_black && fade_out == false)
			this.current_death_stage = EnumDeathStages.dead;
		else if (this.current_death_stage == EnumDeathStages.dead)
		{
			the_level.deadReset();
			this.replay_reset = false;
			this.current_death_stage = EnumDeathStages.fade_to_color;
			fade_in = true;
		}
		else if (this.current_death_stage == EnumDeathStages.fade_to_color && fade_in == false)
			this.current_death_stage = EnumDeathStages.alive;
		
		// debug_addon.onUpdate(delta, the_level);
		
		if (level_done)
		{
			color_time += delta;
			
			if (color_time > this.max_color_time)
				color_time = 0;
			
			// level is done and it finished fadeing
			if (title_screen_button.isReleased())
			{
				fade_out = true;
				this.going_to_titlescreen = true;
			}
			else if (replay_button.isReleased())
			{
				// reset everything.
				level_done = false;
				SinglePlayerSave.last_checkpoint = Constants.empty;
				game_time = 0;
				this.the_level.resetLevel();
				
				// send to start.
				this.current_death_stage = EnumDeathStages.kill;
				System.gc();
			}
		}
	}
	
	private void getPlayerPosition()
	{
		// finally recalculate radius to account for distance between fox and ground
		double distance = Functions.distanceSquared(0, interaction_addon.player_extended.top, 0, interaction_addon.player_shadow_y);
		double multiplier = Functions.linearInterpolate(0, Constants.shadow_height_shader, distance, 1, 0) + .01;
		
		// new calculation using drawn shadow
		the_level.player_fox.player_shadow.setXYPos(the_level.player.quad_object.x_pos_shader, interaction_addon.player_shadow_y, EnumDrawFrom.center);
		the_level.player_fox.player_shadow.setScale(multiplier);
	}
	
	@Override
	public void onDrawObject(EnumLayerTypes... types)
	{
		the_level.onDrawObject(types);
		// debug_addon.onDrawObject();
	}
	
	@Override
	public void onDrawLight()
	{
		the_level.onDrawLight();
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void onDrawConstant()
	{
		the_level.onDrawConstant();
		
		if (level_done)
		{
			// darken things just a bit
			color_overlay.color = Color.argb(100, 0, 0, 0);
			color_overlay.onDrawAmbient(Constants.my_ip_matrix, true);
			
			// draw the scores
			Constants.text.drawText(R.string.time, 0, Constants.three_fourth_height_pos, EnumDrawFrom.center);
			
			Constants.text.drawText(R.string.your_time, Constants.one_third_width_pos + Constants.width_padding, Constants.two_fourth_height_pos + Constants.height_padding, EnumDrawFrom.bottom_right);
			Constants.text.drawText(R.string.best_time, Constants.one_third_width_pos + Constants.width_padding, Constants.two_fourth_height_pos, EnumDrawFrom.bottom_right);
			Constants.text
					.drawText(R.string.world_time, Constants.one_third_width_pos + Constants.width_padding, Constants.two_fourth_height_pos - Constants.height_padding, EnumDrawFrom.bottom_right);
			
			// make it look nice
			int game_color = Color.WHITE;
			int world_color = Color.WHITE;
			
			int change_color = 0;
			if (color_time > max_color_time / 2.0)
				change_color = Functions.linearInterpolateColor(max_color_time / 2.0, max_color_time, color_time, Color.GREEN, Color.WHITE);
			else
				change_color = Functions.linearInterpolateColor(0, max_color_time / 2.0, color_time, Color.WHITE, Color.GREEN);
			
			if (game_time / 1000.0 == prev_best)
			{
				game_color = change_color;
			}
			if (game_time / 1000.0 == world_best)
			{
				world_color = change_color;
			}
			
			// draw level score
			Constants.text.drawDecimalNumber(this.game_time / 1000.0, 4, 3, Constants.two_third_width_pos - Constants.width_padding, Constants.two_fourth_height_pos + Constants.height_padding);
			
			// draw our local score
			Constants.text.drawDecimalNumber(this.prev_best, 4, 3, Constants.two_third_width_pos - Constants.width_padding, Constants.two_fourth_height_pos, game_color);
			
			// draw our world score
			if (Constants.network_activity > 0 || world_best < 0)
				network_loader.onDrawLoading();
			if (this.world_best > 0)
			{
				Constants.text.drawDecimalNumber(this.world_best, 4, 3, Constants.two_third_width_pos - Constants.width_padding, Constants.two_fourth_height_pos - Constants.height_padding,
						world_color);
			}
			
			// draw our buttons
			title_screen_button.onDrawConstant();
			replay_button.onDrawConstant();
		}
		
		// cover everything up
		if (fade_in || fade_out || this.going_to_titlescreen) // yes this is correct
			black_overlay_fade.onDrawAmbient(Constants.my_ip_matrix, true);
		
		if (this.current_death_stage == EnumDeathStages.dead)
		{
			color_overlay.color = Color.BLACK;
			color_overlay.onDrawAmbient(Constants.my_ip_matrix, true);
		}
		
		// draw the controls
		if (current_state == EnumScreenState.paused)
			pause_addon.onDraw();
		else if (!level_done)
		{
			my_modifier.onDraw();
			
			// draw our score
			if(!the_level.credits_level)
			{
				Constants.text.drawText(R.string.small_time, Constants.mini_time_title_pos_x, Constants.mini_time_pos_y, EnumDrawFrom.bottom_right);
				Constants.text.drawDecimalNumber(this.game_time / 1000.0, 3, 2, Constants.mini_time_pos_x, Constants.mini_time_pos_y);
			}
		}
		
	}
	
	@Override
	public void onDrawLoading(double delta)
	{
		// we want all loading screens to look the same, so we use this helper loader thingy :)
		if (loading_addon != null)
			loading_addon.onDrawLoading(delta);
	}
	
	@Override
	public void onPause()
	{
		// only on game screens to we send the system into paused state.
		current_state = EnumScreenState.paused;
	}
	
	@Override
	public void onScreenChange(boolean next_level)
	{
		if (!level_done && !replay_reset)
		{
			level_done = true;
			
			// get the previous
			prev_best = SinglePlayerSave.getPrevBest(level_name);
			
			// get and set world bests here
			TaskSendScore score_sender = new TaskSendScore();
			score_sender.setFinishedScoring(this);
			Constants.accounts.sendScore(level_name, game_time / 1000.0, score_sender);
			
			// save this as the best if its better...
			if (game_time / 1000.0 < prev_best && game_time > 0)
			{
				SinglePlayerSave.saveBest(level_name, game_time / 1000.0);
				prev_best = game_time / 1000.0;
			}
			
			// save everything
			AsyncSave save_xml = new AsyncSave();
			save_xml.execute();
			
			// set the buttons
			if (next_level) // has another level, not title screen
				title_screen_button.label = R.string.next_level;
			else
				title_screen_button.label = R.string.title_screen;
			
			replay_reset = true;
		}
	}
	
	public void onScoreCompleted(double score)
	{
		this.world_best = score;
	}
}
