package entity;

public class ChatMessage {
	private String message; // Текст сообщения
	private ChatUser author; // Автор сообщения
	private long timestamp; // Временная метка сообщения (в микросекундах)
	private String privatem;
	
	public ChatMessage(String message, ChatUser author, long timestamp, String privatem) {
		super();
		this.message = message;
		this.author = author;
		this.timestamp = timestamp;
		this.privatem = privatem;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ChatUser getAuthor() {
		return author;
	}
	
	public void setAuthor(ChatUser author) {
		this.author = author;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getPrivatem() {
		return privatem;
	}

	public void setPrivatem(String privatem) {
		this.privatem = privatem;
	}
}
/*
Показывать самого себя в списке
*/