package servlet;

import entity.ChatMessage;
import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;

public class NewMessageServlet extends ChatServlet {
	//
	int forFirstMessage = 0;
	String firstMessage = "Big Brother is watching you!";
	//
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// По умолчанию используется кодировка ISO-8859. Так как мы передаём данные в кодировке UTF-8, то необходимо установить соответствующую кодировку HTTP-запроса
		String pname = null;
		String message = (String)request.getParameter("message");// Извлечь из HTTP-запроса параметр 'message'
		if (message != null && !"".equals(message)) {// Если сообщение не пустое, то
			//--------------------------------------------------------------------------------------------------------//
			String privatem=(String)request.getSession().getAttribute("privatem");
			if(privatem != null && !"toall".equals(privatem))
				pname=privatem;
			//--------------------------------------------------------------------------------------------------------//
			ChatUser author = activeUsers.get((String)
					request.getSession().getAttribute("name"));
			synchronized (messages) {// Добавить в список сообщений новое
				//
				if (forFirstMessage == 0){
					messages.add(new ChatMessage(firstMessage, author, Calendar.getInstance().getTimeInMillis(),pname));
					forFirstMessage++;
				}
				//
				messages.add(new ChatMessage(message, author, Calendar.getInstance().getTimeInMillis(),pname));
			}
		}
		response.sendRedirect("/lab8_war_exploded/WebContent/compose_message.html");// Перенаправить пользователя на страницу с формой сообщения
	}
}