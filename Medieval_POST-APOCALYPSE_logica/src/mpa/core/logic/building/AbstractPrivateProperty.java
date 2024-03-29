package mpa.core.logic.building;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import mpa.core.logic.characters.DependentCharacter;
import mpa.core.logic.characters.Player;

public abstract class AbstractPrivateProperty extends AbstractProperty
{

	protected Player owner = null;
	protected List<DependentCharacter> controllers = new ArrayList<>();

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected Lock readLock = lock.readLock();
	protected Lock writeLock = lock.writeLock();

	public AbstractPrivateProperty( float x, float y, float width, float height, Player owner )
	{
		super( x, y, width, height );
		this.owner = owner;
	}

	public boolean isFree()
	{
		try
		{
			readLock.lock();

			return owner == null;
		} finally
		{
			readLock.unlock();
		}
	}

	public void setOwner( Player player )
	{
		writeLock.lock();
		this.owner = player;
		writeLock.unlock();
	}

	public Player getOwner()
	{
		try
		{
			readLock.lock();
			return owner;
		} finally
		{
			readLock.unlock();
		}
	}

	public int getNumberOfControllers()
	{
		return controllers.size();
	}

	public void setController( DependentCharacter controller )
	{
		writeLock.lock();
		// if( this.controllers != null )
		// throw new ControllerAlreadyPresentException();
		// else if( owner != controller.getBoss() )
		// throw new DifferentOwnerException();

		this.controllers.add( controller );

		writeLock.unlock();
	}
}
