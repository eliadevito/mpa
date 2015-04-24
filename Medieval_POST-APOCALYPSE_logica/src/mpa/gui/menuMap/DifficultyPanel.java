package mpa.gui.menuMap;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import mpa.core.ai.DifficultyLevel;

public class DifficultyPanel extends JPanel
{

	private MainMenuGamePanel mainMenuGamePanel;

	private List<AbstractButton> buttonList = new ArrayList<AbstractButton>();
	private ButtonGroup group = new ButtonGroup();
	private Image backgroundImage;
	private ActionListener actionListenerRadioButton;

	public DifficultyPanel(MainMenuGamePanel mainMenuGamePanel)
	{
		this.mainMenuGamePanel = mainMenuGamePanel;

		initRadioButtonGroup();
		this.setLayout(null);
		this.setOpaque(false);

		actionListenerRadioButton = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				String difficultyLevelSelected = ((JRadioButton) e.getSource()).getLabel();
				List<DifficultyLevel> somethingList = Arrays.asList(DifficultyLevel.values());

				for (DifficultyLevel difficultyLevel : somethingList)
				{
					if (difficultyLevel.name().equals(difficultyLevelSelected))
						DifficultyPanel.this.mainMenuGamePanel.setDifficultyLevel(difficultyLevel);
				}

			}

		};
		try
		{
			backgroundImage = ImageIO.read(new File("Assets/BackgroundImages/difficultyPanel.png"));
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}

		this.setVisible(true);

	}

	private void initRadioButtonGroup()
	{

		List<DifficultyLevel> somethingList = Arrays.asList(DifficultyLevel.values());

		for (DifficultyLevel difficultyLevel : somethingList)
		{
			JRadioButton jRadioButton = new JRadioButton(difficultyLevel.name());
			buttonList.add(jRadioButton);
			group.add(jRadioButton);
		}

	}

	private void addComponents()
	{

		int yComponent = this.getHeight() * 15 / 100;
		int xComponent = this.getWidth() * 15 / 100;

		int increment = (this.getHeight() - this.getHeight() * 30 / 100) / (buttonList.size() + 1);
		JLabel label = new JLabel("Choose Difficulty Level");
		label.setOpaque(false);
		label.setBounds(xComponent, yComponent, this.getWidth() - xComponent, increment);

		this.add(label);

		for (AbstractButton button : buttonList)
		{
			yComponent += increment;
			button.addActionListener(actionListenerRadioButton);
			button.setOpaque(false);
			button.setBounds(xComponent, yComponent, this.getWidth() - xComponent, increment);
			this.add(button);

		}
	}

	@Override
	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);
		addComponents();

	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);

	}
}
