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

public class SettingsDialog extends JDialog{

	private final JPanel contentPanel = new JPanel();
	String[] textureNames = {"Cube", "Cube 2"};
	String[] themeNames = {"Island", "Snow", "Halloween"};
	private JTextField textFieldServerIP = new JTextField();
	private JTextField textFieldServerPort = new JTextField();
	private JCheckBox chckbxCreateServer = new JCheckBox("Create Server");
	JComboBox comboBoxTextureName = new JComboBox(textureNames);
	JComboBox comboBoxThemeName = new JComboBox(themeNames);
	JLabel lblCubeImage = new JLabel(new ImageIcon(new ImageIcon("images/textures/objects/" + comboBoxTextureName.getSelectedItem() + ".png").getImage().getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH)));
	JCheckBox chckbxMultiplayer = new JCheckBox("Multiplayer");

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
		this.setModal(true);
		setBounds(100, 100, 505, 278);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{95, 0, 0, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 28, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);

		
		chckbxMultiplayer.setSelected(true);
		chckbxMultiplayer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Boolean selected = chckbxMultiplayer.isSelected();
			chckbxCreateServer.setEnabled(selected);
			textFieldServerIP.setEnabled(selected && !chckbxCreateServer.isSelected());
			textFieldServerPort.setEnabled(selected);
			comboBoxThemeName.setEnabled(!selected || chckbxCreateServer.isSelected());
		}
		});
		{
			JLabel lblLevelTheme = new JLabel("Level Theme:");
			GridBagConstraints gbc_lblLevelTheme = new GridBagConstraints();
			gbc_lblLevelTheme.insets = new Insets(0, 0, 5, 5);
			gbc_lblLevelTheme.anchor = GridBagConstraints.EAST;
			gbc_lblLevelTheme.gridx = 0;
			gbc_lblLevelTheme.gridy = 5;
			contentPanel.add(lblLevelTheme, gbc_lblLevelTheme);
		}
		{
			
			GridBagConstraints gbc_comboBoxThemeName = new GridBagConstraints();
			gbc_comboBoxThemeName.insets = new Insets(0, 0, 5, 5);
			gbc_comboBoxThemeName.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxThemeName.gridx = 1;
			gbc_comboBoxThemeName.gridy = 5;
			contentPanel.add(comboBoxThemeName, gbc_comboBoxThemeName);
		}
		{
			GridBagConstraints gbc_lblCubeImage = new GridBagConstraints();
			gbc_lblCubeImage.insets = new Insets(0, 0, 5, 5);
			gbc_lblCubeImage.fill = GridBagConstraints.BOTH;
			gbc_lblCubeImage.gridheight = 3;
			gbc_lblCubeImage.gridwidth = 6;
			gbc_lblCubeImage.gridx = 2;
			gbc_lblCubeImage.gridy = 5;
			contentPanel.add(lblCubeImage, gbc_lblCubeImage);
		}
		{
			JLabel lblCubeTexture = new JLabel("Cube Texture:");
			GridBagConstraints gbc_lblCubeTexture = new GridBagConstraints();
			gbc_lblCubeTexture.anchor = GridBagConstraints.EAST;
			gbc_lblCubeTexture.insets = new Insets(0, 0, 5, 5);
			gbc_lblCubeTexture.gridx = 0;
			gbc_lblCubeTexture.gridy = 6;
			contentPanel.add(lblCubeTexture, gbc_lblCubeTexture);
		}
		GridBagConstraints gbc_comboBoxTextureName = new GridBagConstraints();
		gbc_comboBoxTextureName.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxTextureName.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxTextureName.gridx = 1;
		gbc_comboBoxTextureName.gridy = 6;
		comboBoxTextureName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblCubeImage.setIcon(new ImageIcon(new ImageIcon("images/textures/objects/" + comboBoxTextureName.getSelectedItem() + ".png").getImage().getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH)));
			}
		});
		contentPanel.add(comboBoxTextureName, gbc_comboBoxTextureName);
		GridBagConstraints gbc_chckbxMultiplayer = new GridBagConstraints();
		gbc_chckbxMultiplayer.anchor = GridBagConstraints.EAST;
		gbc_chckbxMultiplayer.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxMultiplayer.gridx = 0;
		gbc_chckbxMultiplayer.gridy = 7;
		contentPanel.add(chckbxMultiplayer, gbc_chckbxMultiplayer);
		//JCheckBox chckbxCreateServer = new JCheckBox("Create Server");
		chckbxCreateServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				textFieldServerIP.setEnabled(!chckbxCreateServer.isSelected());
				comboBoxThemeName.setEnabled(chckbxCreateServer.isSelected());
			}
		});
		chckbxCreateServer.setSelected(true);
		GridBagConstraints gbc_chckbxCreateServer = new GridBagConstraints();
		gbc_chckbxCreateServer.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxCreateServer.gridx = 1;
		gbc_chckbxCreateServer.gridy = 7;
		contentPanel.add(chckbxCreateServer, gbc_chckbxCreateServer);
		{
			JLabel lblServerIp = new JLabel("Server IP:");
			GridBagConstraints gbc_lblServerIp = new GridBagConstraints();
			gbc_lblServerIp.anchor = GridBagConstraints.EAST;
			gbc_lblServerIp.insets = new Insets(0, 0, 5, 5);
			gbc_lblServerIp.gridx = 0;
			gbc_lblServerIp.gridy = 8;
			contentPanel.add(lblServerIp, gbc_lblServerIp);
		}

		
		textFieldServerIP.setEnabled(false);
		GridBagConstraints gbc_textFieldServerIP = new GridBagConstraints();
		gbc_textFieldServerIP.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldServerIP.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldServerIP.gridx = 1;
		gbc_textFieldServerIP.gridy = 8;
		contentPanel.add(textFieldServerIP, gbc_textFieldServerIP);
		textFieldServerIP.setColumns(10);
		try
		{
			textFieldServerIP.setText(InetAddress.getLocalHost().getHostAddress());
		}
		catch(Exception e)
		{
			
		}
		{
			JLabel lblPort = new JLabel("Port:");
			GridBagConstraints gbc_lblPort = new GridBagConstraints();
			gbc_lblPort.anchor = GridBagConstraints.EAST;
			gbc_lblPort.insets = new Insets(0, 0, 5, 5);
			gbc_lblPort.gridx = 0;
			gbc_lblPort.gridy = 9;
			contentPanel.add(lblPort, gbc_lblPort);
		}
		{
			GridBagConstraints gbc_textFieldServerPort = new GridBagConstraints();
			gbc_textFieldServerPort.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldServerPort.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldServerPort.gridx = 1;
			gbc_textFieldServerPort.gridy = 9;
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
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Exit");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(1);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public String[] showDialog()
	{
		this.setVisible(true);
		String[] data = {(String)comboBoxThemeName.getSelectedItem(), (String)comboBoxTextureName.getSelectedItem(), String.valueOf(chckbxMultiplayer.isSelected()), String.valueOf(chckbxCreateServer.isSelected()), textFieldServerIP.getText(), textFieldServerPort.getText()};
		return data;
	}

}
