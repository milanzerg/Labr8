package servlet;

import entity.ChatMessage;
import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected HashMap<String, ChatUser> activeUsers;// Карта текущих пользователей
	protected ArrayList<ChatMessage> messages;// Список сообщений чата
	@SuppressWarnings("unchecked")
	public void init() throws ServletException {
		super.init();// Вызвать унаследованную от HttpServlet версию init()
		activeUsers = (HashMap<String, ChatUser>)getServletContext().getAttribute("activeUsers");// Извлечь из контекста карту пользователей и список сообщений
		messages = (ArrayList<ChatMessage>)getServletContext().getAttribute("messages");
		if (activeUsers == null) {// Если карта пользователей не определена ...
			activeUsers = new HashMap<String, ChatUser>();// Создать новую карту
			getServletContext().setAttribute("activeUsers",	activeUsers);// Поместить её в контекст сервлета, чтобы другие сервлеты могли до него добраться
		}
		if (messages == null) {// Если список сообщений не определён ...
			messages = new ArrayList<ChatMessage>(100);// Создать новый список
			getServletContext().setAttribute("messages", messages);// Поместить его в контекст сервлета, чтобы другие сервлеты могли до него добрать
		}
	}

	//------------------------------------------------------------------------------------------------------------//
	/*
	public boolean checklogint(HttpServletRequest request, HttpServletResponse response) throws IOException{
		boolean b=false;
		for (ChatUser aUser: activeUsers.values()) {
			if(aUser.getName().equals((String)request.getSession().getAttribute("name"))){
				b=true;
			}
		}
		if(!b)
			response.sendRedirect(response.encodeRedirectURL("/lab8_war_exploded/login.do"));
		return b;
	}
	 */

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendRedirect(resp.encodeRedirectURL("/lab8_war_exploded/view.do"));
	}
	//------------------------------------------------------------------------------------------------------------//
}
