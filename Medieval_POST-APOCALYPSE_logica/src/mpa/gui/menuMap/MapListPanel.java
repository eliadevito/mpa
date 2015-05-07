package mpa.gui.menuMap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mpa.core.util.GameProperties;

public class MapListPanel extends JPanel
{

	private final String defaultMaps = GameProperties.getInstance().getPath("DefaultMapsPath");
	private final String customMaps = GameProperties.getInstance().getPath("CustomMapsPath");

	private JPanel mainPanel;

	private JList<String> mapList;
	private String[] data;
	JScrollPane scrollPane;

	public MapListPanel(MainMenuGamePanel mainPanel)
	{

		this.setOpaque(false);

		this.mainPanel = mainPanel;
		File folderDefaultMaps = new File(defaultMaps);
		final File[] listOfFilesDefaultFolder = folderDefaultMaps.listFiles();

		File folderCustomMaps = new File(customMaps);
		final File[] listOfFilesCustomFolder = folderCustomMaps.listFiles();

		data = new String[listOfFilesDefaultFolder.length + listOfFilesCustomFolder.length];

		int dataIndex = 0;
		for (int i = 0; i < listOfFilesDefaultFolder.length; i++)
		{
			if (listOfFilesDefaultFolder[i].isFile())
			{
				data[dataIndex] = listOfFilesDefaultFolder[i].getName().substring(0, listOfFilesDefaultFolder[i].getName().length() - 4);
				dataIndex++;
			}
		}
		for (int i = 0; i < listOfFilesCustomFolder.length; i++)
		{
			if (listOfFilesCustomFolder[i].isFile())
			{
				data[dataIndex] = listOfFilesCustomFolder[i].getName().substring(0, listOfFilesCustomFolder[i].getName().length() - 4);
				dataIndex++;
			}
		}

		mapList = new JList<>(data);

		mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mapList.setBackground(new Color(255, 255, 204));
		mapList.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					int selectedIndex = mapList.getSelectedIndex();

					if (selectedIndex < listOfFilesDefaultFolder.length)
						((MainMenuGamePanel) MapListPanel.this.mainPanel).setMap(MapListPanel.this.defaultMaps + "/" + mapList.getSelectedValue()
								+ ".xml");
					else
						((MainMenuGamePanel) MapListPanel.this.mainPanel).setMap(MapListPanel.this.customMaps + "/" + mapList.getSelectedValue()
								+ ".xml");

				}
			}
		});

		this.mainPanel = mainPanel;
		this.setLayout(null);

		mapList.setOpaque(false);
		mapList.setBounds(0, 0, mapList.getPreferredSize().width, mapList.getPreferredSize().width);
		scrollPane = new JScrollPane()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				try
				{
					Image backgroundImage = ImageIO.read(new File(GameProperties.getInstance().getPath("BackgroundImagesPath") + "/mapList.gif"));
					g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		};
		scrollPane.setLayout(null);
		scrollPane.add(mapList);
		scrollPane.setOpaque(false);

		scrollPane.setBounds(0, 0, this.getWidth() - 15, this.getHeight());
		scrollPane.repaint();

		this.add(scrollPane);

		this.setVisible(true);

	}

	@Override
	public void setBounds(int x, int y, int width, int height)
	{
		super.setBounds(x, y, width, height);
		scrollPane.setBounds(0, 0, this.getWidth() - 15, this.getHeight());
		mapList.setBounds(0, 0, this.getWidth() - 15, this.getHeight());
	}

}
