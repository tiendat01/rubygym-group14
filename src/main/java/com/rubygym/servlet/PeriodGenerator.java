package com.rubygym.servlet;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class PeriodGenerator implements ServletContextListener {

	private ScheduledExecutorService scheduler;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			scheduler = Executors.newSingleThreadScheduledExecutor(); // tạo 1 thread
			Runnable command = new PeriodGenThread(sce.getServletContext());
			
			LocalDateTime now = LocalDateTime.now();
			Map<DayOfWeek, Integer> dayToDelay = new HashMap<DayOfWeek, Integer>();
				dayToDelay.put(DayOfWeek.FRIDAY, 1);
				dayToDelay.put(DayOfWeek.SATURDAY, 0);
				dayToDelay.put(DayOfWeek.SUNDAY, 6); // every 23h sunday
				dayToDelay.put(DayOfWeek.MONDAY, 5);
				dayToDelay.put(DayOfWeek.TUESDAY, 4);
				dayToDelay.put(DayOfWeek.WEDNESDAY, 3);
				dayToDelay.put(DayOfWeek.THURSDAY, 2);
				
			DayOfWeek dayOfWeek = now.getDayOfWeek();
			int delayInDays = dayToDelay.get(dayOfWeek);
			int hour = now.getHour();
			int deleyInHours = 0;
			// 6 is Sunday
			if (delayInDays == 6 && hour < 23) {
				deleyInHours = 23 - hour;
			}
			else if (delayInDays == 6 && hour == 23) {
				;
			}
			else {
				deleyInHours = delayInDays * 24 + ((24-hour) + 23);
			}
			System.out.println("có chạy ko? Giờ hiện tại: " + now.toString() + " " + delayInDays);
			System.out.println(delayInDays + "   " + deleyInHours);
			// 24*7 everyweek
			scheduler.scheduleAtFixedRate(command, deleyInHours, 
					(24*7), TimeUnit.HOURS);
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		scheduler.shutdownNow();
	}
}
