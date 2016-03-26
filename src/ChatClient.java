import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChatClient extends Frame {

	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;

	TextField tf = new TextField();
	TextArea ta = new TextArea();

	Thread tRecv = new Thread(new RecvThread());

	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}

	public void launchFrame() {
		setLocation(400, 300);
		setSize(500, 500);
		add(tf, BorderLayout.SOUTH);
		add(ta, BorderLayout.NORTH);
		pack();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		tf.addActionListener(new tfListener());
		setVisible(true);
		connect();

		tRecv.start();
	}

	public void connect() {
		try {
			s = new Socket("127.0.0.1", 8888);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			System.out.println("connect!");
			bConnected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {

		try {
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * try { bConnected = false; tRecv.join(); } catch (InterruptedException
		 * e) { e.printStackTrace(); } finally { try { dos.close(); dis.close();
		 * s.close(); } catch (IOException e) { e.printStackTrace(); } }
		 */
	}

	private class tfListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String str = tf.getText().trim();
			// ta.setText(str);
			tf.setText(null);
			try {
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private class RecvThread implements Runnable {

		public void run() {
			try {
				while (bConnected) {
					String str = dis.readUTF();
					// System.out.println(str);
					ta.setText(ta.getText() + str + '\n');
				}
			} catch (SocketException e) {
				System.out.println("已退出");
			} catch (EOFException e) {
				System.out.println("已退出");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
