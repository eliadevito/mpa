package mpa.gui.menuMap;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import mpa.core.ai.DifficultyLevel;
import mpa.core.logic.GameManager;
import mpa.core.logic.MapInfo;
import mpa.core.logic.Pair;
import mpa.core.logic.World;
import mpa.core.logic.WorldFromMapInfo;
import mpa.core.logic.WorldLoader;
import mpa.core.util.GameProperties;
import mpa.core.util.MapFromXMLCreator;

import org.jdom2.JDOMException;

import com.jme3.system.AppSettings;

public class MenuSinglePlayerPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MapInfo mapInfo;
	private MapListPanel mapListPanel;
	private MapPreview mapPreview;
	private InputNamePanel inputNamePanel;
	private DifficultyPanel difficultyPanel;
	private WorldLoader worldLoader = new WorldLoader();
	private Pair<Float, Float> selectedHQ = null;
	private String playerName = null;
	private JLabel buttonPlay;
	private Image backgroundImage;
	private int selectedHQIndex = 0;
	int screenWidth;
	int screenHeight;
	DifficultyLevel difficultyLevelSelected = null;
	private String textImagePath = GameProperties.getInstance().getPath("TextImagePath");

	private JButton back = null;

	public MenuSinglePlayerPanel()
	{
		// TODO Stub di costruttore generato automaticamente
	}

	public MenuSinglePlayerPanel(int x, int y, int width, int height)
	{

		this.setSize(width, height);
		this.setLocation(x, y);
		this.setLayout(null);
		screenWidth = this.getWidth();
		screenHeight = this.getHeight();

		addMapPreviewPanel();

		addMapListPanel();

		addInputNamePanel();

		addDifficultyPanel();

		Image imagePlay = null;
		try
		{
			imagePlay = ImageIO.read(new File(textImagePath + "/Play.png"));
			imagePlay = imagePlay.getScaledInstance(screenWidth * 20 / this.getWidth() * 10, 40, Image.SCALE_FAST);
			buttonPlay = new JLabel(new ImageIcon(imagePlay));
		} catch (IOException e2)
		{
			// TODO Blocco catch generato automaticamente
			e2.printStackTrace();
		}
		buttonPlay.setBounds(this.getWidth() - imagePlay.getWidth(this), 52 + screenHeight * 80 / 100, imagePlay.getWidth(this),
				imagePlay.getHeight(this));
		buttonPlay.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				playerName = inputNamePanel.getPlayerName();
				if (playerName == null || playerName.equals(""))
				{
					setButtonsStyle();
					JOptionPane.showMessageDialog(new Frame(), "Inserisci il nome che preferisci", "Message", JOptionPane.PLAIN_MESSAGE);
				}
				else if (selectedHQ == null)
					JOptionPane.showMessageDialog(new Frame(), "Seleziona il quartier generale", "Message", JOptionPane.PLAIN_MESSAGE);
				else if (difficultyLevelSelected == null)
					JOptionPane.showMessageDialog(new Frame(), "Seleziona la difficoltà", "Message", JOptionPane.PLAIN_MESSAGE);
				else
				{
					World world = MenuSinglePlayerPanel.this.worldLoader.loadWorld(new WorldFromMapInfo());
					// GameManager.init(world,
					// MainMenuGamePanel.this.worldLoader.makePlayers(MainMenuGamePanel.this.playerName,
					// world,
					// MainMenuGamePanel.this.selectedHQ));
					GameManager.init(world, difficultyLevelSelected, false);
					MenuSinglePlayerPanel.this.selectedHQIndex = MenuSinglePlayerPanel.this.worldLoader.makePlayers(
							MenuSinglePlayerPanel.this.playerName, world, MenuSinglePlayerPanel.this.selectedHQ);
					// MainMenuPanel.this.removeAll();
					// GameGui gameGui = new GameGui();
					// gameGui.setBounds(0, 0, MainMenuPanel.this.screenWidth,
					// MainMenuPanel.this.screenHeight);
					// MainMenuPanel.this.add(gameGui);
					// new GraphicThread( gameGui ).start();
					// new PositionUpdater().start();
					// MainMenuPanel.this.updateUI();
					// mpa.gui.gameGui.GameGui app = new mpa.gui.gameGui.GameGui();
					// app.start();
					// Logger.getLogger( "" ).setLevel( Level.OFF );
					Thread t = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							mpa.gui.gameGui.playingGUI.GameGui app = new mpa.gui.gameGui.playingGUI.GameGui(playerName);
							AppSettings gameSettings = new AppSettings(false);
							// gameSettings.setResolution( 800, 600 );
							// GraphicsDevice device = GraphicsEnvironment
							// .getLocalGraphicsEnvironment().getDefaultScreenDevice();

							// gameSettings.setResolution( screenWidth, screenHeight );
							// gameSettings.setFullscreen( true );

							gameSettings.setResolution(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width, java.awt.Toolkit
									.getDefaultToolkit().getScreenSize().height);
							gameSettings.setFullscreen(true);
							gameSettings.setVSync(false);
							gameSettings.setTitle("Stellar Conquest");
							gameSettings.setUseInput(true);
							gameSettings.setFrameRate(500);
							gameSettings.setSamples(0);
							gameSettings.setRenderer("LWJGL-OpenGL2");
							app.setSettings(gameSettings);
							app.setShowSettings(false);

							// disable statistics
							app.setDisplayFps(false);
							app.setDisplayStatView(false);
							app.start();
						}
					});
					t.start();
				}
			}

		});

		this.add(buttonPlay);
		try
		{
			backgroundImage = ImageIO.read(new File("Assets/BackgroundImages/mainMenu.jpg"));
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		this.setVisible(true);
	}

	private void setButtonsStyle()
	{

		Font font = null;
		try
		{
			font = Font.createFont(Font.PLAIN, new FileInputStream("./Assets/Fonts/KELMSCOT.TTF"));
		} catch (FontFormatException | IOException e)
		{
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}
		font = font.deriveFont(Font.PLAIN, 15);
		UIManager.put("Label.font", font);
		UIManager.put("Button.font", new Font("URW Chancery L", Font.BOLD, 13));
	}

	public void setMap(String mapName)
	{
		mapPreview.removeAll();
		try
		{
			worldLoader.loadMapInfo(new MapFromXMLCreator(), mapName);
		} catch (JDOMException | IOException e)
		{
			JOptionPane.showMessageDialog(this, "Sorry but this map seems to be lost or damaged :(", "MAP ERROR", JOptionPane.ERROR_MESSAGE);

			// TODO rimuovere la mappa danneggiata (o persa) dalla lista!!!!!
		}
		mapInfo = worldLoader.getMapInfo();
		mapPreview.setMapDimension(mapInfo.getWidth(), mapInfo.getHeight());
		mapPreview.loadMap(mapInfo);
		selectedHQ = null;
		this.repaint();
	}

	public void setSelectedHeadQuarter(Pair<Float, Float> headQuarterPosition)
	{
		if (mapInfo.getHeadQuarters().contains(headQuarterPosition))
			selectedHQ = headQuarterPosition;
	}

	public Pair<Float, Float> getSelectedHQ()
	{
		return selectedHQ;
	}

	public void setPlayerName(String playerName)
	{
		this.playerName = playerName;
	}

	public void setDifficultyLevel(DifficultyLevel difficultyLevel)
	{
		this.difficultyLevelSelected = difficultyLevel;
	}

	private void addMapPreviewPanel()
	{
		mapPreview = new MapPreview(this);
		mapPreview.setBounds(screenWidth * 55 / 100, 30, screenWidth * 45 / 100 - 30, screenHeight * 55 / 100);
		mapPreview.repaint();
		this.add(mapPreview);
	}

	private void addMapListPanel()
	{
		mapListPanel = new MapListPanel(this);
		mapListPanel.setBounds(screenWidth * 80 / 100, 50 + screenHeight * 60 / 100, screenWidth * 19 / 100 + 5, screenHeight * 20 / 100);
		System.out.println(mapListPanel.getBounds());
		this.add(mapListPanel);
	}

	private void addInputNamePanel()
	{
		inputNamePanel = new InputNamePanel();
		inputNamePanel.setBounds(40, 45, screenWidth * 50 / 100, screenHeight * 40 / 100);
		inputNamePanel.setOpaque(false);
		this.add(inputNamePanel);
	}

	private void addDifficultyPanel()
	{
		difficultyPanel = new DifficultyPanel(this);
		difficultyPanel.setBounds(40, screenHeight * 60 / 100, screenWidth * 45 / 100, screenHeight * 25 / 100);
		this.repaint();
		this.add(difficultyPanel);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, screenWidth, screenHeight, this);
	}

	public static void main(String[] args)
	{

		int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;

		JFrame frame = new JFrame();
		frame.setLocation(0, 0);

		frame.setSize(screenWidth, screenHeight);
		MenuSinglePlayerPanel menuSinglePlayerPanel = new MenuSinglePlayerPanel(screenWidth * 15 / 100, screenHeight * 10 / 100,
				screenWidth * 70 / 100, screenHeight * 80 / 100);

		frame.setContentPane(new Panel());
		frame.getContentPane().setBackground(Color.BLACK);
		frame.getContentPane().add(menuSinglePlayerPanel);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setVisible(true);;
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}