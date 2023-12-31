package shotmaniacs.group2.di.model;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.sql.Date;
import java.sql.Timestamp;
@XmlRootElement
public class Announcement {
    @XmlAnyElement
    private AnnouncementState state;
    @XmlAnyElement
    private int id;
    @XmlAnyElement
    private String title;
    @XmlAnyElement
    private String body;
    @XmlAnyElement
    private int publisher;
    @XmlAnyElement
    private Urgency urgency;
    @XmlAnyElement
    private java.sql.Timestamp date;
    //private java.sql.Timestamp timestamp;

    public Announcement(int id, String title,String body,int publisher, Urgency urgency, java.sql.Timestamp date) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.publisher = publisher;
        this.urgency = urgency;
        this.date = date;
    }
    public Announcement(){

    }
    public Announcement(int id, String title,String body,int publisher, Urgency urgency, java.sql.Timestamp date,AnnouncementState state) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.publisher = publisher;
        this.urgency = urgency;
        this.date = date;
        this.state = state;
    }
    public AnnouncementState getAnnouncementState(){
        return state;
    }
    public void setAnnouncementState(AnnouncementState state){
        state = state;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public int getPublisher() {
        return publisher;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public java.sql.Timestamp getDate() {
        return date;
    }

    /*public java.sql.Timestamp getTimestamp() {
        return timestamp;
    }*/

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    /*public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }*/
}