package ori.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ori.common.enums.UserRole;
import ori.entity.Roles;
import ori.entity.User;
import ori.repository.RoleRepository;
import ori.repository.UserRepository;
import ori.utils.AppUtil;

import javax.management.relation.Role;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(("/api/auth"))
public class AuthAPIController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AppUtil appUtil;

    @GetMapping("/role")
    public ResponseEntity role() {
        Roles role =Roles.builder()
                .role(UserRole.USER)
                .build();
        Roles roleAdmin =Roles.builder()
                .role(UserRole.ADMIN)
                .build();
        roleRepository.save(role);
        roleRepository.save(roleAdmin);
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @PostMapping("/sign-up")
    public ResponseEntity signup(@RequestBody User user) {
        System.out.println(user);
        user.setPasswordSalt(appUtil.generateSalt());
        user.setPasswordHash(appUtil.generatePasswordHash(user.getPasswordHash(), user));
        Roles role = roleRepository.findById(1).orElse(null);
        Set<Roles> set = new HashSet<>();
        set.add(role);
        user.setRoles(set);
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
