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

import telran.java48.post.dao.PostRepository;
import telran.java48.post.model.Post;

@Component
@Order(50)
@RequiredArgsConstructor
public class UpdatePostById implements Filter {
	final PostRepository postRepository;
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		
		if (checkEndPoint(request.getMethod(), request.getServletPath())) {
			
			Principal principal = request.getUserPrincipal();
			
			String[] arr = request.getServletPath().split("/");
			// poluchim id
			String id = arr[arr.length - 1];
			//po id naidem ves post
			Optional<Post> post = postRepository.findById(id);
			if (post ==null) {
				response.sendError(404);
			}
			if(!principal.getName().equalsIgnoreCase(post.get().getAuthor())) {
				response.sendError(403, "Permission post denied");
				
				return;
			}		
			
		}
		chain.doFilter(request, response);
		
		
	}
	
	private boolean checkEndPoint(String method, String servletPath) {
		return HttpMethod.PUT.matches(method) && servletPath.matches("/forum/post/\\w+/?");
	}

}
