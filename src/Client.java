import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
	private int id;
	private String email;
	private String name;
	private Server server;
	private Socket socket;
	private Scanner scanner;

	private Client(InetAddress serverAddress, int serverPort) throws Exception {
		this.socket = new Socket(serverAddress, serverPort);
		this.scanner = new Scanner(System.in);
	}
	private void start() throws IOException {
		String input;
		while (true) {
			input = scanner.nextLine();
			PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
			out.println(input);
			out.flush();
		}
	}
	public void effectuerTransaction(int montant, int id) {
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Server getServer() {
		return server;
	}
	public void setServer(Server server) {
		this.server = server;
	}
	public static void main(String[] args) throws Exception {
		Client client = new Client(
				InetAddress.getByName(args[0]),
				Integer.parseInt(args[1])
				);
		System.out.println("\r\nConnected to server: " + client.socket.getInetAddress());
		client.start();
	}
}
