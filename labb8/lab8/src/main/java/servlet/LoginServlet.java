package servlet;

import entity.ChatUser;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class LoginServlet extends ChatServlet {	
	private static final long serialVersionUID = 1L;
	private int sessionTimeout = 10 * 60;// Длительность сессии, в секундах
	public void init() throws ServletException {
		super.init();
		String value = getServletConfig().getInitParameter("SESSION_TIMEOUT");// Прочитать из конфигурации значение параметра SESSION_TIMEOUT
		if (value!=null) {// Если он задан, переопределить длительность сессии по умолчанию
			sessionTimeout = Integer.parseInt(value);
		}
	}

	// Метод будет вызван при обращении к сервлету HTTP-методом GET, т.е. когда пользователь просто открывает адрес в браузере
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {// Проверить, есть ли уже в сессии заданное имя пользователя?
		String name = (String)request.getSession().getAttribute("name");
		String errorMessage = (String)request.getSession().getAttribute("error");// Извлечь из сессии сведения о предыдущей ошибке (возможной)
		String previousSessionId = null;// Идентификатор предыдущей сессии изначально пуст
		if (name==null && request.getCookies()!=null) {// Если в сессии имя не сохранено, то попытаться восстановить имя через cookie //тут дописаны печеньки
				for (Cookie aCookie: request.getCookies()) {
					if (aCookie.getName().equals("sessionId")) {
						previousSessionId = aCookie.getValue();// Запомнить значение этого cookie – это старый идентификатор сессии
						break;
					}
				}
			
			if (previousSessionId!=null) {// Мы нашли session cookie. Попытаться найти пользователя с таким sessionId
				for (ChatUser aUser: activeUsers.values()) {
					if
					(aUser.getSessionId().equals(previousSessionId)) {
						name = aUser.getName();// Мы нашли такого, т.е. восстановили имя
						aUser.setSessionId(request.getSession().getId());
					}
				}
			}
		}
		if (name!=null && !"".equals(name)) {// Если в сессии имеется не пустое имя пользователя, то...
			errorMessage = processLogonAttempt(name, request, response);
		}

		response.setCharacterEncoding("utf8");// Пользователю необходимо ввести имя. Показать форму. Задать кодировку HTTP-ответа
		PrintWriter pw = response.getWriter();// Получить поток вывода для HTTP-ответа
		pw.println("<!DOCTYPE html>\n<html><head><title>Чат</title>" + "<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>" + "</head>");
		if (errorMessage!=null) {// Если возникла ошибка - сообщить о ней
			pw.println("<p><font color='red'>" + errorMessage + "</font></p>");
		}
		// Вывести форму
		pw.println("<form action='/lab8_war_exploded/' method='post'>Введите имя: <input type='text' name='name' value=''>" +
				"<input type='submit' value='Войти в чат'>");
				pw.println("</form></body></html>");
				request.getSession().setAttribute("error", null);// Сбросить сообщение об ошибке в сессии
	}

	// Метод будет вызван при обращении к сервлету HTTP-методом POST, т.е. когда пользователь отправляет сервлету данные
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8"); // Задать кодировку HTTP-запроса - очень важно! Иначе вместо символов будет абракадабра
		String name = (String)request.getParameter("name");// Извлечь из HTTP-запроса значение параметра 'name'
		String errorMessage = null;// Полагаем, что изначально ошибок нет
		if (name==null || "".equals(name)) {// Пустое имя недопустимо - сообщить об ошибке
			errorMessage = "Name can't be empty!";
		} else {
			errorMessage = processLogonAttempt(name, request, response);// Если имя не пустое, то попытаться обработать запрос
		}
		if (errorMessage != null) {
			request.getSession().setAttribute("name", null);// Сбросить имя пользователя в сессии
			request.getSession().setAttribute("error", errorMessage);// Сохранить в сессии сообщение об ошибке
			response.sendRedirect(response.encodeRedirectURL("/lab8_war_exploded/view.do"));// Переадресовать обратно на исходную страницу с формой
		}
	}

	String processLogonAttempt(String name, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String sessionId = request.getSession().getId();// Определить идентификатор Java-сессии пользователя
		ChatUser aUser = activeUsers.get(name);// Извлечь из списка объект, связанный с этим именем
		if (aUser == null) {
			aUser = new ChatUser(name, Calendar.getInstance().getTimeInMillis(), sessionId);// Если оно свободно, то добавить нового пользователя в список активных
			synchronized (activeUsers) {// Так как одновременно выполняются запросы от множества пользователей, то необходима синхронизация на ресурсе
				activeUsers.put(aUser.getName(), aUser);
			}
		}
		if (aUser.getSessionId().equals(sessionId) || aUser.getLastInteractionTime() < (Calendar.getInstance().getTimeInMillis() - sessionTimeout * 1000)) {
			// Если указанное имя принадлежит текущему пользователю, либо оно принадлежало кому-то другому, но сессия истекла,
			// то одобрить запрос пользователя на это имя. Обновить имя пользователя в сессии
			request.getSession().setAttribute("name", name);
			aUser.setLastInteractionTime(Calendar.getInstance().getTimeInMillis());// Обновить время взаимодействия пользователя с сервером
			Cookie sessionIdCookie = new Cookie("sessionId", sessionId);// Обновить идентификатор сессии пользователя в cookies
			sessionIdCookie.setMaxAge(60 * 60 * 24 * 365);// Установить срок годности cookie 1 год
			response.addCookie(sessionIdCookie);// Добавить cookie в HTTP-ответ
			//----------------------------------------------------------------------------------------------------------//
			request.getSession().setAttribute("privatem", null);
			//----------------------------------------------------------------------------------------------------------//
			response.sendRedirect(response.encodeRedirectURL("/lab8_war_exploded/view.do"));// Перейти к главному окну чата
			return null;// Вернуть null, т.е. сообщений об ошибках нет
		} else {
			// Сохранённое в сессии имя уже закреплено за кем-то другим. Извиниться, отказать и попросить ввести другое имя
			return "Извините, но имя <strong>" + name + "</strong> уже кем-то занято. Пожалуйста выберите другое имя!";
		}
	}
}
