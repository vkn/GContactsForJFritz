package gcontacts2jfritz;

//import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.FamilyName;
import com.google.gdata.data.extensions.GivenName;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.PostalAddress;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ContactsWriter {
    
    private static final Logger LOG = Logger.getLogger(ContactsWriter.class.getName());

    void write(List<ContactEntry> contacts, Boolean skipWithoutNumbers) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("phonebook");
            doc.appendChild(rootElement);
            Element comment = doc.createElement("comment");
            comment.appendChild(doc.createTextNode("Phonebook for JFritz v0.7.4.2"));
            rootElement.appendChild(comment);

            for (ContactEntry contactEntry : contacts) {
                /*
                @TODO download photo
                Link photoLink = contactEntry.getLink(
                        "http://schemas.google.com/contacts/2008/rel#photo", "image/*");
                String photoEtag = photoLink.getEtag();
                if (photoEtag != null) {
                    System.err.println("Photo link: " + photoLink.getHref());
                }
                */
                Element node = makeContactNode(doc, contactEntry, skipWithoutNumbers);
                if (node != null) {
                    rootElement.appendChild(node);
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("jfritz.phonebook.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);
            transformer.transform(source, result);

            System.out.println("Saved! You can import jfritz.phonebook.xml from JFritz application");

        } catch (ParserConfigurationException | TransformerException pce) {
            LOG.log(Level.SEVERE, "an exception was thrown", pce);
        }

    }

    private Element makeContactNode(Document doc, ContactEntry contactEntry, Boolean skipWithoutNumbers) {
  
        Element entry = doc.createElement("entry");
        
        entry.setAttribute("private", "false");
        Element nameNode = makeNameNode(doc, contactEntry.getName());
        if (nameNode != null) {
            entry.appendChild(nameNode);
        }

        List<PhoneNumber> numbers = contactEntry.getPhoneNumbers();
        Element entryNumbers = makeNumbersNode(numbers, doc);
        if (entryNumbers != null) {
            entry.appendChild(entryNumbers);
        } else {
            if (skipWithoutNumbers) {
                return null;
            }
        }
        
        List<PostalAddress> addresses = contactEntry.getPostalAddresses();
        Element entryAddress = makeAddressNode(addresses, doc);
        if (entryAddress != null) {
            entry.appendChild(entryAddress);
        }
        
        List<Email> emails = contactEntry.getEmailAddresses();
        Element internet = makeEmailNode(emails, doc);
        if (internet != null) {
            entry.appendChild(internet);
        }
        return entry;
    }
    
    

    private Element makeEmailNode(List<Email> emails, Document doc) {
        if (emails.size() < 1) {
            return null;
        }
        Element internet = doc.createElement("internet");
        for (Email email : emails) {
            Element emailElement = doc.createElement("email");
            emailElement.appendChild(doc.createTextNode(email.getAddress()));
            internet.appendChild(emailElement);
            break;
        }
        return internet;
    }
    
    private Element makeAddressNode(List<PostalAddress> list, Document doc)
    {
        if (list.size() < 1) {
            return null;
        }        
        Element address = doc.createElement("address");
        for (PostalAddress postalAddress : list) {
            String value = postalAddress.getValue();
            Element city = doc.createElement("city");
            city.appendChild(doc.createTextNode(value));
            address.appendChild(city);
            break;
        }            
        return address;
    }
    
    private Element makeNumbersNode(List<PhoneNumber> list, Document doc) {
        if (list.size() < 1) {
            return null;
        }
        Element entryNumbers = doc.createElement("numbers");
        String primary = "";
        for (PhoneNumber phoneNumber : list) {
            String rel = phoneNumber.getRel();
            String type = "home";
            if (rel != null) {
                if (rel.endsWith("mobile")) {
                    type = "mobile";
                }
                if (rel.endsWith("home")) {
                    type = "home";
                }
                if (rel.endsWith("work")) {
                    type = "work";
                }
                if (phoneNumber.getPrimary()) {
                    primary = type;
                }
            }
            Element entryNumber = doc.createElement("number");
            entryNumber.setAttribute("type", type);
            entryNumber.appendChild(doc.createTextNode(phoneNumber.getPhoneNumber()));
            entryNumbers.appendChild(entryNumber);
        }
        if (!primary.isEmpty()) {
            entryNumbers.setAttribute("standard", primary);
        }
        return entryNumbers;
    }

    private Element makeNameNode(Document doc, Name contactName) throws DOMException {

        if (contactName == null) {
            return null;
        }
        FamilyName familyName = contactName.getFamilyName();
        GivenName givenName = contactName.getGivenName();
        if (givenName == null && familyName == null) {
            return null;
        }
        Element name = doc.createElement("name");
        
        if (givenName != null) {
            Element firstname = doc.createElement("firstname");
            firstname.appendChild(doc.createTextNode(givenName.getValue()));
            name.appendChild(firstname);
        }
        
        if (familyName != null) {
            Element lastname = doc.createElement("lastname");
            lastname.appendChild(doc.createTextNode(familyName.getValue()));
            name.appendChild(lastname);
        }
        return name;
    }
}
