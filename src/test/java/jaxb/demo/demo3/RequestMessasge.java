package jaxb.demo.demo3;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.n3r.core.lang.RBaseBean;

@XmlRootElement(name = "RequestMessasge")
public class RequestMessasge extends RBaseBean {
    @XmlElement
    private String interval;
    private String from;
    private String to;
    private String pollingurl;

    public RequestMessasge() {
        interval = "c";
        from = "a";
        to = "b";
    }

    public static void main(String[] args) throws JAXBException {
        String xml = "<RequestMessasge>"
                + "<interval>int</interval>"
                + "<from>String</from>"
                + "<to>String</to>"
                + "<pollingurl>"
                + "String"
                + "</pollingurl>"
                + "</RequestMessasge>";
        //set the pojo in a new instance of JaxbContext
        JAXBContext jaxbContext = JAXBContext
                .newInstance(RequestMessasge.class);

        Unmarshaller um = jaxbContext.createUnmarshaller();
        ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
        RequestMessasge message = (RequestMessasge) um.unmarshal(is);
        System.out.println(message);
    }

    public String getInterval() {
        return interval;
    }

    //    public void setInterval(String interval) {
    //        this.interval = interval;
    //    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;

    }

    public String getPollingurl() {
        return pollingurl;
    }

    public void setPollingurl(String pollingurl) {
        this.pollingurl = pollingurl;
    }

}
