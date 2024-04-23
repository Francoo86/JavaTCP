import io.github.cdimascio.dotenv.Dotenv;
import server.TCPServer;

public class MainServer {
    private static final int PORT = 7896;
    public static void main(String[] args) {
        Dotenv env = Dotenv.load();
        String databaseUrl = env.get("DB_URL");
        String dbName = env.get("DB_NAME");
        String port = env.get("DB_PORT");
        String user = env.get("DB_USER");
        String password = env.get("DB_PASSWORD");
        String fullPath = String.format("jdbc:%s:%s/%s?user=%s&password=%s", databaseUrl, port, dbName, user, password);

        TCPServer server = new TCPServer(fullPath, PORT);
        server.listenClients();
    }
}
