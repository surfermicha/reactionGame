package GameServer;

/**
 * This class represents a user.
 *
 * @author Michael Landreh
 */
public class SessionData {
    private int sessionId;
    private String email;
    private String firstname;
    private String lastname;

    //Constructors

    public SessionData(int sessionId, String email, String firstname, String lastname) {
        this.sessionId = sessionId;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public SessionData() {
    }

    //Getter and Setter

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
