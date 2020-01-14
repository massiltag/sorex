import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
	private final static int port = 3025;

	public static Blockchain blockchain = Blockchain.getInstance(1);

	private static ServerSocket serverSocket;

	private static List<Socket> connectedClients = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		showIntro();
		System.out.println(" - Enter [" + StringUtil.ANSI_WHITE + "show" + StringUtil.ANSI_RESET + "] to show Blockchain\n" +
				" - Enter [" + StringUtil.ANSI_WHITE + "exit" + StringUtil.ANSI_RESET + "] to exit.");
		initSocket();

		// Starting Threads
		connectionListener();
		blockchainOperations();
	}

	private static void initSocket() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Running " + StringUtil.ANSI_BLUE + "Sor" + StringUtil.ANSI_RED + "Ex" + StringUtil.ANSI_RESET + " Server on port " + port + "(" + StringUtil.ANSI_PURPLE + getIP() + StringUtil.ANSI_RESET + ")");
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
			boolean run = true;
			while (run) {
				// Receiving a transaction request
				Transaction transaction = null;
				try {
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					transaction = (Transaction) ois.readObject();
					System.out.println(StringUtil.ANSI_BLUE + "[+]" + StringUtil.ANSI_RESET + " Received new transaction request :" + StringUtil.ANSI_CYAN + " [" + transaction.getValue() + " SorEx]" + StringUtil.ANSI_RESET + " from " + StringUtil.ANSI_CYAN + getSocketIP(socket) + StringUtil.ANSI_RESET + " to " + StringUtil.ANSI_CYAN + transaction.getReceiverIP() + StringUtil.ANSI_RESET + ".");
				} catch (IOException | ClassNotFoundException e) {
					run = false;
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
					System.out.println(StringUtil.ANSI_GREEN + "[+] " + StringUtil.ANSI_CYAN + "[" + transaction.getValue() + " SorEx]" + StringUtil.ANSI_RESET + " coins transferred from " + StringUtil.ANSI_CYAN + getSocketIP(socket) + StringUtil.ANSI_RESET + " to " + StringUtil.ANSI_CYAN + getSocketIP(receiver) + StringUtil.ANSI_RESET + ".");

					blockchain.addBlock(transaction); // TODO CHECK THIS
				} catch (UnknownHostException e) {
					reply(socket, transaction, "ERR");
					System.out.println(StringUtil.ANSI_RED + "[-] Error " + StringUtil.ANSI_RESET + "while processing transaction from " + StringUtil.ANSI_CYAN + getSocketIP(socket) + StringUtil.ANSI_RESET + " to " + StringUtil.ANSI_CYAN + transaction.getReceiverIP() + StringUtil.ANSI_RED + "\n--> Host unreachable." + StringUtil.ANSI_RESET);
				} catch (Exception e) {
					System.out.println(StringUtil.ANSI_RED + "[-] " + StringUtil.ANSI_RESET + "Client " + StringUtil.ANSI_CYAN + getSocketIP(socket) + StringUtil.ANSI_RED + " disconnected" + StringUtil.ANSI_RESET + ".");
					for (Socket s : connectedClients) {
						if (getSocketIP(s).equals(getSocketIP(socket))) {
							connectedClients.remove(s);
							break;
						}
					}
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

	public static void blockchainOperations() {
		Runnable task = () -> {
			Scanner sc = new Scanner(System.in);
			String op;
			while (true) {
				op = sc.nextLine();
				switch (op) {
					case "show":
						showBlocks();
						break;
					case "exit":
						exit();
						break;
					default:
						break;
				}
			}
		};
		new Thread(task).start();
	}

	public static void showBlocks() {
		for (Block block : blockchain.getBlockchain()) {
			System.out.print(block.toString());
		}
	}

	public static void exit() {
		for (Socket socket : connectedClients) {
			try {
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
			}
		}
		System.exit(0);
	}

	public static void showIntro() {
		System.out.println("Welcome to");
		try {
			BufferedReader reader = new BufferedReader(new FileReader("./SorEx.txt"));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				System.out.println(buffer);
			}
		} catch (IOException e) {
		}
	}

	public static String getIP() {
		String s = null;
		try {
			s = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}
		return s;
	}

}
