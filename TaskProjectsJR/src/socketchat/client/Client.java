package socketchat.client;

import socketchat.Connection;
import socketchat.ConsoleHelper;
import socketchat.Message;
import socketchat.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Input server address");
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Input server port");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Input username");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        }
        catch (Exception e) {
            clientConnected = false;
            ConsoleHelper.writeMessage("Message send failed");
        }
    }

    public void run() {
        Thread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized (this) {
                this.wait();
            }
        }
        catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Thread notification expired");
            return;
        }
        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду ‘exit’.");

        }
        else ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        while (clientConnected) {
            String s = ConsoleHelper.readString();
            if (s.equalsIgnoreCase("exit")) break;
            else if (shouldSendTextFromConsole()) {
                sendTextMessage(s);
            }
        }

    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "joined chat");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "left chat");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = Client.this.connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST){
                    String clientName = getUserName();
                    Message msg = new Message(MessageType.USER_NAME, clientName);
                    Client.this.connection.send(msg);
                }
                else if (message.getType()==MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    return;
                }
                else throw new IOException("Unexpected MessageType");
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (!this.isInterrupted()) {
                Message message;
                message = Client.this.connection.receive();
                if (message.getType()==MessageType.TEXT) processIncomingMessage(message.getData());
                else if (message.getType()==MessageType.USER_ADDED) informAboutAddingNewUser(message.getData());
                else if (message.getType()==MessageType.USER_REMOVED) informAboutDeletingNewUser(message.getData());
                else throw new IOException("Unexpected MessageType");
            }
        }

        @Override
        public void run() {
            String serverAddr = getServerAddress();
            int serverPort = getServerPort();
            try {
                Socket socket = new Socket(serverAddr,serverPort);
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            }
            catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }

    }
}
