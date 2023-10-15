package telran.java48.security.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

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
import telran.java48.post.dao.PostRepository;
import telran.java48.post.model.Post;
import telran.java48.security.model.Role;
import telran.java48.security.model.User;

@Component
@Order(60)
@RequiredArgsConstructor
public class DeletePostByOwnerByModerator implements Filter {
	final PostRepository postRepository;
	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			User user = (User) request.getUserPrincipal();

			String[] arr = request.getServletPath().split("/");
			// poluchim id
			String id = arr[arr.length - 1];
			// po id naidem ves post
			Optional<Post> post = postRepository.findById(id);

			if (!(user.getName().equalsIgnoreCase(post.get().getAuthor()) || !user.getRoles().contains(Role.MODERATOR))) {
				response.sendError(403, "Permission delete post denied");

				return;
			}

			chain.doFilter(request, response);
		}

	}

	private boolean checkEndPoint(String method, String servletPath) {
		return HttpMethod.DELETE.matches(method) && servletPath.matches("/forum/post/\\w+/?");
	}
}
