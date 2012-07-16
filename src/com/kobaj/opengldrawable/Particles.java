package com.kobaj.opengldrawable;

import java.util.ArrayList;

public class Particles
{
	//hells yeah particles
	//this will start off just as a special collection of quads
	//changing shape, size, and color
	//but might evolve into a system using its own shader
	
	//things that can vary.
	//particle color		- extendable array (...) int (-1 means random) is an array of colors that will randomly be chosen for particles.
	//particle radius		- double radius
	//particle start		- x and y start position
	//particle movement		- gravity (falls down), fountain (up then down), orbit (rotate around start point).
	//particle have_lifes	- how many particles to have alive at one time (how many on screen at one time).
	//particle total_lifes	- how many particles total to show (if -1 then infinite, if have_lifes > total_lifes then have_lifes = total_lifes).
	//bloom					- boolean for bloom
	public double life, radius, shader_x, shader_y;
	public int color, x, y, have_lifes, total_lifes; //lives or lifes...
	public boolean is_bloom;
	public ArrayList<Integer> colors;
	
	//movement parameters
	public EnumParticleMovement movement = EnumParticleMovement.none;
	private double spread = 1; //for gravity, fountain, and orbit.
	private double height = 1; //for fountain and orbit
	
	//actual particles
	public ArrayList<Particle> actual_particles;
	
	//screen coordinates
	public Particles(double radius, int x, int y, int have_lifes, int total_lifes, boolean is_bloom, int ... color)
	{
		//this may just be the longest constructor declaration I've ever written, weeee!
		
		//set everything
		this.x = x;
		this.y = y;
		this.shader_x = com.kobaj.math.Functions.screenXToShaderX(x);
		this.shader_y = com.kobaj.math.Functions.screenYToShaderY(y);
		
		this.radius = radius;
		this.have_lifes = have_lifes;
		this.total_lifes = total_lifes;
		this.is_bloom = is_bloom;
		
		actual_particles = new ArrayList<Particle>();
		
		if(have_lifes > total_lifes && total_lifes != -1)
			this.have_lifes = this.total_lifes;
		
		colors = new ArrayList<Integer>();
		for(int selection_color: color)
			colors.add(selection_color);
		
		//begin making the amount of particles currently alive
		for(int i = 0; i < have_lifes; i++)
		{
			actual_particles.add(makeParticle());
		}
	}
	
	private Particle makeParticle()
	{
		//figure out color
		int particle_color = 0;
		if(colors.get(0) == -1)
		{
			int min = 0xFF000000;
			int max = 0xFFFFFFFF;
			
			particle_color = com.kobaj.math.Functions.randomInt(min, max);
		}
		else
		{
			int min = 0;
			int max = colors.size() - 1;
			int index = com.kobaj.math.Functions.randomInt(min, max);
			
			particle_color = colors.get(index);
		}
		
		Particle the_particle = new Particle();
		
		the_particle.actual_quad = new QuadColorShape(radius, particle_color, false);
		the_particle.actual_quad.setPos(com.kobaj.math.Functions.screenXToShaderX(x), com.kobaj.math.Functions.screenYToShaderY(y), com.kobaj.opengldrawable.EnumDrawFrom.center);
		
		//add a bloom particle if requested
		if(is_bloom)
		{
			the_particle.bloom_quad = new QuadColorShape(radius, particle_color, true);
			the_particle.bloom_quad.setPos(com.kobaj.math.Functions.screenXToShaderX(x), com.kobaj.math.Functions.screenYToShaderY(y), com.kobaj.opengldrawable.EnumDrawFrom.center);
		}
	
		return the_particle;
	}
	
	public void onUpdate(double delta)
	{
		//see how we are animating things
		//animated objects
		if(movement == EnumParticleMovement.none || have_lifes == 0)
			return;
		
		for(int i = actual_particles.size()-1; i >= 0; i--) 
		{
			Particle p = actual_particles.get(i);
			
			if(movement == EnumParticleMovement.orbit)
			{
				//TODO figure out orbital movement.
			}
			else
			{
				com.kobaj.math.Constants.physics.apply_physics(delta, p.actual_quad);
				if(p.bloom_quad != null)
					com.kobaj.math.Constants.physics.apply_physics(delta, p.bloom_quad);
			}
			
			//see if any object needs to die 
			//delete it if it does (would be cool to animated it out, fade, shrink, fly off screen, whatever...)
			if(!com.kobaj.math.Functions.onShader(p.actual_quad.get_x_pos(), p.actual_quad.get_y_pos()))
			{
				p.reset(shader_x, shader_y);
				setParticleToMovement(p);
				if(total_lifes > 0)
					total_lifes--;
			}
		}
		
		//and calculated everything.
		if(have_lifes > total_lifes && total_lifes != -1)
			have_lifes = total_lifes;
	}
	
	//the below is not an exact science.
	//I'm experimenting with values nothing is exact (sometimes on purpose, sometimes because of improper calculations)
	//these will be modified +- 10percent to make it more awesome.
	//ie height does not mean exact height, etc.
	//screen coordinates
	public void setMovementToGravity(double spread)
	{
		movement = EnumParticleMovement.gravity;
		this.spread = spread;
		
		//add the left right to each particle
		for(Particle p: actual_particles)
			setParticleToMovement(p);
	}
	
	//screen coordinates
	public void setMovementToFountian(double spread, double height)
	{
		movement = EnumParticleMovement.fountain;
		this.spread = spread;
		this.height = height;
		
		//add the jump to each particle
		//add left and right to each particle
		for(Particle p: actual_particles)
			setParticleToMovement(p);
	}
	
	//screen coordinates
	public void setMovementToOrbit(double radius, double speed)
	{
		movement = EnumParticleMovement.orbit;
		this.spread = radius;
		this.height = speed; //not confusing at all...
		
		for(Particle p: actual_particles)
			setParticleToMovement(p);
	}
	
	//so things are a bit more staggered
	public void advancePhysics()
	{
		
		int loop = 0;
		for(Particle p: actual_particles)
		{
			int loops = 10 * loop;
			double delta = 1.0 / 60.0 * 1000.0;
			
			for(int i = 0 ; i < loops; i++)
			{
				com.kobaj.math.Constants.physics.apply_physics(delta, p.actual_quad);
				if(p.bloom_quad != null)
					com.kobaj.math.Constants.physics.apply_physics(delta, p.bloom_quad);
			}
			
			loop += 1;
			if(loop == 7)
				loop = 0;
		}
	}
	
	private void setParticleToMovement(Particle p)
	{
		if(movement == EnumParticleMovement.none)
			return;
		else if(movement == EnumParticleMovement.fountain)
		{
			p.addLeftRight(spread);
			p.addUpdown(spread);
		}
		else if(movement == EnumParticleMovement.gravity)
		{
			p.addLeftRight(spread);
		}
		else if(movement == EnumParticleMovement.orbit)
		{
			p.addRadiusDegree(spread, height);
		}
	}
}
