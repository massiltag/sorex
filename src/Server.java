import java.net.*;
import java.io.*;

public class Server {
	public Blockchain blockchain;
	private ServerSocket server;

	public Server(String ipAddress) throws Exception {
		if (ipAddress != null && !ipAddress.isEmpty()) {
			this.server = new ServerSocket(
					0, 1, InetAddress.getByName(ipAddress)
					);
		} else {
			this.server = new ServerSocket(
					0, 1, InetAddress.getLocalHost()
					);
		}
	}

	private void listen() throws Exception {
		String data = null;
		Socket client = this.server.accept();
		String clientAddress = client.getInetAddress().getHostAddress();
		System.out.println("\r\nNew connection from : " + clientAddress);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
					client.getInputStream()
					)
				);
		while ((data = in.readLine()) != null) {
			System.out.println("\r\nMessage from : " + clientAddress +
					" : " + data);
		}
	}

	public InetAddress getSocketAddress() {
		return this.server.getInetAddress();
	}

	public int getPort() {
		return this.server.getLocalPort();
	}

	public boolean TCPConnect() {
		return false;
	}
	public static void main(String[] args) throws Exception {
		Server app = new Server(args[0]);
		System.out.println("\r\nRunning server : " +
				" Host=" + app.getSocketAddress().getHostAddress() +
				" Port=" + app.getPort()
				);
		app.listen();
	}
}
