package servlet;

import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends ChatServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = (String) request.getSession().getAttribute("name");
		if (name != null) {// Если в сессии имеется имя пользователя...
			ChatUser aUser = activeUsers.get(name);// Получить объект, описывающий пользователя с таким именем
			// Если идентификатор сессии пользователя, вошедшего под этим именем, совпадает с идентификатором сессии
			// пользователя, пытающегося выйти из чата (т.е. выходит тот же, кто и входил)
			if (aUser != null && aUser.getSessionId().equals((String)request.getSession().getId())) {
				synchronized (activeUsers) {// Удалить пользователя из списка активных т.к. запросы обрабатываются одновременно, нужна синхронизация
					activeUsers.remove(name);
				}
				request.getSession().setAttribute("name", null);// Сбросить имя пользователя в сессии
				response.addCookie(new Cookie("sessionId", null));// Сбросить ID сессии в cookie
				response.sendRedirect(response.encodeRedirectURL("/lab8_war_exploded/"));// Перенаправить на главную страницу
				//-------------------------------------------------------------------------------------------------//
			} else {
				response.addCookie(new Cookie("sessionId", null));
				response.sendRedirect(response.encodeRedirectURL("/lab8_war_exploded/login.do"));
				//-------------------------------------------------------------------------------------------------//
			}
		}
		/*else {
			response.addCookie(new Cookie("sessionId", null));
			response.sendRedirect(response.encodeRedirectURL("/lab8_war_exploded/login.do"));// Перенаправить пользователя на главное окно чата
		}
		*/
	}
}
