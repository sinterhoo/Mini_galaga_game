import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.image.*;

// 1) �������������� �ѽð����� ������ �ű�� ������ (�ְ������� ���)

public class MainGame {
	JFrame frame=new JFrame();				// ��ü GUI�� ���� �����ӿ� ���� ���۷���
	private final int S_MARGIN = 20;  		// �׸��� �� ������ ������ �� �浹�� ������ �������� ��(���� �׸�)
	private final int B_MARGIN = 50;  		// �׸��� �� ������ ������ �� �浹�� ������ �������� ��(ū �׸�)
	private final int WIN_WIDTH = 660; 		// ��ü frame�� ��
	private final int WIN_HEIGHT = 700; 	// ��ü frame�� ����
	private final int NEW_ATTACKER_INTERVAL= 1;	// ���� �����ڰ� ��Ÿ���� �ֱ�
	private final int BIG_ATTACKER_INTERVAL= 7;	// ū �����ڰ� ��Ÿ���� �ֱ� (��� ���)
	private final int SPEED = 50;			// �ִϸ��̼��� �ӵ� (�и���)
	private final int STEPS = 10;			// �׸� ��ü���� �ѹ��� �����̴� �Ƚ� ��
	// ��ư ����� ���� ��Ʈ ���꿡 ���� �����
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	// ����� ������ �� �÷��̾� �׸� �� ����
	// src ������ ��Ʈ "/"�� �νĵǹǷ� ��Ʈ���� ��θ��� ��
	private final String ATTACKER_PIC = "/8.PNG";
	private final String BIG_ATTACKER_PIC = "/9.PNG";
	private final String PLAYER_PIC = "/2.PNG";
	private final String MAIN_PIC = "/main1.jpg";
	private final String Missle_PIC = "/3.PNG";
	private final String START_SOUND = "/start.wav";
	private final String BOOM_SOUND = "/boom.wav";
	private final String SMALLHIT_SOUND = "/smallhit.wav";
	private final String BIGHIT_SOUND = "/bighit.wav";
	private final String SHOT_SOUND = "/shot.wav";
	private final String END_MUSIC = "/endmusic.wav";
	private final String END_MAIN_PIC = "/endmain.gif";

	int gamePanelWidth, gamePanelHeight;	// ���� ������ �̷���� ������ ũ�� 
	JPanel controlPanel=new JPanel();		// ���� ��Ʈ�Ѱ� �ð�, ����� ���÷��̰� �� �г�
	JButton start=new JButton("����");		// ���۹�ư
	JButton end=new JButton("����");			// �����ư
	JButton suspend=new JButton("�Ͻ�����");	// �Ͻ����� ��ư
	JButton cont=new JButton("���");			// ��� ��ư
	JLabel timing=new JLabel("�ð�  : 0�� 0��");// ���Ӱ�� �ð� ���÷��̸� ���� ��
	JLabel scoreLabel = new JLabel(" ���� : 0��"); // �ǽð� ����ȹ�� ���÷��̸� ���� ��
	JLabel endLabel = new JLabel();			// ���� ȹ�� ������ �гο� ���� ���� ��
	JLayeredPane lp = new JLayeredPane();	// ȭ���� ������ ��ġ�� ���� Panel ���̾�
	JPanel coverPanel;						// �ʱ�ȭ���� ��Ÿ�� �г�	
	GamePanel gamePanel;					// ������ �̷��� �г�
	EndPanel endPanel;						// ������ ������ ��Ÿ�� �г�
	Timer goAnime;							// �׷��� ��ü�� �������� �����ϱ� ���� Ÿ�̸�
	Timer goClock;							// �ð豸���� ���� ���� Ÿ�̸�
	ClockListener clockListener;			// �ð踦 �����ϱ� ���� ������
	ArrayList<Shape> attackerList;			// ���ӿ� ���Ǵ� ���� ������ ��ü�� ��� ����Ʈ
	ArrayList<Shape> bigAttackerList;		// ���ӿ� ���Ǵ� ū ������ ��ü�� ��� ����Ʈ
	ArrayList<Missle> missleList = new ArrayList<>();			// ���ӿ� ���Ǵ� �̻��� ��ü�� ��� ����Ʈ
	Shape player;							// Ű����� �����̴� Player ��ü
	DirectionListener keyListener;			// ȭ��ǥ �������� �����ϴ� ������
	private AudioClip backgroundSound;		// ���� ��� ����
	private AudioClip boomSound;			// �浹����
	private AudioClip bighitsound;			// ū �� �̻��� �ǰ�����
	private AudioClip smallhitsound;		// ���� �� �̻��� �ǰ�����
	private AudioClip shotsound;			// �̻��� �߻�����
	private AudioClip endsound;
	static String playerName;				// �÷��̾� �̸�
	int score =0;							// ����
	Shape endMain2 = new Shape(getClass().getResource(END_MAIN_PIC)); // �������� �г��� �׸� ����� ���� Shape �� ���� ����
	
	public static void main(String [] args) {
		playerName=JOptionPane.showInputDialog("�̸��� �Է����ּ��� :");	// Player�� �̸� �Է�
		new MainGame().go();									// ������  �ʱ�ȭ
	}
	

	public void go() {
		//GUI����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// ���� ���� ��ư �� ���÷��� �󺧵��� �� �г�
		controlPanel.add(start);
		controlPanel.add(suspend);
		controlPanel.add(cont);
		controlPanel.add(end);
		controlPanel.add(timing);
		controlPanel.add(new JLabel(" Player : "));
		controlPanel.add(new JLabel(playerName));
		controlPanel.add(scoreLabel);
		
		// ������ ������ ���÷��� �� �г�
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// �ʱ�ȭ���� ���� �г�
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		
		// ������ ���� �� ��Ÿ�� �г�
		endPanel = new EndPanel();
		endPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		endPanel.add(endLabel);
		
		// �ʱ�ȭ��� ����ȭ���� ���̾�ȭ ��
		lp.add(gamePanel, new Integer(0));
		lp.add(coverPanel, new Integer(1));
		lp.add(endPanel,new Integer(2));
		
		endPanel.setVisible(false); //������ ���� �� ��Ÿ�����ϱ� ������ �ϴ� �Ⱥ��̰� ����
		
		// ��ü �����ӿ� ��ġ
		frame.add(lp);
		frame.add(BorderLayout.CENTER, lp);
		frame.add(BorderLayout.SOUTH, controlPanel);
		
		// ������ �̷���� �г��� ���� ���� ���� ���
		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -130;

		//��µ� ��ü���� ���� (���)�Ͽ� attackerList�� �־� ��
		prepareAttackers();
		
		// Ű����� ������ player ��ü ����
		player = new Shape(getClass().getResource(PLAYER_PIC), B_MARGIN, gamePanelWidth, gamePanelHeight);
		
		// �ð� ���÷���, ��ü�� �������� �ڵ�ȭ �ϱ� ���� Ÿ�̸ӵ� 
		clockListener = new ClockListener();
		goClock = new Timer(1000, clockListener);			// �ð��� �ʴ����� ��Ÿ���� ���� ������
		goAnime = new Timer(SPEED, new AnimeListener());	// �׸��� �̵��� ó���ϱ� ���� ������

		// Player�� Ű���� �������� ���� ��û��
		gamePanel.addKeyListener(new DirectionListener());	// Ű���� ������ ��ġ
		gamePanel.setFocusable(false);						// �ʱ⿡�� ��Ű�� �ȵǰ� ��(�� Ű �ȸ���)

		// ��ư  �������� ��ġ
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		// ������ ���� ���� ���� ��ġ
		try {
			// backgroundSound = JApplet.newAudioClip(new URL("file", "localhost","/res/start.wav"));
			// boomSound = JApplet.newAudioClip(new URL("file", "localhost","/res/boom.wav"));
			// ���� ����� ����θ� ��Ÿ���� ���ϴ� ����̾, jar���Ϸ� �������� ���鶧 ��θ� ã�� ���ϴ�
			// ������ ����. ���� getClass()�� ����Ͽ� ������� URL�� ���ϴ� ����� �Ʒ�ó�� ����ؾ� ��
			// ���⿡�� root�� �Ǵ� ������ ���� �� ���α׷��� ����Ǵ� ���̴� ���� ������ �־��־�� ��
			backgroundSound = JApplet.newAudioClip(getClass().getResource(START_SOUND));
			boomSound = JApplet.newAudioClip(getClass().getResource(BOOM_SOUND));
			bighitsound = JApplet.newAudioClip(getClass().getResource(BIGHIT_SOUND));
			smallhitsound = JApplet.newAudioClip(getClass().getResource(SMALLHIT_SOUND));
			shotsound = JApplet.newAudioClip(getClass().getResource(SHOT_SOUND));
			endsound = JApplet.newAudioClip(getClass().getResource(END_MUSIC));
		}
		catch(Exception e){
			System.out.println("���� ���� �ε� ����");
		}
		
		// ȭ���� Ȱ��ȭ
		buttonToggler(START);	// �ʱ⿡�� start��ư�� �� Ȱ��ȭ
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);
	}
	 
	// ���� �Լ���

	// ��ư�� Ȱ�� ��Ȱ��ȭ�� ���� ��ƾ
	private void buttonToggler(int flags) {
		if ((flags & START) != 0)
			start.setEnabled(true);
		else
			start.setEnabled(false);
		if ((flags & SUSPEND) != 0)
			suspend.setEnabled(true);
		else
			suspend.setEnabled(false);
		if ((flags & CONT) != 0)
			cont.setEnabled(true);
		else
			cont.setEnabled(false);
		if ((flags & END) != 0)
			end.setEnabled(true);
		else
			end.setEnabled(false);
	}
	
	// ������ ���ۿ� ���� �����ڵ�
	private void prepareAttackers() {
		bigAttackerList = new ArrayList<Shape>();		// ū �������� ����Ʈ�� ó������ ���
		attackerList = new ArrayList<Shape>();			// ������ 6���� ����
		attackerList.add(new DiagonallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new VerticallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new DiagonallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new VerticallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
	}
	
	// �̻��� �߻翡 ���� �޼ҵ�
	private void prepareMissle() {
		missleList.add(new Missle(player.x,player.y));
	}
	
	// ������ ����� ó���ؾ� �� ����
	private void finishGame() {
		endsound.play();
		endLabel.setText("<html><br><br><br><br><br>"+"���� ����! ���� : "+score+"</html>"); //�гο� ��Ÿ�� ��������
		endLabel.setFont(endLabel.getFont().deriveFont(40.0f)); // �� ���� ũ�� ����
		endLabel.setHorizontalAlignment(endLabel.CENTER);		// �� ���� ��ġ ����
		endLabel.setForeground(Color.white);// �� ���� �� ����
		endPanel.setVisible(true);			// ������ ����Ǹ� ��� �г��� ���̰� ��
		backgroundSound.stop();				// ���� ����
		goClock.stop();						// �ð� ���ÿ��� ����
		goAnime.stop();						// �׸���ü ������ ����
		gamePanel.setFocusable(false);		// ��Ŀ�� �ȵǰ� ��(�� Ű �ȸ���)
		buttonToggler(START);				// Ȱ��ȭ ��ư�� ����

		
	}
	
	// ���������� �������� �������� �߻���Ű�� ���ݰ�ü�� ����
	private Shape getRandomAttacker(String pic, int margin, int steps) {
		int rand = (int)(Math.random() * 3) + 1;
		Shape newAttacker;
		switch (rand) {
		case 1 :
			newAttacker =  new DiagonallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
			break;
		case 2 :
			newAttacker =  new HorizontallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
			break;
		case 3 :
			newAttacker =  new VerticallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
			break;
		default :	
			newAttacker =  new DiagonallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
		}
		return newAttacker;
	}
	
	// ���� Ŭ���� ��
	
	
	// goAnime Ÿ�̸ӿ� ���� �ֱ������� ����� ����
	// ��ü�� ������, �浹�� ���� ����
	public class AnimeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// ���� �浹�Ͽ����� �浹�� ȿ���� ��Ÿ���� Ÿ�̸Ӹ� �ߴܽ�Ŵ
			for (Shape s : attackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					boomSound.play();					// �浹�� ����
					finishGame();						// ���� �ߴ�
					return;
				}
				for(Missle k : missleList) { //�̻��ϰ� ���� �浹�ϸ� �����
					if (s.collide(new Point(k.mx,k.my))) {
						attackerList.remove(s);					// �浹�� �� ����
						missleList.remove(k);					// �浹�� �̻��� ����
						score++;								// ���� óġ�ϸ� ����ȹ��
						smallhitsound.play();					// ������ óġ���� �÷���
						scoreLabel.setText("���� : "+score+"��");
						return;
					}
				}
			}
			for (Shape s : bigAttackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					boomSound.play();					// �浹�� ����
					finishGame();						// ���� �ߴ�
					return;
				}
				for(Missle k : missleList) { //�̻��ϰ� ���� �浹�ϸ� �����
					if (s.collide(new Point(k.mx,k.my))) {
						s.hp--;					//�̻��� �Ѱ� �浹���� ū ���� hp�� 1 ���ҽ�Ŵ
						missleList.remove(k);	// �浹�� �̻��� ����
						if(s.hp <=0) {			// ū ���� ü���� 0 ������ ��������
							bigAttackerList.remove(s);
							score = score+3;
							bighitsound.play();
							scoreLabel.setText("���� : "+score+"��");
						}
						return;
					}
				}
				
			}
			// �׸� ��ü���� �̵���Ŵ
			for (Shape s : attackerList) {
				s.move();
			}
			for (Shape s : bigAttackerList) {
				s.move();
			}
			for (Missle s : missleList) {
				s.move();
			}
			frame.repaint();								
		}
	}
	
	// ���� ��ư�� ��û��
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			endsound.stop();
			endPanel.setVisible(false);						// �����ϸ� endPanel�� �Ⱥ��̰� ����
			lp.setLayer(gamePanel, 2);						// gamePanel �� ������ ������ ��
			gamePanel.setFocusable(true);					// gamePanel�� ��Ŀ�̵� �� �ְ� ��
			gamePanel.requestFocus();						// ��Ŀ���� ������(�̰� �ݵ�� �ʿ�)

			backgroundSound.play();							// ������� ����
			goAnime.start();								// �׸���ü �������� ���� ����

			clockListener.reset();							// Ÿ�̸��� ���۰� �ʱ�ȭ
			timing.setText("�ð�  : 0�� 0��");	
			goClock.start();								// �ð� ���÷��� Ÿ�̸ӽ���
			score = 0;										// �ٽ� ���� �ʱ�ȭ
			scoreLabel.setText("���� : "+score+"��");
			
			missleList.clear();								// �̻��� ��ü �ʱ�ȭ

			prepareAttackers();								// �ʱ� ������ �غ�

			buttonToggler(SUSPEND+END);						// Ȱ��ȭ�� ��ư�� ����

		}
	}
	
	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.stop();		
			goAnime.stop();
			gamePanel.setFocusable(false);					// ���� �����ӿ� Ű �ȸ԰� ��
			buttonToggler(CONT+END);						// Ȱ��ȭ ��ư�� ����
		}
	}
	
	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.restart();
			goAnime.restart();
			gamePanel.setFocusable(true);					// ���� ������ Ű �԰� ��
			gamePanel.requestFocus();						// ��ü �����ֿ� ��Ŀ���ؼ� Ű �԰� ��
			buttonToggler(SUSPEND+END);						// Ȱ��ȭ ��ư�� ����
		}
	}

	// �����ư�� ���� ��û��
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			finishGame();
		}
	}

	// ������ ����Ǵ� ���� �г�
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0,0,this.getWidth(), this.getHeight());		// ȭ�� �����
		
			// ���ӿ� ���Ǵ� �׷��� ��ü�� ��� �׷���
			for (Shape s : attackerList) {
				s.draw(g, this);
			}
			for (Shape s : bigAttackerList) {
				s.draw(g, this);
			}
			for(Missle s : missleList) {
					s.draw(g);
			}
			
			player.draw(g, this);	
		}
	}
	
	// ������ ����ǰ� ��Ÿ���� �г�
	class EndPanel extends JPanel{
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			endMain2.draw2(g, this);
		}
	}
	
	// �ʱ�ȭ���� ��Ÿ���� �г�
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 
			g.drawImage(image,0,0,this);
		}
	}
	
	// �ð� ���÷��̸� ���� ����ϴ� �ð�
	private class ClockListener implements ActionListener {
		int times = 0;
		public void actionPerformed (ActionEvent event) {		
			times++;						
			timing.setText("�ð�  : "+times/60+"�� "+times%60+"��");

			// �ð��� �����ð� ������ ���ο� ��⸦ ������Ŵ
			if (times % NEW_ATTACKER_INTERVAL == 0)
				attackerList.add(getRandomAttacker(ATTACKER_PIC, S_MARGIN, STEPS));

			// �ð��� �����ð� ������ bigAttacker ����/�Ҹ� ��Ŵ
			if (times % BIG_ATTACKER_INTERVAL == 0) {
					bigAttackerList.add(getRandomAttacker(BIG_ATTACKER_PIC, B_MARGIN, STEPS));
				
			}
		}
		
		public void reset() {
			times = 0;
		}
		public int getElaspedTime() {
			return times;
		}
	}
	
	// Ű���� �������� ��û�ϴ� ��û��
	class DirectionListener implements KeyListener {
	   public void keyPressed (KeyEvent event) {
		   switch (event.getKeyCode()){
		   case KeyEvent.VK_UP:
			   if (player.y >= 0)
				   player.y -= STEPS;
			   break;
		   case KeyEvent.VK_DOWN:
			   if (player.y <= gamePanelHeight)
				   player.y += STEPS;
			   break;
		   case KeyEvent.VK_LEFT:
			   if (player.x >= 0)
				   player.x -= STEPS;
			   break;
		   case KeyEvent.VK_RIGHT:
			   if (player.x <= gamePanelWidth)
				   player.x += STEPS;
			   break;
		   }
	   }
	   public void keyTyped (KeyEvent event) {}
	   
	   // �����̽��� ������ ���� �̻��� ��ü�� �ϳ� ������
	   public void keyReleased (KeyEvent event) {
		   switch(event.getKeyCode()) {
		   case KeyEvent.VK_SPACE:
			   prepareMissle();
			   shotsound.play();
			   break;
		   }
			   
	   }
   }
}