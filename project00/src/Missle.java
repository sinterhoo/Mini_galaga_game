import javax.swing.ImageIcon;
import java.net.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import javax.imageio.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Missle extends ImageIcon{
		int mx,my; //������ ��ġ
		private int dy; //1ȸ �̵��Ÿ�
		int missle_w = 30, missle_h = 30; // �̻����� ũ��
		
		//Image img =Toolkit.getDefaultToolkit().getImage("C:\\Users\\whwls\\Desktop\\cho\\project00\\src\\3.PNG"); ������
		
		Image img = Toolkit.getDefaultToolkit().getImage(MainGame.class.getResource("").getPath()+"3.PNG");
		
		public Missle(int x, int y) {
			mx=x;
			my=y;
			dy = 15;
		}
		 public void move() {
	         my -= dy ;
	      }
		 public void draw(Graphics g) {
			 g.drawImage(img,mx,my,missle_w,missle_h,null);
		 }
	}