import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.*;

public class ChatServer {

	boolean started = false;
	ServerSocket ss = null;

	List<Client> clients = new ArrayList<Client>();

	public static void main(String[] args) {
		new ChatServer().start();
	}

	public void start() {
		try {
			ss = new ServerSocket(8888);
			started = true;
		} catch (BindException e) {
			System.out.println("端口使用中");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			while (started) {
				Socket s = ss.accept();
				Client c = new Client(s);
				System.out.println("a cilent connected!");
				new Thread(c).start();
				clients.add(c);
				// dis.close();
			}
		} catch (SocketException e) {
			System.out.println("one client closed!");
		} catch (EOFException e) {
			System.out.println("one client closed!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	class Client implements Runnable {
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnected = false;

		public Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(String str) {

			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("退出了一个client已去除");
				//e.printStackTrace();
			}

		}

		public void run() {
			Client c = null;
			try {
				while (bConnected) {
					String str;
					str = dis.readUTF();
					System.out.println(str);
					for (int i = 0; i < clients.size(); i++) {
						c = clients.get(i);
						c.send(str);
					}
				}
			} catch (EOFException e) {
				System.out.println("client closed!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
					if (s != null)
						s.close();

				} catch (IOException e1) {
					e1.getStackTrace();
				}

			}

		}

	}
}
