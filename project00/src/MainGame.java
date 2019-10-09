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

// 1) 맞춰질때까지의 총시간으로 점수를 매기면 좋겠음 (최고점수자 등록)

public class MainGame {
	JFrame frame=new JFrame();				// 전체 GUI를 담을 프레임에 대한 레퍼런스
	private final int S_MARGIN = 20;  		// 그림의 얼마 범위에 들어왔을 때 충돌로 결정할 것인지의 값(작은 그림)
	private final int B_MARGIN = 50;  		// 그림의 얼마 범위에 들어왔을 때 충돌로 결정할 것인지의 값(큰 그림)
	private final int WIN_WIDTH = 660; 		// 전체 frame의 폭
	private final int WIN_HEIGHT = 700; 	// 전체 frame의 높이
	private final int NEW_ATTACKER_INTERVAL= 1;	// 작은 공격자가 나타나는 주기
	private final int BIG_ATTACKER_INTERVAL= 7;	// 큰 공격자가 나타나는 주기 (토글 방식)
	private final int SPEED = 50;			// 애니매이션의 속도 (밀리초)
	private final int STEPS = 10;			// 그림 객체들이 한번에 움직이는 픽슬 수
	// 버튼 토글을 위한 비트 연산에 사용될 상수들
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	// 사용할 공격자 및 플레이어 그림 및 음향
	// src 폴더가 루트 "/"로 인식되므로 루트부터 경로명을 줌
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

	int gamePanelWidth, gamePanelHeight;	// 실제 게임이 이루어질 영역의 크기 
	JPanel controlPanel=new JPanel();		// 게임 컨트롤과 시간, 사용자 디스플레이가 들어갈 패널
	JButton start=new JButton("시작");		// 시작버튼
	JButton end=new JButton("종료");			// 종료버튼
	JButton suspend=new JButton("일시중지");	// 일시중지 버튼
	JButton cont=new JButton("계속");			// 계속 버튼
	JLabel timing=new JLabel("시간  : 0분 0초");// 게임경과 시간 디스플레이를 위한 라벨
	JLabel scoreLabel = new JLabel(" 점수 : 0점"); // 실시간 점수획득 디스플레이를 위한 라벨
	JLabel endLabel = new JLabel();			// 최종 획득 점수를 패널에 띄우기 위한 라벨
	JLayeredPane lp = new JLayeredPane();	// 화면을 여러장 겹치기 위한 Panel 레이어
	JPanel coverPanel;						// 초기화면이 나타날 패널	
	GamePanel gamePanel;					// 게임이 이루질 패널
	EndPanel endPanel;						// 게임이 끝나고 나타날 패널
	Timer goAnime;							// 그래픽 객체의 움직임을 관장하기 위한 타이머
	Timer goClock;							// 시계구현을 위한 위한 타이머
	ClockListener clockListener;			// 시계를 구현하기 위한 리스너
	ArrayList<Shape> attackerList;			// 게임에 사용되는 작은 공격자 객체를 담는 리스트
	ArrayList<Shape> bigAttackerList;		// 게임에 사용되는 큰 공격자 객체를 담는 리스트
	ArrayList<Missle> missleList = new ArrayList<>();			// 게임에 사용되는 미사일 객체를 담는 리스트
	Shape player;							// 키보드로 움직이는 Player 객체
	DirectionListener keyListener;			// 화살표 움직임을 감지하는 리스너
	private AudioClip backgroundSound;		// 게임 배경 음악
	private AudioClip boomSound;			// 충돌음향
	private AudioClip bighitsound;			// 큰 적 미사일 피격음향
	private AudioClip smallhitsound;		// 작은 적 미사일 피격음향
	private AudioClip shotsound;			// 미사일 발사음향
	private AudioClip endsound;
	static String playerName;				// 플레이어 이름
	int score =0;							// 점수
	Shape endMain2 = new Shape(getClass().getResource(END_MAIN_PIC)); // 게임종료 패널의 그림 배경을 위해 Shape 형 변수 지정
	
	public static void main(String [] args) {
		playerName=JOptionPane.showInputDialog("이름을 입력해주세요 :");	// Player의 이름 입력
		new MainGame().go();									// 게임의  초기화
	}
	

	public void go() {
		//GUI세팅
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 게임 조정 버튼 및 디스플레이 라벨들이 들어갈 패널
		controlPanel.add(start);
		controlPanel.add(suspend);
		controlPanel.add(cont);
		controlPanel.add(end);
		controlPanel.add(timing);
		controlPanel.add(new JLabel(" Player : "));
		controlPanel.add(new JLabel(playerName));
		controlPanel.add(scoreLabel);
		
		// 게임의 진행이 디스플레이 될 패널
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// 초기화면을 위한 패널
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		
		// 게임이 끝난 후 나타날 패널
		endPanel = new EndPanel();
		endPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		endPanel.add(endLabel);
		
		// 초기화면과 게임화면을 레이어화 함
		lp.add(gamePanel, new Integer(0));
		lp.add(coverPanel, new Integer(1));
		lp.add(endPanel,new Integer(2));
		
		endPanel.setVisible(false); //게임이 끝난 후 나타나야하기 때문에 일단 안보이게 설정
		
		// 전체 프레임에 배치
		frame.add(lp);
		frame.add(BorderLayout.CENTER, lp);
		frame.add(BorderLayout.SOUTH, controlPanel);
		
		// 게임이 이루어질 패널의 실제 폭과 넓이 계산
		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -130;

		//출력될 객체들을 생성 (모기)하여 attackerList에 넣어 줌
		prepareAttackers();
		
		// 키보드로 움직일 player 개체 생성
		player = new Shape(getClass().getResource(PLAYER_PIC), B_MARGIN, gamePanelWidth, gamePanelHeight);
		
		// 시간 디스플레이, 객체의 움직임을 자동화 하기 위한 타이머들 
		clockListener = new ClockListener();
		goClock = new Timer(1000, clockListener);			// 시간을 초단위로 나타내기 위한 리스너
		goAnime = new Timer(SPEED, new AnimeListener());	// 그림의 이동을 처리하기 위한 리스너

		// Player의 키보드 움직임을 위한 감청자
		gamePanel.addKeyListener(new DirectionListener());	// 키보드 리스너 설치
		gamePanel.setFocusable(false);						// 초기에는 포키싱 안되게 함(즉 키 안먹음)

		// 버튼  리스너의 설치
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		// 게임을 위한 음향 파일 설치
		try {
			// backgroundSound = JApplet.newAudioClip(new URL("file", "localhost","/res/start.wav"));
			// boomSound = JApplet.newAudioClip(new URL("file", "localhost","/res/boom.wav"));
			// 위의 방법은 상대경로를 나타내지 못하는 방법이어서, jar파일로 배포판을 만들때 경로를 찾지 못하는
			// 문제가 생김. 따라서 getClass()를 사용하여 상대적인 URL을 구하는 방법을 아래처럼 사용해야 함
			// 여기에서 root가 되는 폴더는 현재 이 프로그램이 수행되는 곳이니 같은 레벨에 넣어주어야 함
			backgroundSound = JApplet.newAudioClip(getClass().getResource(START_SOUND));
			boomSound = JApplet.newAudioClip(getClass().getResource(BOOM_SOUND));
			bighitsound = JApplet.newAudioClip(getClass().getResource(BIGHIT_SOUND));
			smallhitsound = JApplet.newAudioClip(getClass().getResource(SMALLHIT_SOUND));
			shotsound = JApplet.newAudioClip(getClass().getResource(SHOT_SOUND));
			endsound = JApplet.newAudioClip(getClass().getResource(END_MUSIC));
		}
		catch(Exception e){
			System.out.println("음향 파일 로딩 실패");
		}
		
		// 화면의 활성화
		buttonToggler(START);	// 초기에는 start버튼만 비 활성화
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);
	}
	 
	// 서비스 함수들

	// 버튼의 활성 비활성화를 위한 루틴
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
	
	// 게임의 시작에 사용될 공격자들
	private void prepareAttackers() {
		bigAttackerList = new ArrayList<Shape>();		// 큰 공격자의 리스트는 처음에는 비움
		attackerList = new ArrayList<Shape>();			// 공격자 6으로 시작
		attackerList.add(new DiagonallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new VerticallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new DiagonallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new VerticallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
	}
	
	// 미사일 발사에 사용될 메소드
	private void prepareMissle() {
		missleList.add(new Missle(player.x,player.y));
	}
	
	// 게임의 종료시 처리해야 될 내용
	private void finishGame() {
		endsound.play();
		endLabel.setText("<html><br><br><br><br><br>"+"게임 종료! 점수 : "+score+"</html>"); //패널에 나타날 최종점수
		endLabel.setFont(endLabel.getFont().deriveFont(40.0f)); // 라벨 글자 크기 조절
		endLabel.setHorizontalAlignment(endLabel.CENTER);		// 라벨 글자 위치 조절
		endLabel.setForeground(Color.white);// 라벨 글자 색 조절
		endPanel.setVisible(true);			// 게임이 종료되면 결과 패널이 보이게 됨
		backgroundSound.stop();				// 음향 종료
		goClock.stop();						// 시간 디스플에이 멈춤
		goAnime.stop();						// 그림객체 움직임 멈춤
		gamePanel.setFocusable(false);		// 포커싱 안되게 함(즉 키 안먹음)
		buttonToggler(START);				// 활성화 버튼의 조정

		
	}
	
	// 여러종류의 움직임을 랜덤으로 발생시키는 공격객체의 생성
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
	
	// 내부 클래스 둘
	
	
	// goAnime 타이머에 의해 주기적으로 실행될 내용
	// 객체의 움직임, 충돌의 논리를 구현
	public class AnimeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			// 만약 충돌하였으면 충돌의 효과음 나타내고 타이머를 중단시킴
			for (Shape s : attackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					boomSound.play();					// 충돌의 음향
					finishGame();						// 게임 중단
					return;
				}
				for(Missle k : missleList) { //미사일과 적이 충돌하면 사라짐
					if (s.collide(new Point(k.mx,k.my))) {
						attackerList.remove(s);					// 충돌한 적 삭제
						missleList.remove(k);					// 충돌한 미사일 삭제
						score++;								// 적을 처치하면 점수획득
						smallhitsound.play();					// 작은적 처치음향 플레이
						scoreLabel.setText("점수 : "+score+"점");
						return;
					}
				}
			}
			for (Shape s : bigAttackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					boomSound.play();					// 충돌의 음향
					finishGame();						// 게임 중단
					return;
				}
				for(Missle k : missleList) { //미사일과 적이 충돌하면 사라짐
					if (s.collide(new Point(k.mx,k.my))) {
						s.hp--;					//미사일 한개 충돌마다 큰 적의 hp를 1 감소시킴
						missleList.remove(k);	// 충돌한 미사일 삭제
						if(s.hp <=0) {			// 큰 적의 체력이 0 밑으로 떨어지면
							bigAttackerList.remove(s);
							score = score+3;
							bighitsound.play();
							scoreLabel.setText("점수 : "+score+"점");
						}
						return;
					}
				}
				
			}
			// 그림 객체들을 이동시킴
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
	
	// 시작 버튼의 감청자
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			endsound.stop();
			endPanel.setVisible(false);						// 시작하면 endPanel을 안보이게 만듬
			lp.setLayer(gamePanel, 2);						// gamePanel 이 앞으로 나오게 함
			gamePanel.setFocusable(true);					// gamePanel이 포커싱될 수 있게 함
			gamePanel.requestFocus();						// 포커싱을 맞춰줌(이것 반드시 필요)

			backgroundSound.play();							// 배경음악 시작
			goAnime.start();								// 그림객체 움직임을 위한 시작

			clockListener.reset();							// 타이머의 시작값 초기화
			timing.setText("시간  : 0분 0초");	
			goClock.start();								// 시간 디스플레이 타이머시작
			score = 0;										// 다시 점수 초기화
			scoreLabel.setText("점수 : "+score+"점");
			
			missleList.clear();								// 미사일 객체 초기화

			prepareAttackers();								// 초기 공격자 준비

			buttonToggler(SUSPEND+END);						// 활성화된 버튼의 조정

		}
	}
	
	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.stop();		
			goAnime.stop();
			gamePanel.setFocusable(false);					// 게임 프레임에 키 안먹게 함
			buttonToggler(CONT+END);						// 활성화 버튼의 조정
		}
	}
	
	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.restart();
			goAnime.restart();
			gamePanel.setFocusable(true);					// 게임 프레임 키 먹게 함
			gamePanel.requestFocus();						// 전체 프레밍에 포커싱해서 키 먹게 함
			buttonToggler(SUSPEND+END);						// 활성화 버튼의 조정
		}
	}

	// 종료버튼을 위한 감청자
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			finishGame();
		}
	}

	// 게임이 진행되는 메인 패널
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0,0,this.getWidth(), this.getHeight());		// 화면 지우기
		
			// 게임에 사용되는 그래픽 객체들 모두 그려줌
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
	
	// 게임이 종료되고 나타나는 패널
	class EndPanel extends JPanel{
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			endMain2.draw2(g, this);
		}
	}
	
	// 초기화면을 나타내는 패널
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 
			g.drawImage(image,0,0,this);
		}
	}
	
	// 시간 디스플레이를 위해 사용하는 시계
	private class ClockListener implements ActionListener {
		int times = 0;
		public void actionPerformed (ActionEvent event) {		
			times++;						
			timing.setText("시간  : "+times/60+"분 "+times%60+"초");

			// 시간이 일정시간 지나면 새로운 모기를 출현시킴
			if (times % NEW_ATTACKER_INTERVAL == 0)
				attackerList.add(getRandomAttacker(ATTACKER_PIC, S_MARGIN, STEPS));

			// 시간이 일정시간 지나면 bigAttacker 출현/소멸 시킴
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
	
	// 키보드 움직임을 감청하는 감청자
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
	   
	   // 스페이스를 눌렀다 떼면 미사일 객체를 하나 생성함
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