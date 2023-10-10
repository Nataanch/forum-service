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
			Principal principal = request.getUserPrincipal();
			// poluhim ves put v massiv
			String[] arr = request.getServletPath().split("/");
			String user = arr[arr.length - 1];
			UserAccount userAccount = userAccountRepository.findById(principal.getName()).get();
			if (!principal.getName().equalsIgnoreCase(user) && !userAccount.getRoles().contains("ADMINISTRATOR")) {
				response.sendError(403);
			
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private boolean checkEndPoint(String method, String servletPath) {
		// TODO Auto-generated method stub
		return HttpMethod.DELETE.matches(method) && servletPath.matches("/user/\\w+/?");
	}

}
