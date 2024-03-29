package mpa.core.ai;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2f;

import mpa.core.logic.GameManager;
import mpa.core.logic.characters.Minion;
import mpa.core.logic.characters.Player;
import mpa.core.logic.characters.TowerCrusher;
import mpa.core.logic.characters.Player.Item;
import mpa.core.logic.potions.Potions;

public class DefenseState extends AIState
{

	Choice myChoice = null;

	enum Choice
	{
		FIGHT_BACK, RUN;
	}

	public DefenseState( OpponentAI opponentAI, List<Enemy> bullies )
	{
		opponentAI.player.stopMoving();
		Collections.sort( bullies );

		bully = bullies.remove( 0 ).getEnemy();

		if( !bullies.isEmpty()
				&& ( bully instanceof Minion
						|| ( bully instanceof Player && opponentAI
								.canIFightWithHim( ( ( Player ) bully ) ) ) || bully instanceof TowerCrusher ) )
			myChoice = Choice.FIGHT_BACK;
		else
			myChoice = Choice.RUN;

	}

	@Override
	void action( OpponentAI opponentAI )
	{
		if( myChoice.ordinal() == Choice.FIGHT_BACK.ordinal() )
			return;

		int flashBangAmount = opponentAI.player.getPotionAmount( Potions.FLASH_BANG );

		if( flashBangAmount > 0 && bully instanceof Player )
		{
			do
			{
				GameManager.getInstance().changeSelectedItem( opponentAI.player, Item.FLASH_BANG );
				GameManager.getInstance().playerAction( opponentAI.player, bully.getPosition() );
			} while( !( ( Player ) bully ).isFlashed()
					&& opponentAI.player.getPotionAmount( Potions.FLASH_BANG ) > 0 );
			Vector2f gatheringPlace = opponentAI.player.getHeadquarter().getGatheringPlace();
			GameManager.getInstance().computePath( opponentAI.player, gatheringPlace.x,
					gatheringPlace.y );

		}
		else
		{
			Vector2f gatheringPlace = opponentAI.player.getHeadquarter().getGatheringPlace();
			GameManager.getInstance().computePath( opponentAI.player, gatheringPlace.x,
					gatheringPlace.y );
		}

		bully = null;

	}

	@Override
	AIState changeState( OpponentAI opponentAI )
	{
		if( myChoice.ordinal() == Choice.FIGHT_BACK.ordinal() )
			return new CombatState( bully );

		AIState nextState = null;

		if( opponentAI.player.canUpgrade() )
			nextState = new StrengtheningState();
		else if( opponentAI.shouldBuyPotions() && opponentAI.player.canBuyPotions()
				&& opponentAI.canGoToThisState( ProductionState.class ) )
			nextState = new ProductionState();
		else if( !opponentAI.knownAllTheWorld
				&& opponentAI.canGoToThisState( ExplorationState.class ) )
			nextState = new ExplorationState();
		else if( !opponentAI.knownBuildings.isEmpty() && opponentAI.areThereConquerableBuildings()
				&& opponentAI.canGoToThisState( ConquestState.class ) )
			nextState = new ConquestState();
		else if( opponentAI.shouldICreateTowers() && opponentAI.canIcreateTowers() )
			nextState = new FortificationState();
		else
		{
			opponentAI.resetStateCounters();
			nextState = new WaitingState();
		}
		return nextState;

	}
}
