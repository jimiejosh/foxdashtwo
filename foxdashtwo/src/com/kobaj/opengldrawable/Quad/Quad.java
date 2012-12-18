package com.kobaj.opengldrawable.Quad;

//a lot of help from
//http://www.learnopengles.com/android-lesson-one-getting-started/
//https://developer.android.com/resources/tutorials/opengl/opengl-es20.html	

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kobaj.loader.GLBitmapReader;
import com.kobaj.loader.GLLoadedTexture;
import com.kobaj.math.Constants;
import com.kobaj.math.Functions;
import com.kobaj.math.RectFExtended;
import com.kobaj.opengldrawable.EnumDrawFrom;
import com.kobaj.openglgraphics.BaseLightShader;

public class Quad
{
	// transformation matrix to convert from object to world space
	private float[] my_model_matrix = new float[16];
	
	// and placed in the exact center of the quad
	public EnumDrawFrom currently_drawn = EnumDrawFrom.center;
	
	// these are in shader coordinates 0 to 1
	// anyway, dont be fooled, these are public to READ but not public to SET
	public double x_pos = 0.0;
	public double y_pos = 0.0;
	public double x_acc = 0.0;
	public double y_acc = 0.0;
	public double x_vel = 0.0;
	public double y_vel = 0.0;
	
	// this is new
	public int color = Color.WHITE;
	
	// z index doesnt have to specially be set.
	// objects will only collide if on the same z index plane.
	// this shouldn't really change much actually.
	public double z_pos = -1.0f;
	
	// physics rectangle. An object can have multiple
	// rectangles so it has better 'resolution' when interacting with other quads
	// phys rect is stored in shader coordinates
	public ArrayList<RectFExtended> phys_rect_list = new ArrayList<RectFExtended>();
	
	// maximul AABB is calculated by the engine
	// used to determine if an object is on screen
	// also helpful in physics
	// stored in shader coordinates
	public RectFExtended best_fit_aabb = new RectFExtended();
	
	// begin by holding these
	// should be read only to outside classes...
	public int width;
	public int height;
	public double shader_width;
	public double shader_height;
	public int square;
	public double scale_value = 1.0;
	public double degree = 0;
	
	// data about the quad
	private float[] my_position_matrix = new float[18];
	protected FloatBuffer my_position;
	protected FloatBuffer my_tex_coord;
	
	// camera
	private float[] my_mvp_matrix = new float[16];
	
	// handle to texture
	protected int my_texture_data_handle = -1;
	protected int texture_resource = -1;
	
	// this is a temporary bitmap that holds onto a bitmap that is passed in
	// just long enough so that it gets loaded onto the gpu
	// then it gets disposed.
	private Bitmap nullify_me;
	
	// constructores
	protected Quad()
	{
		// do nothing. Assume whoever is extending knows what he/she is doing.
	}
	
	public Quad(int texture_resource, int width, int height)
	{
		// load dat texture.
		com.kobaj.loader.GLBitmapReader.loadTextureFromResource(texture_resource, false);
		onCreate(texture_resource, width, height);
	}
	
	public Quad(int texture_resource, Bitmap bmp, int width, int height)
	{
		nullify_me = bmp;
		com.kobaj.loader.GLBitmapReader.loadTextureFromBitmap(texture_resource, bmp);
		onCreate(texture_resource, width, height);
	}
	
	// method that will go and get the texture handle after it has been loaded
	// so that we can draw the texture!
	protected boolean setTextureDataHandle()
	{
		if (my_texture_data_handle != -1)
			return true;
		
		if (texture_resource != -1)
		{
			GLLoadedTexture proposed_handle = GLBitmapReader.loaded_textures.get(texture_resource);
			if (proposed_handle != null)
			{
				if (nullify_me != null)
					nullify_me = null;
				
				my_texture_data_handle = proposed_handle.texture_id;
				return true;
			}
		}
		
		return false;
	}
	
	// actual constructor
	// width and height in screen coordinates 0 - 800
	protected void onCreate(int texture_resource, int width, int height)
	{
		// set our texture resource
		this.texture_resource = texture_resource;
		
		// width height data
		setWidthHeight(width, height);
		
		// texture data
		final int tr_square_x = com.kobaj.math.Functions.nearestPowerOf2(width);
		final int tr_square_y = com.kobaj.math.Functions.nearestPowerOf2(height);
		
		square = Math.max(tr_square_x, tr_square_y);
		
		final float tex_y = (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, height, 0, 1);
		final float tex_x = (float) com.kobaj.math.Functions.linearInterpolateUnclamped(0, square, width, 0, 1);
		
		simpleUpdateTexCoords(tex_x, tex_y);
		
		final float tr_x = (float) (this.shader_width / 2.0);
		final float tr_y = (float) (this.shader_height / 2.0);
		
		// up next setup phys rect list. Just a default. The user can
		// set/add/remove more rectangles as needed.
		if (phys_rect_list.isEmpty())
			phys_rect_list.add(new RectFExtended(-tr_x, tr_y, tr_x, -tr_y));
	}
	
	// methods for calculating stuffs
	protected void simpleUpdateTexCoords(float tex_x, float tex_y)
	{
		complexUpdateTexCoords(0, tex_x, 0, tex_y);
	}
	
	// these are in shader coordinates. start_x, end_x, start_y, end_y
	protected void complexUpdateTexCoords(float one_x, float two_x, float one_y, float two_y)
	{
		// only time I use floats...
		float buffer = -0.005f;
		one_x -= buffer;
		two_x += buffer;
		one_y -= buffer;
		two_y += buffer;
		
		// S, T (or X, Y)
		// Texture coordinate data.
		final float[] cubeTextureCoordinateData = {
				// Front face
				one_x, -one_y, one_x, -two_y, two_x, -one_y, one_x, -two_y, two_x, -two_y, two_x, -one_y };
		
		if (my_tex_coord == null)
			my_tex_coord = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_tex_coord.clear();
		
		my_tex_coord.put(cubeTextureCoordinateData).position(0);
	}
	
	// this is a value between 1.0 and
	public void setScale(double scale_value)
	{
			setWidthHeightRotationScale(width, height, 0, scale_value);
	}
	
	// do note: this doesn't change the physics bounding box.
	// this is in screen size
	public void setWidthHeight(int width, int height)
	{
			setWidthHeightRotationScale(width, height, 0, 1);
	}
	
	// rotate from the center
	public void setRotationZ(double degrees)
	{
			setWidthHeightRotationScale(width, height, degrees, 1);
	}
	
	// Why oh why are you doing this instead of a very simple matrix rotation Jakob?
	// Thats a good question, I very well could use model * view * projection where model is scale * rotation * translation
	// however when working on android devices we have to deal with orthographic projections
	// where the ratio is /not/ one.
	// meaning a model * view * projection with rotation will end up skewed!
	// By doing vertex multiplication with compensated coords, we eliminate the skew!
	
	// width and height are in screen values 0 - 800
	// scale will override width and height if it is not 1.
	public void setWidthHeightRotationScale(int width, int height, double degree, double scale_value)
	{
		// double check all values
		if (scale_value < 0 || scale_value > 1)
			scale_value = 1;
		
		this.degree = degree;
		
		final double old_scale_value = this.scale_value;
		final double scale_factor = (scale_value / old_scale_value);
		this.scale_value = scale_value;
		
		// width and height
		width = (int) (width * scale_factor);
		height = (int) (height * scale_factor);
		
		// then the physics
		for (int i = phys_rect_list.size() - 1; i >= 0; i--)
			phys_rect_list.get(i).setScale(scale_value);
		
		// store these for our bounding rectangle
		this.width = width;
		this.height = height;
		
		// Define points for a cube.
		this.shader_width = Functions.screenWidthToShaderWidth(width);
		this.shader_height = Functions.screenHeightToShaderHeight(height);
		
		// begin rotation data
		final double rads = (float) Math.toRadians(degree);
		final double cos_rads = Math.cos(rads);
		final double sin_rads = Math.sin(rads);
		
		float pos_tr_x = width / 2.0f;
		float pos_tr_y = height / 2.0f;
		
		float neg_tr_x = -pos_tr_x;
		float neg_tr_y = -pos_tr_y;
		
		final float z_buffer = 0.0f;
		
		// X, Y, Z
		my_position_matrix[0] = neg_tr_x;
		my_position_matrix[1] = pos_tr_y;
		my_position_matrix[2] = z_buffer;
		my_position_matrix[3] = neg_tr_x;
		my_position_matrix[4] = neg_tr_y;
		my_position_matrix[5] = z_buffer;
		my_position_matrix[6] = pos_tr_x;
		my_position_matrix[7] = pos_tr_y;
		my_position_matrix[8] = z_buffer;
		my_position_matrix[9] = neg_tr_x;
		my_position_matrix[10] = neg_tr_y;
		my_position_matrix[11] = z_buffer;
		my_position_matrix[12] = pos_tr_x;
		my_position_matrix[13] = neg_tr_y;
		my_position_matrix[14] = z_buffer;
		my_position_matrix[15] = pos_tr_x;
		my_position_matrix[16] = pos_tr_y;
		my_position_matrix[17] = z_buffer;
		
		double x_maximul = Double.MIN_VALUE;
		double y_maximul = Double.MIN_VALUE;
		double x_minimul = Double.MAX_VALUE;
		double y_minimul = Double.MAX_VALUE;
		
		// rotate and convert
		for (int i = 0; i < 18; i = i + 3)
		{
			// get coordinate
			final double tr_x1 = my_position_matrix[i];
			final double tr_y1 = my_position_matrix[i + 1];
			
			// apply transforms
			final double tr_x2 = Functions.screenWidthToShaderWidth(tr_x1 * cos_rads - tr_y1 * sin_rads);
			final double tr_y2 = Functions.screenHeightToShaderHeight(tr_y1 * cos_rads + tr_x1 * sin_rads);
			
			// set value
			my_position_matrix[i] = (float) tr_x2;
			my_position_matrix[i + 1] = (float) tr_y2;
			
			// calculate max and min
			if (tr_x2 > x_maximul)
				x_maximul = tr_x2;
			if (tr_x2 < x_minimul)
				x_minimul = tr_x2;
			if (tr_y2 > y_maximul)
				y_maximul = tr_y2;
			if (tr_y2 < y_minimul)
				y_minimul = tr_y2;
		}
		
		// set our maximul aabb
		best_fit_aabb.setExtendedRectF(x_minimul, y_maximul, x_maximul, y_minimul);
		best_fit_aabb.setPositionWithOffset(x_pos, y_pos);
		
		// Initialize the buffers. and store the new coords
		if (my_position == null)
			my_position = ByteBuffer.allocateDirect(my_position_matrix.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		else
			my_position.clear();
		
		my_position.put(my_position_matrix).position(0);
	}
	
	public void setZPos(double z)
	{
		this.z_pos = z;
		update_position_matrix();
	}
	
	// these x and y are in shader space 0 to 1
	public void setXYPos(double x, double y, EnumDrawFrom where)
	{
		currently_drawn = where;
		
		if (where == EnumDrawFrom.top_left)
		{
			// positive x
			// negative y
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;
		}
		else if (where == EnumDrawFrom.top_right)
		{
			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y - shader_height / 2.0;
		}
		else if (where == EnumDrawFrom.bottom_left)
		{
			this.x_pos = x + shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;
		}
		else if (where == EnumDrawFrom.bottom_right)
		{
			
			this.x_pos = x - shader_width / 2.0;
			this.y_pos = y + shader_height / 2.0;
		}
		else
		{
			x_pos = x;
			y_pos = y;
		}
		
		update_position_matrix();
		
		// set the rectangle
		best_fit_aabb.setPositionWithOffset(x_pos, y_pos);
		for (int i = phys_rect_list.size() - 1; i >= 0; i--)
			phys_rect_list.get(i).setPositionWithOffset(x_pos, y_pos);
	}
	
	private boolean position_set = false;
	private void update_position_matrix()
	{
		// set the quad up
		Matrix.setIdentityM(my_model_matrix, 0);
		Matrix.translateM(my_model_matrix, 0, (float) x_pos, (float) y_pos, (float) z_pos);
	
		//I'm not sure why the game doesn't work without this...
		position_set = true;
	}
	
	// methods for
	// drawing stuffs
	protected <T extends BaseLightShader> void onSetupAmbient(float[] my_vp_matrix, T ambient_light)
	{
		if(!position_set)
			update_position_matrix();
		
		// setup the program
		GLES20.glUseProgram(ambient_light.my_shader);
		
		// quick attempt at optimization
		// this is white
		float red = 1;
		float green = 1;
		float blue = 1;
		float alpha = 1;
		
		if (color == Color.BLACK)
		{
			red = 0;
			green = 0;
			blue = 0;
			alpha = 0;
		}
		else if (color != Color.WHITE)
		{
			red = (float) Functions.byteToShader(Functions.red(color));
			green = (float) Functions.byteToShader(Functions.green(color));
			blue = (float) Functions.byteToShader(Functions.blue(color));
			alpha = (float) Functions.byteToShader(Functions.alpha(color));
		}
		
		// pass in color
		GLES20.glUniform4f(ambient_light.my_color_handle, red, green, blue, alpha);
		
		// Set the active texture unit to texture unit 0 and bind necissary handles
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, my_texture_data_handle);
		GLES20.glUniform1i(ambient_light.my_texture_uniform_handle, 0);
		
		// pass in position information
		GLES20.glVertexAttribPointer(ambient_light.my_position_handle, 3, GLES20.GL_FLOAT, false, 0, my_position);
		
		// Pass in the texture coordinate information
		GLES20.glVertexAttribPointer(ambient_light.my_tex_coord_handle, 2, GLES20.GL_FLOAT, false, 0, my_tex_coord);
		
		// multiplies the vp matrix with the model matrix
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(my_mvp_matrix, 0, my_vp_matrix, 0, my_model_matrix, 0);
		
		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(ambient_light.my_mvp_matrix_handle, 1, false, my_mvp_matrix, 0);
		
		// Clear the currently bound buffer (so future OpenGL calls do not use
		// this buffer).
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
	
	// main stuffs
	protected void onDraw()
	{
		Constants.quads_drawn_screen++;
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}
	
	// ouside calls
	public void onDrawAmbient()
	{
		onDrawAmbient(Constants.my_vp_matrix, false);
	}
	
	public void onDrawAmbient(float[] my_vp_matrix, boolean skip_draw_check)
	{
		// if we have a handle, draw.
		if (!setTextureDataHandle())
			return;
		
		// If on screen, draw.
		if (skip_draw_check || com.kobaj.math.Functions.onShader(best_fit_aabb))
		{
			onSetupAmbient(my_vp_matrix, Constants.ambient_light);
			
			// Draw the cube.
			onDraw();
		}
	}
}
