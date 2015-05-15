package mpa.core.logic;

import java.util.ArrayList;

import mpa.core.logic.resource.AbstractResourceProducer;

public class ResourceUpdater extends Thread
{
	private ArrayList<AbstractResourceProducer> resources = GameManager.getInstance().getWorld()
			.getResourceProducers();

	private static final int INTERVAL = 2000;

	@Override
	public void run()
	{
		while( true )
		{
			try
			{
				sleep( INTERVAL );
			} catch( InterruptedException e )
			{
				e.printStackTrace();
			}

			for( AbstractResourceProducer resource : resources )
			{
				resource.providePlayer();
			}
		}
	}

}