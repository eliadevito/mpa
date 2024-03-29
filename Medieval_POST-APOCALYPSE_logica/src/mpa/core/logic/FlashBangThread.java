package mpa.core.logic;

import mpa.core.logic.characters.Player;

public class FlashBangThread extends MyThread
{
	private Player player;
	private final long STANDARD_TIME_TO_WAKE = 15000;
	private long timeToWake;
	private long startingTime;
	private long residualTime;

	public FlashBangThread(Player player, long timeToSleep)
	{
		super();
		if (timeToSleep != -1)
			timeToWake = timeToSleep;
		else
			timeToWake = STANDARD_TIME_TO_WAKE - 100 * player.getPlayerLevel().ordinal();
		this.player = player;
	}

	public long getResidualTime()
	{
		return residualTime;
	}

	@Override
	public synchronized void run()
	{
		super.run();
		startingTime = System.currentTimeMillis();

		residualTime = System.currentTimeMillis() - startingTime;
		while (residualTime < timeToWake)
		{
			System.out.println(residualTime + " " + isInterrupted());
			if (isInterrupted())
			{
				return;
			}
			residualTime = System.currentTimeMillis() - startingTime;
		}

		player.getWriteLock();
		player.setFlashed(false);
		player.leaveWriteLock();
	}
}
