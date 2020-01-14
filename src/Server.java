import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private final static int port = 3025;

	public static Blockchain blockchain = Blockchain.getInstance(2);

	private static ServerSocket serverSocket;

	private static List<Socket> connectedClients = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		initSocket();

		// Starting Threads
		connectionListener();
	}

	private static void initSocket() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Running " + StringUtil.ANSI_BLUE + "Sor" + StringUtil.ANSI_RED + "Ex" + StringUtil.ANSI_RESET + " Server on port " + port);
	}

	private static void connectionListener() {
		Runnable task = () -> {
			while (true) {
				Socket socket;
				try {
					socket = acceptConnect(serverSocket);
					connectedClients.add(socket);
					receiveAndSendTransaction(socket);
				} catch (Exception e) {
					System.err.println("Connection Error (34)");
				}
			}
		};
		new Thread(task).start();
	}

	private static Socket acceptConnect(ServerSocket serverSocket) throws Exception {
		Socket clientSocket = serverSocket.accept();
		System.out.println(StringUtil.ANSI_BLUE + "[+]" + StringUtil.ANSI_RESET + " New connection accepted from " + StringUtil.ANSI_GREEN + getSocketIP(clientSocket) + StringUtil.ANSI_RESET);
		return clientSocket;
	}

	private static void receiveAndSendTransaction(Socket socket) {
		Runnable task = () -> {
			while (true) {
				// Receiving a transaction request
				Transaction transaction = null;
				try {
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					transaction = (Transaction) ois.readObject();
					System.out.println(StringUtil.ANSI_BLUE + "[+]" + StringUtil.ANSI_RESET + "Received new transaction request : [" + StringUtil.ANSI_CYAN + transaction.getValue() + " SorEx]" + StringUtil.ANSI_RESET + " from " + StringUtil.ANSI_CYAN + getSocketIP(socket) + StringUtil.ANSI_RESET + " to " + StringUtil.ANSI_CYAN + transaction.getReceiverIP() + StringUtil.ANSI_RESET + ".");
				} catch (IOException | ClassNotFoundException e) {
					System.err.println("Error (58)"); // TODO DEL
				}

				// Forwarding the transaction to the receiver
				try {
					Socket receiver = null;
					for (Socket s : connectedClients) {
						if (s.getInetAddress().getHostAddress().equals(transaction.getReceiverIP()))
							receiver = s;
					}
					if (receiver == null || receiver.isClosed()) throw new UnknownHostException();

					ObjectOutputStream receiver_oos = new ObjectOutputStream(receiver.getOutputStream());
					receiver_oos.writeObject(transaction);

					reply(socket, transaction, "ACK");
					System.out.print(StringUtil.ANSI_GREEN + "[+] " + StringUtil.ANSI_CYAN + transaction.getValue() + StringUtil.ANSI_RESET + " SorEx coins transferred from " + StringUtil.ANSI_CYAN + getSocketIP(socket) + StringUtil.ANSI_RESET + " to " + StringUtil.ANSI_CYAN + getSocketIP(receiver) + StringUtil.ANSI_RESET + ".");

					blockchain.addBlock(transaction); // TODO CHECK THIS
				} catch (UnknownHostException e) {
					reply(socket, transaction, "ERR");
					System.out.print(StringUtil.ANSI_RED + "[-] Error " + StringUtil.ANSI_RESET + "while processing transaction from " + StringUtil.ANSI_CYAN + getSocketIP(socket) + StringUtil.ANSI_RESET + " to " + StringUtil.ANSI_CYAN + transaction.getReceiverIP() + StringUtil.ANSI_RED + "\n[ ] Unknown host." + StringUtil.ANSI_RESET);
				} catch (Exception e) {
					System.out.println("EXCEPTION (86)");
				}
			}
		};
		new Thread(task).start();
	}

	public static void reply(Socket socket, Transaction transaction, String status) {
		try {
			ObjectOutputStream sender_oos = new ObjectOutputStream(socket.getOutputStream());
			transaction.setControlValue(status);
			sender_oos.writeObject(transaction);
		} catch (IOException e) {
		}
	}

	public static String getSocketIP(Socket socket) {
		return socket.getInetAddress().getHostAddress();
	}
}
