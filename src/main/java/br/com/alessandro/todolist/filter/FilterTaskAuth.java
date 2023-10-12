package br.com.alessandro.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import br.com.alessandro.todolist.user.IUserRepository;
import br.com.alessandro.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                String servletPath = request.getServletPath();
                if(servletPath.startsWith("/tasks/")) {  //startWith indica que o prefixo incia com um nome especifico desconsiderando o que vem a seguir

                    String authorization = request.getHeader("Authorization");
            
                String auth_encoded = authorization.substring("Basic".length()).trim();
                byte[] authDecoded = Base64.getDecoder().decode(auth_encoded);
                String authString = new String(authDecoded);

                String[] credentials = authString.split(":");
                String username = credentials[0];
                String password = credentials[1];

                //valida usuario
                UserModel user = this.userRepository.findByUsername(username);
                if(user == null){
                    response.sendError(401);
                }else{
                    //valida senha
                    Result passwordVerify =  BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                    if(passwordVerify.verified){
                        request.setAttribute("idUser", user.getId());
                        filterChain.doFilter(request, response);
                    }else{
                        response.sendError(401);
                    }
                }
                }else{
                    filterChain.doFilter(request, response);
                }
                //pagar a autenticação
                
                
                
        
    }

    
}
