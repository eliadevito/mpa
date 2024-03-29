package mpa.core.logic.fights;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.vecmath.Vector2f;

import mpa.core.ai.OpponentAI;
import mpa.core.logic.MyThread;
import mpa.core.logic.characters.AbstractCharacter;
import mpa.core.logic.characters.Minion;
import mpa.core.logic.characters.Player;
import mpa.core.logic.characters.Player.Item;
import mpa.core.logic.potions.Potions;

public class AttackRequests extends MyThread
{
	private List<AbstractCharacter> attackList = new ArrayList<>();
	private List<Vector2f> targets = new ArrayList<>();
	private Map<Player, OpponentAI> AI_players;
	private ReentrantLock lock = new ReentrantLock();

	public AttackRequests(Map<Player, OpponentAI> AI_players)
	{
		this.AI_players = AI_players;
	}

	public void addRequest(AbstractCharacter character, Vector2f target)
	{
		lock.lock();
		if (character != null)
		{

			attackList.add(character);
			targets.add(target);
		}
		lock.unlock();
	}

	@Override
	public void run()
	{
		while (true)
		{
			lock.lock();
			super.run();
			List<AbstractCharacter> hitPlayers = new ArrayList<>();
			if (!attackList.isEmpty())
			{
				AbstractCharacter attacker = attackList.remove(0);
				Vector2f target = targets.get(0);
				if (attacker instanceof Minion)
					hitPlayers = CombatManager.getInstance().attackPhysically((Minion) attacker);
				else if (attacker instanceof Player)
				{
					Player player = (Player) attacker;
					if (player.getSelectedItem().equals(Item.WEAPON))
						CombatManager.getInstance().attackPhysically((Player) attacker);
					else if (player.getSelectedItem().equals(Item.GRANADE))
						hitPlayers = CombatManager.getInstance().distanceAttack(player, player.takePotion(Potions.GRANADE), target);
					else if (player.getSelectedItem().equals(Item.FLASH_BANG))
						hitPlayers = CombatManager.getInstance().distanceAttack(player, player.takePotion(Potions.FLASH_BANG), target);

				}
				for (AbstractCharacter hitPlayer : hitPlayers)
				{
					if (hitPlayer != attacker && AI_players.keySet().contains(hitPlayer))
					{
						System.out.println("ho segnalato l'attacco");
						AI_players.get(hitPlayer).gotAttackedBy(attacker);
					}
				}

			}
			lock.unlock();
		}
	}

}
