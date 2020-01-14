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
		System.out.println("Server running on port " + port);
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
		System.out.println("[+] New connection accepted from " + getSocketIP(clientSocket));
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
					System.out.println("[+] Received new transaction request : [" + transaction.getValue() + " SorEx] from " + getSocketIP(socket) + " to " + transaction.getReceiverIP() + ".");
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
					System.out.print("[+] " + transaction.getValue() + " SorEx coins transferred from " + getSocketIP(socket) + " to " + getSocketIP(receiver) + ".");

					blockchain.addBlock(transaction); // TODO CHECK THIS
				} catch (UnknownHostException e) {
					reply(socket, transaction, "ERR");
					System.out.print("[-] Error while processing transaction from " + getSocketIP(socket) + " to " + transaction.getReceiverIP() + "\n[ ] Unknown host.");
				} catch (Exception e) {
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
