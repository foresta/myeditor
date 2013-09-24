import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.KeyStroke;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Window;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;

public class MemoMain extends JFrame implements DocumentListener,MouseListener,WindowListener{
	
	private int tabCount = 0; //タブの個数
	private JPanel	 		panel[]			= new JPanel[10]; 
	private JScrollPane 	scroll[] 		= new JScrollPane[10];
	private JTextPane 		text[] 			= new JTextPane[10];
	private JLabel			label[]			= new JLabel[10];
	private String			name[]			= new String[10]; 
	private JButton 		button[] 		= new JButton[10];
	private File 			file[]			= new File[10];
	private boolean			editFlag[]		= new boolean[10];
	
	ImageIcon closeIcon;
	
	private JLabel statusBar = new JLabel("MemoRandamへようこそ！");
	
	private JFileChooser fileChooser; 
	private static final int WINDOW_WIDTH = 700; //画面横幅
	private static final int WINDOW_HEIGHT = 500; //画面縦幅
	
	private JTabbedPane tab = new JTabbedPane();
	
		
	//ファイルメニュー
	private JMenuItem saveAsMenuItem;	//保存
	private JMenuItem saveMenuItem; 	//上書き保存
	private JMenuItem openMenuItem; 	//開く
	private JMenuItem newMenuItem;	 	//新規
	private JMenuItem closeMenuItem; 	// 閉じる
	private JMenuItem exitMenuItem; 	//メモの終了
	

	/*
	 * メインメソッド
	 */
	public static void main(String arg[]){
		MemoMain frame = new MemoMain("MemoRandam( . . )φ");
		frame.setVisible(true);
		frame.newFile(); //一つ目のタブ生成
	}
	
	/*
	 * コンストラクタ
	 */
	public MemoMain(String title){
		setTitle(title);  			//タイトルをセットする
		setBounds(100,50,700,500); 	//ウィンドウの設定

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //×ボタンが押された時の設定
														//ウィンドウが閉じるとプログラム終了
		
		fileChooser = new JFileChooser(); //ファイルの保存/開くを管理するインスタンス生成
		JMenuBar menuBar = new JMenuBar(); //メニューバーの設定
		
		try{
			closeIcon = new ImageIcon(new URL(getClass().getResource("close.GIF"),"close.GIF"));
		}catch(MalformedURLException e){
			e.printStackTrace();
		}

		////////////////////ファイルメニュー///////////////////////////////
		
		JMenu fileMenu = new JMenu("ファイル(F)"); //"File"メニューの作成
		
				
		saveAsMenuItem  = new JMenuItem("名前をつけて保存(A)...");
		saveMenuItem	= new JMenuItem("上書き保存(S)");
		openMenuItem	= new JMenuItem("開く(O)...");
		closeMenuItem 	= new JMenuItem("タブを閉じる(C)");
		exitMenuItem  	= new JMenuItem("MemoRandamの終了(X)");
		newMenuItem   	= new JMenuItem("新規作成(N)");
		
		/*
		 * ニーモニック設定
		 * */
		fileMenu.setMnemonic('f'); //Alt＋fで開けるように設定
		saveAsMenuItem.setMnemonic('a');
		saveMenuItem.setMnemonic('s');
		openMenuItem.setMnemonic('o');
		closeMenuItem.setMnemonic('c');
		exitMenuItem.setMnemonic('x');
		newMenuItem.setMnemonic('n');
		
		/*
		 * ショートカットキー設定
		 */
		saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.ALT_MASK));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK));
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,InputEvent.CTRL_MASK));
		/*
		 * 名前をつけて保存の処理
		 */
		saveAsMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//タブが0個でなかったら
				if(tab.getTabCount()!=0){
					saveAs();
					statusBar.setText("保存しました。");	
				}
				//タブが0だったら
				else{
					JOptionPane.showMessageDialog(getContentPane(),
							"タブがありません！",
							"警告",
							//JOptionPane.OK_OPTION,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		saveAsMenuItem.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				statusBar.setText("名前をつけて保存を行います。");
			}
		});
		
		
		/*
		 * 上書き保存の処理
		 */
		saveMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//タブが0個でなかったら
				if(tab.getTabCount()!=0){
					save();
					statusBar.setText("保存しました。");	
				}
				//タブが0だったら
				else{
					JOptionPane.showMessageDialog(getContentPane(),
							"タブがありません！",
							"警告",
							//JOptionPane.OK_OPTION,
							JOptionPane.ERROR_MESSAGE);
				}
				
				
			}
		});
		saveMenuItem.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				statusBar.setText("上書き保存を行います。ファイルが作られていなければ、名前をつけて保存します。");
			}
		});
		
		
		/*
		 * 開くの処理
		 */
		openMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(tab.getTabCount()>=10){
					//警告ダイアログを出す
					JOptionPane.showMessageDialog(
							getContentPane(),
							"タブは10個以内でお願いします",
							"メッセージ",
							JOptionPane.PLAIN_MESSAGE
							);
				}
				else{
					load();
					
				}
			}
		});
		openMenuItem.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				statusBar.setText("新しくタブを作り、ファイルを開きます。");
			}
		});
		
		/*
		 * 新規作成の処理
		 */
		newMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(tab.getTabCount()>=10){
					//警告ダイアログを出す
					JOptionPane.showMessageDialog(
							getContentPane(),
							"タブは10個以内でお願いします",
							"メッセージ",
							JOptionPane.PLAIN_MESSAGE
							);
				}
				else{
					newFile();
					statusBar.setText("新規作成しました。");
				}
			}
		});
		newMenuItem.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				statusBar.setText("新しいタブを作ります。");
			}
		});
		
		/*
		 * タブを閉じるの処理
		 */
		closeMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//タブが0個でなかったら
				if(tab.getTabCount()!=0){
					tabClose(e.getSource());
					statusBar.setText("タブを閉じました。");
				}
				//タブが0だったら
				else{
					JOptionPane.showMessageDialog(getContentPane(),
							"タブがありません！",
							"警告",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		closeMenuItem.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				statusBar.setText("選択中のタブを閉じます。");
			}
		});
		
		/*
		 * MemoRandamの終了の処理
		 */
		exitMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				close();
			}
		});
		exitMenuItem.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				statusBar.setText("MemoRandamのプログラムを終了します。");
			}
		});
		
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.addSeparator(); //区切り
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);
		fileMenu.add(exitMenuItem);

		/////////////////////////////////////////////////////
		
		menuBar.add(fileMenu);
		
		setJMenuBar(menuBar); //メニューバーの設置
		getContentPane().add(tab);
		getContentPane().add(statusBar,BorderLayout.SOUTH);
		this.addWindowListener(this);
		

	}
	
	//新規作成を行うメソッド
	private void newFile(){
				
		file[tabCount] = null;
		panel[tabCount] = new JPanel();
		button[tabCount] = new JButton();
		text[tabCount] = new JTextPane();
		text[tabCount].getDocument().addDocumentListener(this);
		scroll[tabCount] = new JScrollPane(text[tabCount]);
		scroll[tabCount].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll[tabCount].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll[tabCount].setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
		tab.addTab("no title",scroll[tabCount]);
		name[tabCount] = "no title";
		label[tabCount] = new JLabel(name[tabCount]);
		button[tabCount] = new JButton(closeIcon);
		button[tabCount].setPreferredSize(new Dimension(16,16));
				
		button[tabCount].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//タブが0個でなかったら
				if(tab.getTabCount()!=0){
					tabClose(e.getSource());
					statusBar.setText("タブを閉じました。");
				}
				//タブが0だったら
				else{
					JOptionPane.showConfirmDialog(getContentPane(),
							"タブがありません！",
							"警告",
							JOptionPane.OK_OPTION,
							JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		
		panel[tabCount].setBackground(new Color(215,215,255));
		panel[tabCount].add(label[tabCount], BorderLayout.WEST);
		panel[tabCount].add(button[tabCount],BorderLayout.EAST);
		
		tab.setTabComponentAt(tabCount, panel[tabCount]);
		tab.setSelectedIndex(tabCount);
		System.out.println((tabCount+1)+"個めのタブを作成"+tabCount);
		editFlag[tabCount] = false;
		
		tabCount++;
		
	}
	
	//上書き保存を行うメソッド
	private void save(){
		statusBar.setText("保存中...");
		int index = tab.getSelectedIndex();
		if(file[index] == null){ //まだ保存されてなかったら
			saveAs();
		}
		else{
			//上書き保存の処理
			try{
				System.out.println("上書き保存");
				FileWriter fileWriter = new FileWriter(file[index]);
				fileWriter.write(text[index].getText());
				fileWriter.close();
				name[index] = file[index].getName();
				label[index].setText(file[index].getName());
			}catch(Exception e){
				e.printStackTrace();
			}
			editFlag[index] = false;
		}
		
		
	}
	
	//名前をつけて保存を行うメソッド
	private void saveAs(){
		statusBar.setText("保存中...");
		int index = tab.getSelectedIndex();
		if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
			file[index] = fileChooser.getSelectedFile();
			try{
				FileWriter fileWriter = new FileWriter(file[index]);
				
				fileWriter.write(text[index].getText());
				fileWriter.close();
				panel[index] = new JPanel();
				panel[index].setBackground(new Color(215,215,255));
				name[index] = fileChooser.getName(file[index]);
				label[index].setText(fileChooser.getName(file[index]));
				panel[index].add(label[index], BorderLayout.WEST);
				panel[index].add(button[index],BorderLayout.EAST);
				tab.setTabComponentAt(index, panel[index]);
				
			}catch(Exception e){
				e.printStackTrace();
			}
			editFlag[index] = false;
		}
		
	}
	
	//ファイルを開くメソッド
	private void load(){

		statusBar.setText("ファイルを開いています...");

		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			newFile();
			int index = tab.getSelectedIndex();
			file[index] = fileChooser.getSelectedFile();
			try{
				FileReader fileReader = new FileReader(file[index]);
		
				text[index].read(fileReader,null);
				text[index].getDocument().addDocumentListener(this);
				
				panel[index] = new JPanel();
				panel[index].setBackground(new Color(215,215,255));
				name[index] = fileChooser.getName(file[index]);
				label[index].setText(fileChooser.getName(file[index]));
				panel[index].add(label[index], BorderLayout.WEST);
				panel[index].add(button[index],BorderLayout.EAST);
				
				tab.setTabComponentAt(index,panel[index]);
				tab.setSelectedIndex(tabCount-1);
				editFlag[index] = false;
				fileReader.close();
				statusBar.setText("ファイルを開きました。");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	/*
	 * タブを閉じるメソッド
	 * arg : 押されたボタンのオブジェクト
	 */
	public void tabClose(Object object){
				
		statusBar.setText("タブを閉じています...");
		if(object instanceof JButton){
			
			int offset=0;
			for(int i=0;i<10;i++){
 			
				if(object == button[i]){
					if(editFlag[i] == true){ //保存されてなかったら
						int ans=JOptionPane.showConfirmDialog(
								getContentPane(),
								"保存されていません。保存してから閉じますか？",
								"タブを閉じようとしています。",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE
								);
						//警告ダイアログを出す
						if( ans	== JOptionPane.YES_OPTION){
							//保存する場合の処理
							save();
						}
						else if( ans == JOptionPane.CANCEL_OPTION){
							return;
						}
					}		
					tab.remove(i);
					offset = i;
					System.out.println((i+1)+"個めのタブを消去");
				}
			}
			tabSort(offset);
			
		}
		
		else if(object instanceof JMenuItem){
			int tmp = tab.getSelectedIndex();
			if(object == closeMenuItem){
				if(editFlag[tmp] == true){ //保存されてなかったら
					int ans=JOptionPane.showConfirmDialog(
							getContentPane(),
							"保存されていません。保存してから閉じますか？",
							"タブを閉じようとしています。",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE
							);
				
					if( ans	== JOptionPane.YES_OPTION){
						//保存する場合の処理
						save();
					}
					else if( ans == JOptionPane.CANCEL_OPTION){
						return;
					}
				}
				tab.remove(tmp);
				System.out.println((tmp+1)+"個めのタブを消去");
			}
			tabSort(tmp);
		}
			
		tabCount--;
		
	}
	/*
	 * タブを閉じた後ソートするメソッド
	 * arg : 閉じたタブのインデックス
	 */
	public void tabSort(int index){
		while(index < 9){
			scroll[index] = scroll[index+1];
			panel[index] = panel[index+1];
			text[index] = text[index+1];
			button[index] = button[index+1];
			file[index] = file[index+1];
			label[index] = label[index+1];
			name[index] = name[index+1];
			editFlag[index] = editFlag[index+1];
			index++;
		}
		int count=0;
		for(int i=0;i<10;i++){
			if(scroll[i]==null){
				count++;
			}
		}
		System.out.println("タブの数："+(10-count));
	}
	
	/*
	 * テキストが挿入されたときの処理
	 * */
	public void insertUpdate(DocumentEvent e){
		statusBar.setText("編集中...");
		editFlag[tab.getSelectedIndex()] = true;
		if(file[tab.getSelectedIndex()] != null){
			if(name[tab.getSelectedIndex()].equals( file[tab.getSelectedIndex()].getName()) ){
				label[tab.getSelectedIndex()].setText("*"+label[tab.getSelectedIndex()].getText());
				name[tab.getSelectedIndex()] = label[tab.getSelectedIndex()].getText();
			}
		}
		else if(file[tab.getSelectedIndex()] == null){
			if(name[tab.getSelectedIndex()].equals("no title")){
				label[tab.getSelectedIndex()].setText("*"+label[tab.getSelectedIndex()].getText());
				name[tab.getSelectedIndex()] = label[tab.getSelectedIndex()].getText();
			}
		}
	}
	
	/*
	 * テキストが削除されたときの処理
	 * */
	public void removeUpdate(DocumentEvent e){
		statusBar.setText("編集中...");
		editFlag[tab.getSelectedIndex()] = true;
		if(file[tab.getSelectedIndex()] != null){
			if(name[tab.getSelectedIndex()].equals( file[tab.getSelectedIndex()].getName()) ){
				label[tab.getSelectedIndex()].setText("*"+label[tab.getSelectedIndex()].getText());
				name[tab.getSelectedIndex()] = label[tab.getSelectedIndex()].getText();
			}
		}
		else if(file[tab.getSelectedIndex()] == null){
			if(name[tab.getSelectedIndex()].equals("no title")){
				label[tab.getSelectedIndex()].setText("*"+label[tab.getSelectedIndex()].getText());
				name[tab.getSelectedIndex()] = label[tab.getSelectedIndex()].getText();
			}
		}
	}
	
	public void changedUpdate(DocumentEvent e){
	}
	
	public void mouseClicked(MouseEvent e){
	}
	
	public void mousePressed(MouseEvent e){
	}
	
	public void mouseReleased(MouseEvent e){
	}
	
	public void mouseEntered(MouseEvent e){
	}
	
	public void mouseExited(MouseEvent e){
	}
	
	//ウィンドウが開かれた時の処理
	public void windowOpened(WindowEvent e){
	}
	//ウィンドウが閉じられる前に実行する処理
	public void windowClosing(WindowEvent e){
		close();		
	}
	//ウィンドウが閉じた後に実行する処理
	public void windowClosed(WindowEvent e){
	}
	//最小化されたときの処理
	public void windowIconified(WindowEvent e){
	}
	//最小化から元に戻った時の処理
	public void windowDeiconified(WindowEvent e){
	}
	//ウィンドウがアクティブ状態に設定されたときの処理
	public void windowActivated(WindowEvent e){
	}
	//ウィンドウがアクティブ状態でなくなった時の処理
	public void windowDeactivated(WindowEvent e){
	}
	
	public void close(){
		int editCount=0;
		int res = 100;
		for(int i=0;i<10;i++){
			if(editFlag[i]){
				editCount++;
			}
		}
		if(editCount!=0){
			res = JOptionPane.showConfirmDialog
					(getContentPane(),
					"保存してから終了しますか？",
					"保存されていないタブがあります",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE					
					);
		}
		else{ //editCount==0
			System.exit(EXIT_ON_CLOSE);
		}
		if(res == JOptionPane.YES_OPTION){
			for(int i=0;i<10;i++){
				if(editFlag[i]){
					tab.setSelectedIndex(i);
					save();
				}
			}
			System.exit(EXIT_ON_CLOSE);
		}
		else if(res == JOptionPane.NO_OPTION){
			System.exit(EXIT_ON_CLOSE);
		}
		else if(res == JOptionPane.CANCEL_OPTION){
			
		}
		else if(res == JOptionPane.CLOSED_OPTION){
			
		}
	}
}