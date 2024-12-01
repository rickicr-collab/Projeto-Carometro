package view;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

// TODO: Auto-generated Javadoc
/**
 * The Class Sobre.
 */
public class Sobre extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The btn git hub. */
	private JButton btnGitHub;

	/**
	 * Launch the application.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Sobre dialog = new Sobre();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public Sobre() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Sobre.class.getResource("/img/instagram.png")));
		setTitle("Sobre o carômetro");
		setResizable(false);
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Projeto Carômetro");
		lblNewLabel.setBounds(38, 30, 235, 14);
		getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("@author - Ricardo Cunha");
		lblNewLabel_1.setBounds(38, 55, 230, 14);
		getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Sob Licensa MIT");
		lblNewLabel_2.setBounds(38, 89, 148, 14);
		getContentPane().add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setIcon(new ImageIcon(Sobre.class.getResource("/img/mit.png")));
		lblNewLabel_3.setBounds(296, 30, 96, 96);
		getContentPane().add(lblNewLabel_3);

		btnGitHub = new JButton("");
		btnGitHub.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				link("https://github.com/rickicr-collab/Projetos-Java");
			}
		});
		btnGitHub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnGitHub.setContentAreaFilled(false);
		btnGitHub.setBorderPainted(false);
		btnGitHub.setToolTipText("GitHub");
		btnGitHub.setIcon(new ImageIcon(Sobre.class.getResource("/img/github.png")));
		btnGitHub.setBounds(38, 160, 48, 48);
		getContentPane().add(btnGitHub);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnOk.setBounds(319, 216, 89, 23);
		getContentPane().add(btnOk);

	}

	/**
	 * Link.
	 *
	 * @param url the url
	 */
	private void link(String url) {
		Desktop desktop = Desktop.getDesktop();
		try {
			URI uri = new URI(url);
			desktop.browse(uri);

		} catch (Exception ex) {
			System.out.println("Exception gerada: [" + ex + "]");
		}
	}
	
}
