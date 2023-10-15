package telran.java48.security.filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Patch;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Order(30)
public class UpdateByOwnerFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		// zdes sveraem cho user iz filter 1 iz zagolovka == user v puti
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			// principala poluchili v 1 filter
			Principal principal = request.getUserPrincipal();
			// poluhim ves put v massiv
			String[] arr = request.getServletPath().split("/");
			// tak kak user v puti poslednii
			String user = arr[arr.length - 1];
		
			// esli user name !=name principal
			if (!principal.getName().equalsIgnoreCase(user)) {
				response.sendError(403);
				System.out.println("False in 30");
				return;
			}
		}
		chain.doFilter(request, response);

	}

	private boolean checkEndPoint(String method, String servletPath) {
		
		return 
		    (HttpMethod.PUT.matches(method) && servletPath.matches("/account/user/\\w+/?"))
		||	(HttpMethod.POST.matches(method) && servletPath.matches("/forum/post/\\w+/?"))
		||	(HttpMethod.PUT.matches(method) && servletPath.matches("/forum/post/\\w+/comment/\\w+/?"));
		
	}

}
