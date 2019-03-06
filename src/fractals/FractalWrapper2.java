package fractals;

import java.awt.EventQueue;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class FractalWrapper2 extends JFrame {

	public final int FRAMESIZE = 600;
	public final int BTNSPACE = 63;
	public final int HRZSPACE = 8;
	
	public FractalWrapper2() {
        setSize(3*FRAMESIZE/2+HRZSPACE, FRAMESIZE+BTNSPACE);
		add(new JuliaSets2(FRAMESIZE, FRAMESIZE));
        setResizable(false);
        setTitle("Fractals");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FractalWrapper2 go = new FractalWrapper2();
                go.setVisible(true);
            }
        });
	}

}
