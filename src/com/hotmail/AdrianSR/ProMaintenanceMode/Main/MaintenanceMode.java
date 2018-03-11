package com.hotmail.AdrianSR.ProMaintenanceMode.Main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.AdrianSR.ProMaintenanceMode.Utils.Util;

public class MaintenanceMode 
{
	private final Integer time;
	private final TimeUnit unit;
	private Integer taskID = null;
	public ScheduledExecutorService executor;
	
	public MaintenanceMode(Integer time, TimeUnit unit) 
	{
		this.time = time;
		this.unit = unit;
		executor = Executors.newScheduledThreadPool(3);
	}
	
	private int remainingTime = 0;
	
	public void Start() 
	{
		if (time != null && time.longValue() > 0) {
			if (unit != null) {
				if (taskID == null) 
				{
					remainingTime = (int) (unit.toSeconds((long)time));
					
					taskID = Integer.valueOf(new BukkitRunnable() 
					{
						@Override
						public void run() 
						{
							remainingTime -= 1;
						}
					}.runTaskTimer(MM.getInstance(), 20L, 20L).getTaskId());
					
					// Stop Sheduler
					executor.schedule(new StopSheduler(), time, unit);
				}
				else Util.print(ChatColor.RED + "The maintenance mode is already started.");
			}
			else Util.print(ChatColor.RED + "Maintenance mode failed to start because the time unit is invalid.");
		}
		else Util.print(ChatColor.RED + "Maintenance mode failed to start because the time is invalid.");
	}
	
	private boolean stop = true;
	public void Stop() 
	{
		if (taskID != null) {
			Bukkit.getScheduler().cancelTask(taskID.intValue());
			Util.print(ChatColor.YELLOW + "The maintenance mode has been disabled.");
			
			stop = false;
			MM.setMaintenanceMode(null);
		}
		else Util.print(ChatColor.RED + "The maintenance mode is not started.");
	}
	
	public String getTimeWithFormat() 
	{
		return Util.format((((long)remainingTime) * 1000));
	}
	
	public Integer getTime() 
	{
		return time;
	}
	
	public long getRemainingTime() 
	{
		return remainingTime;
	}
	
	public TimeUnit getTimeUnit() 
	{
		return unit;
	}
	
	public Integer getTaskID() 
	{
		return taskID;
	}
	
	private class StopSheduler implements Runnable
	{
		@Override
		public void run() 
		{
			taskID = Integer.valueOf(new BukkitRunnable()
			{
				@Override
				public void run() 
				{
					if (stop) {
						Stop();
					}
				}
			}.runTask(MM.getInstance()).getTaskId());
		}
	}
}
