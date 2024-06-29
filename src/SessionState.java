import java.io.*;
import java.util.List;

public class SessionState implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> tabContents;

    public SessionState(List<String> tabContents) {
        this.tabContents = tabContents;
    }

    public List<String> getTabContents() {
        return tabContents;
    }
    
    public void saveSessionState(List<String> tabContents) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("session.dat"))) {
        oos.writeObject(new SessionState(tabContents));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public SessionState loadSessionState() {
    SessionState sessionState = null;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("session.dat"))) {
        sessionState = (SessionState) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }
    return sessionState;
}
}
