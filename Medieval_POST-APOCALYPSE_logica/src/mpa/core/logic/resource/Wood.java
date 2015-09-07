package mpa.core.logic.resource;

import mpa.core.logic.character.Player;
import mpa.core.util.GameProperties;

public class Wood extends AbstractResourceProducer
{
	private static final int PROVIDING = 35;
	private static final int EXTRA_PROVIDING = 10;

	public Wood( float x, float y, Player player )
	{
		super( x, y, GameProperties.getInstance().getObjectWidth( "Wood" ), GameProperties
				.getInstance().getObjectHeight( "Wood" ), player );
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean providePlayer()
	{
		try
		{
			readLock.lock();
			if( !super.providePlayer() )
				return false;
			if( owner != null )
				owner.putResources( "WOOD", PROVIDING + EXTRA_PROVIDING
						* owner.getPlayerLevel().ordinal() );
			return true;
		} finally
		{
			readLock.unlock();
		}
	}

	@Override
	public int getProviding()
	{
		return PROVIDING;
	}

}
