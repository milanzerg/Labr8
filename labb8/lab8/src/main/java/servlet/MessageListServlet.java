package servlet;

import entity.ChatMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MessageListServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf8");// Установить кодировку HTTP-ответа UTF-8
		PrintWriter pw = response.getWriter();// Получить доступ к потоку вывода HTTP-ответа
		// Записть в поток HTML-разметку страницы
		pw.println("<!DOCTYPE html>\n<html>" +
				"<head>" +
				"<title></title>"+
				"<meta charset='UTF-8' />" +
				"<meta http-equiv='refresh' content='1'>" +
				"</head>");
		pw.println("<body>");
		//---------------------------------------------------------------------------------------------------------//
		pw.println("<div><strong>" + (String)request.getSession().getAttribute("name") + "</strong></div>"); // при удалении не выводит жирно имя
		//---------------------------------------------------------------------------------------------------------//

		// Если это убрать, то автор:сообщение не будет выводится
		    for (int i = messages.size() - 1; i >= 0; i--) {// В обратном порядке записать в поток HTML-разметку для каждого сообщения
			ChatMessage aMessage = messages.get(i);
			String priv = messages.get(i).getPrivatem();
			String auth = messages.get(i).getAuthor().getName();
			String uname = (String)request.getSession().getAttribute("name");
			if(priv != null){
				if(priv.equals(uname) || auth.equals(uname)){
					pw.println("<div><strong>" + aMessage.getAuthor().getName() + "</strong>: " + aMessage.getMessage() + "</div>");
				}
			}else{
				pw.println("<div><strong>" + aMessage.getAuthor().getName() + "</strong>: " + aMessage.getMessage() + "</div>");
			}
		}
		pw.println("</body></html>");
	}
}