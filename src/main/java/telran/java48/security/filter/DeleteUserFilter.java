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

import lombok.RequiredArgsConstructor;
import telran.java48.accounting.dao.UserAccountRepository;
import telran.java48.accounting.model.UserAccount;
import telran.java48.security.model.Role;
import telran.java48.security.model.User;

@Component
@Order(40)
@RequiredArgsConstructor
public class DeleteUserFilter implements Filter {
	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
	

//prover metod i put
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			User user = (User) request.getUserPrincipal();
			// poluhim ves put v massiv
			String[] arr = request.getServletPath().split("/");
			String userName = arr[arr.length - 1];
			
			
			if (!(user.getName().equalsIgnoreCase(userName) 
					|| user.getRoles().contains(Role.ADMINISTRATOR))) {
				response.sendError(403);
			
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String servletPath) {
		// TODO Auto-generated method stub
		return HttpMethod.DELETE.matches(method) && servletPath.matches("/account/user/\\w+/?");
	}

}
