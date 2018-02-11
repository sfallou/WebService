import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

// javac Message.java

@XmlRootElement
public class Message implements Serializable {
    private static final long serialVersionUID = 6826191735682596960L;
    private int id;
    private String poster;
    private String content;
    private String room;
    
    // public Message() {} // needed for JAXB
    public Message(int id, String poster, String content, String room) {
        this.id = id;
        this.poster = poster;
        this.content = content;
        this.room = room;
  
    }
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
}