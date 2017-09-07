package socketchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message) {
            for (Map.Entry<String, Connection> entry: connectionMap.entrySet()) {
                try {
                    entry.getValue().send(message);
                } catch (IOException e) {
                    System.out.println("Error sending message");
                }
            }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            String name = "";
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message msgName = connection.receive();

                if (msgName.getType() == MessageType.USER_NAME && !msgName.getData().isEmpty()) {
                    name = msgName.getData();
                    if (!connectionMap.containsKey(name)) {
                        connectionMap.put(name, connection);
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        break;
                    }
                }
            }
            return name;
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> entry: connectionMap.entrySet()) {
                if (!entry.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, entry.getKey()));
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
           while (true) {
               Message message = connection.receive();
               if (message.getType() == MessageType.TEXT) {
                   String text = userName + ": " + message.getData();
                   Server.sendBroadcastMessage(new Message(MessageType.TEXT, text));
               } else ConsoleHelper.writeMessage("Message is not text");
           }
        }

        public void run() {
            String client = "";
            ConsoleHelper.writeMessage("Connection established " + socket.getRemoteSocketAddress() );
            try (Connection connection = new Connection(socket)) {
            client = serverHandshake(connection);
            Server.sendBroadcastMessage(new Message(MessageType.USER_ADDED, client));
            sendListOfUsers(connection, client);
            serverMainLoop(connection, client);
        }
        catch (IOException | ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Error while data exchange with remote address");
        }
        connectionMap.remove(client);
            Server.sendBroadcastMessage(new Message(MessageType.USER_REMOVED, client));
            ConsoleHelper.writeMessage("Connection closed");

        }
    }

    public static void main(String[] args) throws IOException{
        int port = ConsoleHelper.readInt();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            ConsoleHelper.writeMessage("Server started");
            for (; ; ) {
                Socket s = serverSocket.accept();
                Handler handler = new Handler(s);
                handler.start();
            }
        }
        catch (IOException e){
            ConsoleHelper.writeMessage("io error");
            serverSocket.close();
        }
    }
}
