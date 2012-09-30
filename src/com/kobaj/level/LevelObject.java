package com.kobaj.level;

import org.simpleframework.xml.Element;

import android.graphics.Color;

import com.kobaj.math.Constants;
import com.kobaj.opengldrawable.Quad.Quad;
import com.kobaj.opengldrawable.Quad.QuadColorShape;

public class LevelObject
{
	@Element
	public EnumLevelObject this_object;
	@Element
	public com.kobaj.opengldrawable.EnumDrawFrom draw_from;
	@Element
	public double x_pos; //screen coordinates
	@Element
	public double y_pos; //screen coordinates
	@Element
	public double z_plane; //generally 0-10, with 0 being closes to the 'front'
	@Element
	public boolean active;
	@Element
	public int id;
	
	public Quad quad_object;
	
	public void onInitialize()
	{
		//will be changed in the future
		if(this_object == EnumLevelObject.test)
			quad_object = new QuadColorShape(0, 200, 200, 0, Color.RED, 0);
		else
			quad_object = new QuadColorShape(0, 200, 200, 0, Color.GREEN, 0);
		
		quad_object.z_pos -= (z_plane * Constants.z_modifier);
		quad_object.setPos(com.kobaj.math.Functions.screenXToShaderX(x_pos), com.kobaj.math.Functions.screenYToShaderY(y_pos), draw_from);
	}
	
	public void onDrawObject()
	{
		quad_object.onDrawAmbient();
	}
}
