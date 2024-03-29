package com.kobaj.level.LevelEventTypes;

import java.util.ArrayList;

import com.kobaj.level.Level;
import com.kobaj.level.LevelEntityActive;
import com.kobaj.level.LevelObject;
import com.kobaj.level.LevelTypeLight.LevelAmbientLight;

public class LevelEventActive extends LevelEventBase
{
	private boolean old_active = false;
	
	// stored list of objects with same name
	private ArrayList<LevelEntityActive> matching_entity_cache;
	
	public LevelEventActive(EnumLevelEvent type)
	{
		super(type);
	}
	
	@Override
	public void onInitialize(final Level level, final ArrayList<String> id_strings)
	{
		super.onInitialize(level, id_strings);
		
		matching_entity_cache = new ArrayList<LevelEntityActive>();
		
		// we iterate over all possible names in all possible objects and all possible lights
		// and build a fast cache
		for (int e = id_cache.size() - 1; e >= 0; e--)
		{
			String id = id_cache.get(e);
			
			for (int i = object_cache.size() - 1; i >= 0; i--)
			{
				// .get(i) is constant time. But this makes things more readable.
				LevelObject temp = object_cache.get(i);
				
				if (temp.id.equals(id) && !matching_entity_cache.contains(temp))
					matching_entity_cache.add(temp);
			}
			
			for (int i = light_cache.size() - 1; i >= 0; i--)
			{
				LevelAmbientLight temp = light_cache.get(i);
				
				if (temp.id.equals(id) && !matching_entity_cache.contains(temp))
					matching_entity_cache.add(temp);
			}
		}
		
	}
	
	@Override
	public void onUpdate(double delta, boolean active)
	{
		// first ensure this is a change in status
		if (old_active != active)
		{
			for (int i = matching_entity_cache.size() - 1; i >= 0; i--)
			{
				LevelEntityActive entity = matching_entity_cache.get(i);
				if (active)
				{
					if (this_event == EnumLevelEvent.off_active)
						entity.active = false;
					else if (this_event == EnumLevelEvent.on_active)
						entity.active = true;
					else if (this_event == EnumLevelEvent.toggle_active)
						entity.active = !entity.active;
					else if (this_event == EnumLevelEvent.touch_active)
						entity.active = true;
					else if (this_event == EnumLevelEvent.anti_touch_active)
						entity.active = false;
				}
				else if (!active)
				{
					if (this_event == EnumLevelEvent.touch_active)
						entity.active = false;
					else if (this_event == EnumLevelEvent.anti_touch_active)
						entity.active = true;
				}
			}
		}
		
		// finally swap activity
		old_active = active;
	}
}
