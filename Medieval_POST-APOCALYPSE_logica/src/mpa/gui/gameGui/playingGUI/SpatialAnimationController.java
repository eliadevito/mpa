package mpa.gui.gameGui.playingGUI;

import java.util.Random;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.scene.Spatial;

public class SpatialAnimationController implements AnimEventListener
{
	Spatial model;
	private AnimChannel channel;
	private AnimControl control;
	private String name;
	private GuiObjectManager guiObjectManager;
	private String type;
	private boolean dead = false;

	public SpatialAnimationController(Spatial model, GuiObjectManager guiObjectManager, String name, String type)
	{
		this.model = model;
		this.guiObjectManager = guiObjectManager;
		this.name = name;
		this.type = type;
		control = model.getControl(AnimControl.class);
		control.addListener(this);
		channel = control.createChannel();
		channel.setAnim("stand");
	}

	@Override
	public void onAnimChange(AnimControl arg0, AnimChannel arg1, String animName)
	{
		// TODO Stub di metodo generato automaticamente

	}

	@Override
	public void onAnimCycleDone(AnimControl arg0, AnimChannel arg1, String animName)
	{
		if (animName.equals("death"))
		{
			if (type.equals("player"))
				guiObjectManager.removePlayer(name);
			else if (type.equals("minion"))
				guiObjectManager.removeMinion(name);
			else if (type.equals("towerCrusher"))
				guiObjectManager.removeTowerCrusher(name);
		}
		else if (animName.equals("attack") || animName.equals("attack1"))
		{
			channel.setAnim("stand");
			if (dead)
				channel.setAnim("death");
		}
	}

	public void startWalkAnimation(float animationSpeed)
	{
		if (channel.getAnimationName() == null || channel.getAnimationName().equals("stand"))
		{
			channel.setAnim("walk");
			channel.setSpeed(animationSpeed);
			channel.setLoopMode(LoopMode.Loop);
		}
	}

	public void stopWalkAnimation()
	{
		if (channel.getAnimationName() != null && channel.getAnimationName().equals("walk") && !channel.getAnimationName().equals("death"))
		{
			channel.setAnim("stand");
		}
	}

	public void startAttackAnimation(float animationSpeed)
	{
		if (control.getAnim("attack1") != null)
		{
			int rand = new Random().nextInt(2);
			if (rand == 0)
				channel.setAnim("attack1");
			else
				channel.setAnim("attack");

		}
		else
		{
			channel.setAnim("attack");
		}
		channel.setSpeed(animationSpeed);
		channel.setLoopMode(LoopMode.DontLoop);
	}

	public String getAnimationRunning()
	{
		if (channel.getTime() < channel.getAnimMaxTime())
			return channel.getAnimationName();
		else
			return null;
	}

	public boolean startDeathAnimation()
	{
		if (control.getAnim("death") != null)
		{
			dead = true;
			channel.setAnim("death");
			channel.setLoopMode(LoopMode.DontLoop);
			return true;
		}
		else
			return false;

	}

	public void stopAnimation()
	{
		if (channel.getAnimationName() != null && !channel.getAnimationName().equals("stand"))
		{
			channel.setAnim("stand");
		}

	}
}
