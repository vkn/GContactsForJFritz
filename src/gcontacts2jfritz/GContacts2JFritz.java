package gcontacts2jfritz;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.AuthenticationException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class GContacts2JFritz {
    private static final Logger LOG = Logger.getLogger(GContacts2JFritz.class.getName());
    
    public static void main( String[] args ) throws IOException, AuthenticationException
    {
        Handler handler = new FileHandler("application.log", 1024*1024*1024, 3);
        Logger.getLogger("").addHandler(handler);

        ContactFetcher contactFetcher = new ContactFetcher();
        
        Scanner input = new Scanner(System.in);
        
        System.out.print("Enter username: ");
        contactFetcher.setUsername(input.nextLine());
        System.out.print("Enter password: ");
        contactFetcher.setPassword(input.nextLine());
        List<ContactEntry> contacts = contactFetcher.fetchEntries();
        ContactsWriter writer =  new ContactsWriter();
        writer.write(contacts, true);
        LOG.info("DONE");
    }
    
}
