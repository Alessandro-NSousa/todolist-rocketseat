package br.com.alessandro.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alessandro.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity created(@RequestBody TaskModel taskModel, HttpServletRequest request){
        
        Object idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        LocalDateTime currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) ||currentDate.isAfter(taskModel.getEndAt()) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de Início / data de término deve ser igual ou maior que a Data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt()) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de Início deve ser menor do que a data de término");
        }

        TaskModel task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);

    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        Object idUser = request.getAttribute("idUser");
        List<TaskModel> tasks = this.taskRepository.findByIdUser((UUID)idUser);
        return tasks;
        
    }
    
    @PutMapping("/{id}")
    public TaskModel update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        //Object idUser = request.getAttribute("idUser");
        TaskModel task = this.taskRepository.findById(id).orElse(null);
        Utils.copyNonNullProperties(taskModel, task);
        //taskModel.setIdUser((UUID)idUser); posso precisar para meu projeto
        //taskModel.setId(id);
        return this.taskRepository.save(task);
    }
}
