package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import model.DAO;
import utils.Validador;

@SuppressWarnings("unused")
public class Carometro extends JFrame {

	DAO dao = new DAO();

	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;
	private FileInputStream fis;
	private int tamanho;
	private boolean fotoCarregada = false;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblStatus;
	private JLabel lblData;
	private JLabel lblRa;
	private JTextField txtRA;
	private JLabel lblNome;
	private JTextField txtNome;
	private JLabel lblFoto;
	private JButton btnCarregar;
	private JButton btnAdicionar;
	private JButton btnReset;
	private JButton btnBuscar;
	private JList<String> listNomes;
	private JScrollPane scrollPaneLista;
	private JPanel panel;
	private JButton btnAtualizar;
	private JButton btnExcluir;
	private JButton btnSobre;
	private JButton btnPdf;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Carometro frame = new Carometro();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Carometro() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				status();
				setarData();
			}
		});
		setTitle("Carômetro");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Carometro.class.getResource("/img/instagram.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 667, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		scrollPaneLista = new JScrollPane();
		scrollPaneLista.setBorder(null);
		scrollPaneLista.setVisible(false);
		scrollPaneLista.setBounds(55, 107, 228, 88);
		contentPane.add(scrollPaneLista);

		listNomes = new JList<String>();
		listNomes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				buscarNomes();
			}
		});
		listNomes.setBorder(null);
		scrollPaneLista.setViewportView(listNomes);

		panel = new JPanel();
		panel.setBackground(SystemColor.textHighlight);
		panel.setBounds(0, 323, 651, 58);
		contentPane.add(panel);
		panel.setLayout(null);

		lblStatus = new JLabel("");
		lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dboff.png")));
		lblStatus.setBounds(593, 15, 32, 32);
		panel.add(lblStatus);

		lblData = new JLabel("");
		lblData.setForeground(SystemColor.text);
		lblData.setFont(new Font("Monospaced", Font.PLAIN, 16));
		lblData.setBounds(10, 15, 518, 25);
		panel.add(lblData);

		lblRa = new JLabel("RA");
		lblRa.setBounds(10, 40, 35, 14);
		contentPane.add(lblRa);

		txtRA = new JTextField();
		txtRA.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String caracteres = "0123456789";
				if (!caracteres.contains(e.getKeyChar() + "")) {
					e.consume();
				}
			}
		});
		txtRA.setBounds(55, 37, 96, 20);
		contentPane.add(txtRA);
		txtRA.setColumns(10);
		txtRA.setDocument(new Validador(6));

		lblNome = new JLabel("Nome");
		lblNome.setBounds(10, 90, 35, 14);
		contentPane.add(lblNome);

		txtNome = new JTextField();
		txtNome.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		txtNome.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				listarNomes();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					scrollPaneLista.setVisible(false);
					String message = "Não foi encontrado nenhum aluno no registro\nDeseja Fazer um registro de um novo aluno com esse nome?";
					int confirma = JOptionPane.showConfirmDialog(null, message, "Aviso", JOptionPane.YES_OPTION);
					if (confirma == JOptionPane.YES_OPTION) {
						txtRA.setEditable(false);
						btnBuscar.setEnabled(false);
						btnCarregar.setEnabled(true);
						btnAdicionar.setEnabled(true);
						btnPdf.setEnabled(false);
					} else {
						reset();
					}
				}
			}
		});
		txtNome.setBounds(55, 87, 228, 20);
		contentPane.add(txtNome);
		txtNome.setColumns(10);
		txtNome.setDocument(new Validador(30));

		lblFoto = new JLabel("");
		lblFoto.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/photo.png")));
		lblFoto.setBounds(385, 40, 256, 256);
		contentPane.add(lblFoto);

		btnCarregar = new JButton("Carregar Foto");
		btnCarregar.setEnabled(false);
		btnCarregar.setBounds(new Rectangle(5, 10, 5, 10));
		btnCarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carregarFoto();
			}
		});
		btnCarregar.setBackground(new Color(240, 240, 240));
		btnCarregar.setForeground(SystemColor.textHighlight);
		btnCarregar.setFont(new Font("Monospaced", Font.BOLD, 11));
		btnCarregar.setBounds(10, 153, 141, 25);
		contentPane.add(btnCarregar);

		btnAdicionar = new JButton("");
		btnAdicionar.setEnabled(false);
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adicionar();
			}
		});
		btnAdicionar.setToolTipText("Adicionar");
		btnAdicionar.setIcon(new ImageIcon(Carometro.class.getResource("/img/create.png")));
		btnAdicionar.setBackground(UIManager.getColor("Button.background"));
		btnAdicionar.setBounds(20, 248, 64, 64);
		contentPane.add(btnAdicionar);

		btnReset = new JButton("");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		btnReset.setToolTipText("Limpar Campos");
		btnReset.setIcon(new ImageIcon(Carometro.class.getResource("/img/eraser.png")));
		btnReset.setBackground(UIManager.getColor("Button.background"));
		btnReset.setBounds(297, 248, 64, 64);
		contentPane.add(btnReset);

		btnBuscar = new JButton("Buscar ");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buscarRA();
			}
		});
		btnBuscar.setBounds(new Rectangle(5, 10, 5, 10));
		btnBuscar.setForeground(SystemColor.textHighlight);
		btnBuscar.setFont(new Font("Monospaced", Font.BOLD, 11));
		btnBuscar.setBackground(new Color(233, 233, 233));
		btnBuscar.setBounds(161, 35, 96, 25);
		contentPane.add(btnBuscar);

		btnAtualizar = new JButton("");
		btnAtualizar.setEnabled(false);
		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				atualizar();
			}
		});
		btnAtualizar.setToolTipText("Atualizar");
		btnAtualizar.setIcon(new ImageIcon(Carometro.class.getResource("/img/update.png")));
		btnAtualizar.setBackground(UIManager.getColor("Button.background"));
		btnAtualizar.setBounds(110, 248, 64, 64);
		contentPane.add(btnAtualizar);

		btnExcluir = new JButton("");
		btnExcluir.setEnabled(false);
		btnExcluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				excluir();
			}
		});
		btnExcluir.setToolTipText("Excluir");
		btnExcluir.setIcon(new ImageIcon(Carometro.class.getResource("/img/delete.png")));
		btnExcluir.setBackground(UIManager.getColor("Button.background"));
		btnExcluir.setBounds(203, 248, 64, 64);
		contentPane.add(btnExcluir);

		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(Carometro.class.getResource("/img/search.png")));
		label.setBounds(303, 90, 46, 14);
		contentPane.add(label);

		btnSobre = new JButton("");
		btnSobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Sobre sobre = new Sobre();
				sobre.setVisible(true);
			}
		});
		btnSobre.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSobre.setContentAreaFilled(false);
		btnSobre.setBorderPainted(false);
		btnSobre.setIcon(new ImageIcon(Carometro.class.getResource("/img/info.png")));
		btnSobre.setBounds(301, 11, 48, 48);
		contentPane.add(btnSobre);

		btnPdf = new JButton("");
		btnPdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gerarPdf();
			}

		});
		btnPdf.setToolTipText("Gerar Lista de Alunos");
		btnPdf.setIcon(new ImageIcon(Carometro.class.getResource("/img/pdf.png")));
		btnPdf.setBounds(297, 158, 64, 64);
		contentPane.add(btnPdf);

		this.setLocationRelativeTo(null);

	}

	private void status() {
		try (Connection con = dao.conectar()) {
			if (con == null) {
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dboff.png")));
			} else {
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dbon.png")));
			}
		} catch (Exception e) {
			System.out.println("Exception gerada: [" + e + "]");
		}
	}

	private void setarData() {
		Date data = new Date();
		DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
		lblData.setText(format.format(data));
	}

	private void carregarFoto() {
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Selecionar Arquivo");
		jfc.setFileFilter(new FileNameExtensionFilter("Arquivos de imagens(*.PNG,*.JPG,*.JPEG)", "png", "jpg", "jpeg"));
		int resultado = jfc.showOpenDialog(this);
		if (resultado == JFileChooser.APPROVE_OPTION) {
			try {
				fis = new FileInputStream(jfc.getSelectedFile());
				tamanho = (int) jfc.getSelectedFile().length();
				Image foto = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(lblFoto.getWidth(),
						lblFoto.getHeight(), Image.SCALE_SMOOTH);
				lblFoto.setIcon(new ImageIcon(foto));
				lblFoto.updateUI();
				fotoCarregada = true;
			} catch (Exception e) {
				System.out.println("Exception gerada: [" + e + "]");
			}
		}

	}

	private void adicionar() {
		if (txtNome.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Atenção! Campo nome Obrigatório Favor Preencher.");
			txtNome.requestFocus();
		} else if (tamanho == 0) {
			JOptionPane.showMessageDialog(null, "Selecione a Foto");
		} else {
			String insert = "insert into alunos (nome, foto) values(?,?)";
			try (Connection con = dao.conectar()) {
				pst = con.prepareStatement(insert);
				pst.setString(1, txtNome.getText());
				pst.setBlob(2, fis, tamanho);
				int confirma = pst.executeUpdate();
				if (confirma == 1) {
					JOptionPane.showMessageDialog(null, "Aluno cadastrado com sucesso!");
					reset();
				} else {
					JOptionPane.showMessageDialog(null, "Erro! Aluno não cadastrado corretamente.");
				}
			} catch (SQLException sql) {
				System.out.println("SQLException gerada: [" + sql + "]");
			} catch (Exception e) {
				System.out.println("Exception gerada: [" + e + "]");
			}
		}
	}

	private void atualizar() {
		if (txtNome.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Atenção! Campo nome Obrigatório Favor Preencher.");
			txtNome.requestFocus();
		} else {
			if (fotoCarregada == true) {
				String update = "update alunos set nome=?, foto=? where ra=?";
				try (Connection con = dao.conectar()) {
					pst = con.prepareStatement(update);
					pst.setString(1, txtNome.getText());
					pst.setBlob(2, fis, tamanho);
					pst.setString(3, txtRA.getText());
					int confirma = pst.executeUpdate();
					if (confirma == 1) {
						JOptionPane.showMessageDialog(null, "Aluno Atualizado com sucesso!");
						reset();
					} else {
						JOptionPane.showMessageDialog(null, "Erro! Aluno não Atualizado corretamente.");
					}
				} catch (SQLException sql) {
					System.out.println("SQLException gerada: [" + sql + "]");
				} catch (Exception e) {
					System.out.println("Exception gerada: [" + e + "]");
				}
			} else {
				String update = "update alunos set nome=? where ra=?";
				try (Connection con = dao.conectar()) {
					pst = con.prepareStatement(update);
					pst.setString(1, txtNome.getText());
					pst.setString(2, txtRA.getText());
					int confirma = pst.executeUpdate();
					if (confirma == 1) {
						JOptionPane.showMessageDialog(null, "Aluno Atualizado com sucesso!");
						reset();
					} else {
						JOptionPane.showMessageDialog(null, "Erro! Aluno não Atualizado corretamente.");
					}
				} catch (SQLException sql) {
					System.out.println("SQLException gerada: [" + sql + "]");
				} catch (Exception e) {
					System.out.println("Exception gerada: [" + e + "]");
				}
			}
		}
	}

	private void gerarPdf() {
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream("Alunos.pdf"));
			document.open();
			Date data = new Date();
			DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL);
			document.add(new Paragraph(formatador.format(data)));
			document.add(new Paragraph("Listagem de Alunos: "));
			document.add(new Paragraph(" "));
			PdfPTable tabela = new PdfPTable(3);
			PdfPCell coluna1 = new PdfPCell(new Paragraph("RA"));
			tabela.addCell(coluna1);
			PdfPCell coluna2 = new PdfPCell(new Paragraph("Nome"));
			tabela.addCell(coluna2);
			PdfPCell coluna3 = new PdfPCell(new Paragraph("Fotos"));
			tabela.addCell(coluna3);
			String readLista = "select * from alunos order by nome";
			try (Connection con = dao.conectar()) {
				pst = con.prepareStatement(readLista);
				rs = pst.executeQuery();
				while (rs.next()) {
					tabela.addCell(rs.getString(1));
					tabela.addCell(rs.getString(2));
					Blob blob = (Blob) rs.getBlob(3);
					byte[] img = blob.getBytes(1, (int) blob.length());
					com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(img);
					tabela.addCell(image);
				}
				document.add(tabela);
			} catch (Exception ex) {
				System.out.println("Exception gerada: [" + ex + "]");
			}
		} catch (Exception ex2) {
			System.out.println("Exception gerada: [" + ex2 + "]");
		} finally {
			document.close();
		}

		try {
			Desktop.getDesktop().open(new File("Alunos.pdf"));
		} catch (Exception ex3) {
			System.out.println("Exception gerada: [" + ex3 + "]");
		}
	}

	private void reset() {
		scrollPaneLista.setVisible(false);
		txtRA.setText(null);
		txtNome.setText(null);
		lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/photo.png")));
		txtNome.requestFocus();
		fotoCarregada = false;
		tamanho = 0;
		txtRA.setEditable(true);
		btnBuscar.setEnabled(true);
		btnCarregar.setEnabled(false);
		btnAdicionar.setEnabled(false);
		btnAtualizar.setEnabled(false);
		btnExcluir.setEnabled(false);
		btnPdf.setEnabled(true);
	}

	private void excluir() {
		int confirmaExcluir = JOptionPane.showConfirmDialog(null, "Confirma a exclusão deste aluno?", "Atenção!",
				JOptionPane.YES_NO_OPTION);
		if (confirmaExcluir == JOptionPane.YES_OPTION) {
			String delete = "delete from alunos where ra=?";
			try (Connection con = dao.conectar()) {
				pst = con.prepareStatement(delete);
				pst.setString(1, txtRA.getText());
				int confirmar = pst.executeUpdate();
				if (confirmar == 1) {
					reset();
					JOptionPane.showMessageDialog(null, "Aluno excluido com sucesso !");
				}
			} catch (Exception ex) {
				System.out.println("Excption gerada: [" + ex + "]");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Operação cancelada!");
		}
	}

	private void listarNomes() {
		DefaultListModel<String> modelo = new DefaultListModel<>();
		listNomes.setModel(modelo);
		String readLista = "select * from alunos where nome like '" + txtNome.getText() + "%' order by nome";
		try (Connection con = dao.conectar()) {
			pst = con.prepareStatement(readLista);
			rs = pst.executeQuery();
			while (rs.next()) {
				scrollPaneLista.setVisible(true);
				modelo.addElement(rs.getString(2));
				if (txtNome.getText().isEmpty()) {
					scrollPaneLista.setVisible(false);
				}
			}
		} catch (Exception ex) {
			System.out.println("Exception gerada: [" + ex + "]");
		}
	}

	private void buscarNomes() {
		int linha = listNomes.getSelectedIndex();
		if (linha >= 0) {
			String readNome = "select * from alunos where nome like '" + txtNome.getText() + "%'"
					+ "order by nome limit " + (linha) + ", 1";
			try (Connection con = dao.conectar()) {
				pst = con.prepareStatement(readNome);
				rs = pst.executeQuery();
				while (rs.next()) {
					scrollPaneLista.setVisible(false);
					txtRA.setText(rs.getString(1));
					txtNome.setText(rs.getString(2));
					txtNome.setText(rs.getString(2));
					Blob blob = (Blob) rs.getBlob(3);
					byte[] img = blob.getBytes(1, (int) blob.length());
					BufferedImage image = null;
					try {
						image = ImageIO.read(new ByteArrayInputStream(img));
					} catch (Exception e) {
						System.out.println("Exception gerada: [" + e + "]");
					}
					ImageIcon icone = new ImageIcon(image);
					Icon foto = new ImageIcon(icone.getImage().getScaledInstance(lblFoto.getWidth(),
							lblFoto.getHeight(), Image.SCALE_SMOOTH));
					lblFoto.setIcon(foto);
					lblFoto.setIcon(foto);
					txtRA.setEditable(false);
					btnBuscar.setEnabled(false);
					btnCarregar.setEnabled(true);
					btnAtualizar.setEnabled(true);
					btnExcluir.setEnabled(true);
					btnPdf.setEnabled(false);
				}
			} catch (Exception ex) {
				System.out.println("Exception gerada: [" + ex + "]");
			}
		}
	}

	private void buscarRA() {
		String message = "Não foi encontrado nenhum aluno no registro\nDeseja Fazer um novo registro?";
		if (txtRA.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Por favor! Preencher o campo Obrigatório RA.");
			txtRA.requestFocus();
		} else {
			String readRA = "select * from alunos where ra = ?";
			try (Connection con = dao.conectar()) {
				pst = con.prepareStatement(readRA);
				pst.setString(1, txtRA.getText());
				rs = pst.executeQuery();
				if (rs.next()) {
					txtNome.setText(rs.getString(2));
					Blob blob = (Blob) rs.getBlob(3);
					byte[] img = blob.getBytes(1, (int) blob.length());
					BufferedImage image = null;
					try {
						image = ImageIO.read(new ByteArrayInputStream(img));
					} catch (Exception ex) {
						System.out.println("Exception gerada: [" + ex + "]");
					}
					ImageIcon icone = new ImageIcon(image);
					Icon foto = new ImageIcon(icone.getImage().getScaledInstance(lblFoto.getWidth(),
							lblFoto.getHeight(), Image.SCALE_SMOOTH));
					lblFoto.setIcon(foto);
					txtRA.setEditable(false);
					btnBuscar.setEnabled(false);
					btnCarregar.setEnabled(true);
					btnAtualizar.setEnabled(true);
					btnExcluir.setEnabled(true);
					btnPdf.setEnabled(false);
				} else {
					int confirma = JOptionPane.showConfirmDialog(null, message, "Aviso", JOptionPane.YES_OPTION);
					if (confirma == JOptionPane.YES_OPTION) {
						txtRA.setEditable(false);
						txtRA.setText(null);
						btnBuscar.setEnabled(false);
						txtNome.setText(null);
						txtNome.requestFocus();
						btnCarregar.setEnabled(true);
						btnAdicionar.setEnabled(true);
					} else {
						reset();
					}
				}
			} catch (SQLException sql) {
				System.out.println("SQLException gerada: [" + sql + "]");
			} catch (Exception e) {
				System.out.println("Exception gerada: [" + e + "]");
			}
		}
	}
}
