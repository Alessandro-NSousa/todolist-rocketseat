package br.com.alessandro.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository useRepository;


    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        UserModel user =this.useRepository.findByUsername(userModel.getUsername());

        if(user != null){
            //mensagem de erro
            //status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("usuário já existe");
        }
        String passwordHashred = BCrypt.withDefaults()
        .hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHashred);

       UserModel userCreated = this.useRepository.save(userModel);
       return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);

    }
    
}
