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

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;


@Component
@Order(70)
public class addCommentFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			// principala poluchili v 1 filter
			Principal principal = request.getUserPrincipal();
			// poluhim ves put v massiv
			String[] arr = request.getServletPath().split("/");
			// tak kak user v puti poslednii
			String autor = arr[arr.length - 1];
			if(!principal.getName().equalsIgnoreCase(autor)) {
				response.sendError(403);
				
				return;
			}	
		}
		
		chain.doFilter(request, response);
		

	}
	private boolean checkEndPoint(String method, String servletPath) {
	
		return HttpMethod.PUT.matches(method) && ((servletPath.matches("/post/\\w+/comment/\\w+/?"))|| (servletPath.matches("/post/\\w+/?")));

	}

}

