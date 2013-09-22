package gcontacts2jfritz;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContactFetcher {

    ContactsService service = new ContactsService("<var>Contact Export</var>");

    private String password;
    private String url;
    private URL feedUrl;

    /**
     * Get the value of password
     *
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password
     *
     * @param password new value of password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    private String username;

    /**
     * Get the value of username
     *
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the value of username
     *
     * @param username new value of username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    List<ContactEntry> fetchEntries() throws AuthenticationException, IOException {
        service.setUserCredentials(username, password);
        url = "https://www.google.com/m8/feeds/contacts/" + username + "/thin/?max-results=1000";
        feedUrl = new URL(url);
        try {
            return getEntries();
        } catch (ServiceException ex) {
            Logger.getLogger(ContactFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private List<ContactEntry> getEntries()
            throws IOException, ServiceException {

        com.google.gdata.data.contacts.ContactFeed resultFeed;
        resultFeed = service.getFeed(feedUrl, com.google.gdata.data.contacts.ContactFeed.class);
        System.err.println(resultFeed.getTitle().getPlainText());
        return resultFeed.getEntries();
    }
}
