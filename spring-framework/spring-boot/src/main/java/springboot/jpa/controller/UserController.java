package springboot.jpa.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.jpa.entity.User;
import springboot.jpa.repository.UserRepository;

@RequestMapping("user")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("getAllUsers")
    public Page<User> getAllUsers(Pageable request) {
        Page<User> all = userRepository.findAll(request);

        System.out.println(all.getContent());

        return all;
    }

}
