package com.kobaj.screen.screenaddons;

import com.kobaj.input.GameInputModifier;
import com.kobaj.level.Level;
import com.kobaj.math.AverageMaker;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;

public class BaseInteractionScreen
{
	//camera zoom
	AverageMaker my_test_average = new AverageMaker(20);
		
	public void onUpdate(double delta, GameInputModifier my_modifier, Level test_level)
	{
		boolean jump_time = false;
		boolean touched = false;
		
		// physics
		Constants.physics.integrate_physics(delta, test_level.player.quad_object);
		for (int i = test_level.object_list.size() - 1; i >= 0; i--)
		{
			boolean temp = Constants.physics.check_collision(test_level.player.quad_object, test_level.object_list.get(i).quad_object, 0);
			if(temp)
				jump_time = true;
		}
		
		// initial touch
		if (my_modifier.getInputType().getLeftXorRight())
		{
			touched = true;
			double move_amount = 0;
			
			if (my_modifier.getInputType().getTouchedRight())
			{
				//if we are on the ground
				if(jump_time)
				{
					if(test_level.player.quad_object.x_vel > 0)
						move_amount += Constants.normal_acceleration;
					else
						move_amount += Constants.normal_reverse_acceleration;
				}
				else
					move_amount += Constants.normal_acceleration;
			}
			
			if(my_modifier.getInputType().getTouchedLeft())
			{
				if(jump_time)
				{
					if(test_level.player.quad_object.x_vel < 0)
						move_amount += -Constants.normal_acceleration;
					else
						move_amount += -Constants.normal_reverse_acceleration;
				}
				else
					move_amount += -Constants.normal_acceleration;
			}
			
			// if in the air, apply a damping.
			if (!jump_time)
				move_amount *= Constants.normal_air_damping;
			
			// add to it
			test_level.player.quad_object.x_acc += move_amount;
		}
		
		// add forces
		// add gravity
		Constants.physics.add_gravity(test_level.player.quad_object);
		
		// add friction
		if (!touched && jump_time) 
		{
			double friction = -Constants.normal_friction * test_level.player.quad_object.x_vel;
			test_level.player.quad_object.x_acc += friction;
		}
		
		// prepare camera
		double x_camera = -test_level.player.quad_object.getXPos();
		double y_camera = -test_level.player.quad_object.getYPos();
		
		// restrict camera movement
		double x_buffer = Constants.ratio * Constants.z_shader_translation;
		
		if (x_camera > test_level.left_shader_limit + x_buffer)
			x_camera = test_level.left_shader_limit + x_buffer;
		else if (x_camera < test_level.right_shader_limit - x_buffer)
			x_camera = test_level.right_shader_limit - x_buffer;
		
		if (y_camera < test_level.top_shader_limit - Constants.z_shader_translation)
			y_camera = test_level.top_shader_limit - Constants.z_shader_translation;
		else if (y_camera > test_level.bottom_shader_limit + Constants.z_shader_translation)
			y_camera = test_level.bottom_shader_limit + Constants.z_shader_translation;
		
		// set camera
		Functions.setCamera(x_camera, y_camera);
		
		//update the camera zoom effect
		double buffer = (float) Functions.linearInterpolate(0, Constants.max_x_velocity, Math.abs(test_level.player.quad_object.x_vel), Constants.min_zoom, Constants.max_zoom);
		Functions.setCameraZ(my_test_average.calculateAverage(buffer));
		
		// jump
		if (my_modifier.getInputType().getPressedJump() && jump_time)
		{
			test_level.player.quad_object.y_vel = Constants.jump_velocity;
			jump_time = false;
		}
		else if(my_modifier.getInputType().getReleasedJump())
			if(test_level.player.quad_object.y_vel > Constants.jump_limiter)
				test_level.player.quad_object.y_vel = Constants.jump_limiter;	
	}
}
