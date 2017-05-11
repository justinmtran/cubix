package GameEngine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SettingsDialog extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	String[] textureNames = {"Cube", "Cube 2"};
	String[] themeNames = {"Island", "Snow"};
	private JTextField textFieldServerIP;
	private JTextField textFieldServerPort;
	private JCheckBox chckbxCreateServer = new JCheckBox("Create Server");
	JComboBox comboBoxTextureName = new JComboBox(textureNames);
	JLabel lblCubeImage = new JLabel(new ImageIcon(new ImageIcon("images/textures/objects/" + comboBoxTextureName.getSelectedItem() + ".png").getImage().getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH)));

	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SettingsDialog dialog = new SettingsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {
		String[] textureNames = {"Cube", "Cube 2"};
		String[] themeNames = {"Island", "Snow"};
		setBounds(100, 100, 477, 263);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);

		JCheckBox chckbxMultiplayer = new JCheckBox("Multiplayer");
		chckbxMultiplayer.setSelected(true);
		chckbxMultiplayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Boolean selected = chckbxMultiplayer.isSelected();
				chckbxCreateServer.setEnabled(selected);
				textFieldServerIP.setEnabled(selected && !chckbxCreateServer.isSelected());
				textFieldServerPort.setEnabled(selected);
			}
		});
		GridBagConstraints gbc_chckbxMultiplayer = new GridBagConstraints();
		gbc_chckbxMultiplayer.fill = GridBagConstraints.HORIZONTAL;
		gbc_chckbxMultiplayer.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxMultiplayer.gridx = 0;
		gbc_chckbxMultiplayer.gridy = 0;
		contentPanel.add(chckbxMultiplayer, gbc_chckbxMultiplayer);
		{
			JLabel lblCubeTexture = new JLabel("Cube Texture:");
			GridBagConstraints gbc_lblCubeTexture = new GridBagConstraints();
			gbc_lblCubeTexture.anchor = GridBagConstraints.EAST;
			gbc_lblCubeTexture.insets = new Insets(0, 0, 5, 5);
			gbc_lblCubeTexture.gridx = 5;
			gbc_lblCubeTexture.gridy = 0;
			contentPanel.add(lblCubeTexture, gbc_lblCubeTexture);
		}

		{
			//JCheckBox chckbxCreateServer = new JCheckBox("Create Server");
			chckbxCreateServer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
						textFieldServerIP.setEnabled(!chckbxCreateServer.isSelected());

				}
			});
			{
				GridBagConstraints gbc_comboBoxTextureName = new GridBagConstraints();
				gbc_comboBoxTextureName.gridwidth = 6;
				gbc_comboBoxTextureName.insets = new Insets(0, 0, 5, 5);
				gbc_comboBoxTextureName.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBoxTextureName.gridx = 6;
				gbc_comboBoxTextureName.gridy = 0;
				comboBoxTextureName.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						lblCubeImage.setIcon(new ImageIcon(new ImageIcon("images/textures/objects/" + comboBoxTextureName.getSelectedItem() + ".png").getImage().getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH)));
					}
				});
				contentPanel.add(comboBoxTextureName, gbc_comboBoxTextureName);
			}
			chckbxCreateServer.setSelected(true);
			GridBagConstraints gbc_chckbxCreateServer = new GridBagConstraints();
			gbc_chckbxCreateServer.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxCreateServer.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxCreateServer.gridx = 0;
			gbc_chckbxCreateServer.gridy = 1;
			contentPanel.add(chckbxCreateServer, gbc_chckbxCreateServer);
		}
		{
			JLabel lblLevelTheme = new JLabel("Level Theme:");
			GridBagConstraints gbc_lblLevelTheme = new GridBagConstraints();
			gbc_lblLevelTheme.insets = new Insets(0, 0, 5, 5);
			gbc_lblLevelTheme.anchor = GridBagConstraints.EAST;
			gbc_lblLevelTheme.gridx = 5;
			gbc_lblLevelTheme.gridy = 1;
			contentPanel.add(lblLevelTheme, gbc_lblLevelTheme);
		}
		{
			JComboBox comboBoxThemeName = new JComboBox(themeNames);
			GridBagConstraints gbc_comboBoxThemeName = new GridBagConstraints();
			gbc_comboBoxThemeName.gridwidth = 6;
			gbc_comboBoxThemeName.insets = new Insets(0, 0, 5, 5);
			gbc_comboBoxThemeName.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxThemeName.gridx = 6;
			gbc_comboBoxThemeName.gridy = 1;
			contentPanel.add(comboBoxThemeName, gbc_comboBoxThemeName);
		}
		{
			GridBagConstraints gbc_lblCubeImage = new GridBagConstraints();
			gbc_lblCubeImage.gridheight = 4;
			gbc_lblCubeImage.gridwidth = 5;
			gbc_lblCubeImage.insets = new Insets(0, 0, 5, 5);
			gbc_lblCubeImage.gridx = 7;
			gbc_lblCubeImage.gridy = 2;
			contentPanel.add(lblCubeImage, gbc_lblCubeImage);
		}
		{
			JLabel lblServerIp = new JLabel("Server IP:");
			GridBagConstraints gbc_lblServerIp = new GridBagConstraints();
			gbc_lblServerIp.insets = new Insets(0, 0, 5, 5);
			gbc_lblServerIp.gridx = 0;
			gbc_lblServerIp.gridy = 3;
			contentPanel.add(lblServerIp, gbc_lblServerIp);
		}
		{
			textFieldServerIP = new JTextField();
			textFieldServerIP.setEnabled(false);
			GridBagConstraints gbc_textFieldServerIP = new GridBagConstraints();
			gbc_textFieldServerIP.gridwidth = 5;
			gbc_textFieldServerIP.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldServerIP.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldServerIP.gridx = 1;
			gbc_textFieldServerIP.gridy = 3;
			contentPanel.add(textFieldServerIP, gbc_textFieldServerIP);
			textFieldServerIP.setColumns(10);
			try
			{
				textFieldServerIP.setText(InetAddress.getLocalHost().getHostAddress());
			}
			catch(Exception e)
			{
				textFieldServerIP.setText("");
			}
		}
		{
			JLabel lblPort = new JLabel("Port:");
			GridBagConstraints gbc_lblPort = new GridBagConstraints();
			gbc_lblPort.insets = new Insets(0, 0, 5, 5);
			gbc_lblPort.gridx = 0;
			gbc_lblPort.gridy = 4;
			contentPanel.add(lblPort, gbc_lblPort);
		}
		{
			textFieldServerPort = new JTextField();
			GridBagConstraints gbc_textFieldServerPort = new GridBagConstraints();
			gbc_textFieldServerPort.gridwidth = 5;
			gbc_textFieldServerPort.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldServerPort.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldServerPort.gridx = 1;
			gbc_textFieldServerPort.gridy = 4;
			contentPanel.add(textFieldServerPort, gbc_textFieldServerPort);
			textFieldServerPort.setColumns(10);
			textFieldServerPort.setText("6000");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		
	}

}
