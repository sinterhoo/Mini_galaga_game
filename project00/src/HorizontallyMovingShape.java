import java.net.URL;



public class HorizontallyMovingShape extends Shape{
	public HorizontallyMovingShape(URL imgURL, int x, int y, int margin, int steps, int xBoundary, int yBoundary) {
		// imgPath : �׸� ������ ��θ�
		// x, y : �̹����� ��ġ ��ǥ
		// margin : �� �̹����� ������ ��Ÿ���� ���� (�� �����ȿ� ������ �浹 �� ������ �Ǵ� �ϱ� ����)
		// steps : �̹����� �����϶� �̵��ϴ� ��ǥ ����
		// xBoundary, yBoundary : �׸��� �̵��� �� �ִ� ��ǥ�� �ִ밪
		super (imgURL, x, y, margin, steps, xBoundary, yBoundary);
	}
	
	public HorizontallyMovingShape(URL imgURL,int margin, int steps, int xBoundary, int yBoundary) {
		super (imgURL, margin, steps, xBoundary, yBoundary);
	}

	public void move() {
		if (xDirection > 0 && x >= xBoundary) {
			xDirection = -1;
			y += (yDirection * steps * 5);
		}
		if (xDirection < 0 && x <= 0) {
			xDirection = 1;
			y += (yDirection * steps * 5);
		}
		x += (xDirection * steps);

		if (yDirection > 0 && y >= yBoundary) {
			yDirection = -1;
		}
		if (yDirection < 0 && y <= 0) {
			yDirection = 1;
		}
	}
}