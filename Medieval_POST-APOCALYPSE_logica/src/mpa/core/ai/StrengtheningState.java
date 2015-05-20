package mpa.core.ai;

import javax.vecmath.Vector2f;

import mpa.core.logic.GameManager;
import mpa.core.logic.character.Player;

class StrengtheningState extends AIState
{
	private boolean isWalking = false;

	public StrengtheningState()
	{
		super();
	}

	@Override
	void action( OpponentAI opponentAI )
	{
		System.out.println( "Strengthening State" );
		Player p = opponentAI.player;
		try
		{
			// p.getWriteLock();

			if( isWalking )
				return;
			Vector2f collectionPoint = p.getHeadquarter().getCollectionPoint();
			if( ( p.getPath() == null || p.getPath().isEmpty() )
					&& ( ( int ) p.getX() ) == ( ( int ) collectionPoint.x )
					&& ( ( int ) p.getY() ) == ( ( int ) collectionPoint.y ) )
			{
				if( p.canUpgrade() )
					p.upgradeLevel();

				System.out.println();
				System.out.println();
				System.out.println( "ho uppato di livello" );
				System.out.println();
				System.out.println();

				isWalking = false;
			}

			else if( p.canUpgrade() )
			{
				GameManager.getInstance().computePath( p, collectionPoint.x, collectionPoint.y );
				isWalking = true;
			}
		} finally
		{
			// p.leaveWriteLock();
		}
	}

	@Override
	AIState changeState( OpponentAI opponentAI )
	{
		AIState nextState = null;

		if( opponentAI.player.canUpgrade() || isWalking )
			nextState = this;
		// else if( opponentAI.player.canBuyPotions() )
		// nextState = new ProductionState();
		// else if( !opponentAI.knownAllTheWorld )
		// nextState = new ExplorationState();
		// else if( opponentAI.areThereWeakerPlayers() )
		// nextState = new CombatState();
		else if( !opponentAI.knownBuildings.isEmpty() && opponentAI.areThereConquerableBuildings()
				&& opponentAI.player.isThereAnyFreeSulbaltern() )
			nextState = new ConquestState();
		else if( !opponentAI.knownAllTheWorld )
			nextState = new ExplorationState();
		else
			nextState = new WaitingState();

		return nextState;
	}

}
