package ori.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ori.config.scurity.AuthUser;
import ori.entity.User;
import ori.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {
	@Autowired
	UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public <S extends User> S save(S entity) {
		return userRepository.save(entity);
	}

	@Override
	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findById(Integer id) {
		return userRepository.findById(id);
	}

	@Override
	public long count() {
		return userRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		userRepository.deleteById(id);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User updateUser(User model) {
		Integer userId = model.getUserId();
		if (userId != null) {
			Optional<User> userOptional = userRepository.findById(userId);
			if (userOptional.isPresent()) {
				User user = userOptional.get();

				if (model.getFullName() != null && !model.getFullName().isEmpty()) {
					user.setFullName(model.getFullName());
				}
				if (model.getUsername() != null && !model.getUsername().isEmpty()) {
					user.setUsername(model.getUsername());
				}
				if (model.getEmail() != null && !model.getEmail().isEmpty()) {
					user.setEmail(model.getEmail());
				}
				if (model.getAddress() != null && !model.getAddress().isEmpty()) {
					user.setAddress(model.getAddress());
				}
				if (model.getPhone() != null && !model.getPhone().isEmpty()) {
					user.setPhone(model.getPhone());
				}
				model.setPasswordHash(user.getPasswordHash());

				return userRepository.save(user);
			}
		}
		return null;
	}

	@Override
	public User updateAddress(String email, String newAddress) {

		User user = userRepository.findByEmail(email).get();
		user.setAddress(newAddress);
		return userRepository.save(user);
	}

	@Override
	public Page<User> getAll(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 10);
		return userRepository.findAll(pageable);
	}

	public User login(String email, String passwd) {
		User user = userRepository.findByEmail(email).get();
		if (user != null && passwd.equals(user.getPasswordHash())) {
			return user;
		}
		return null;
	}

	@Override
	public Optional<User> getByUserNameOrEmail(String username) {
		return userRepository.findByUsernameOrEmail(username);
	}

	@Override
	public User getUserLogged() {
		Object authen = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (authen instanceof AuthUser) {
			String email = ((AuthUser)authen).getEmail();
			Optional<User> optUser = findByEmail(email);
			if (optUser.isPresent()) {
				User user = optUser.get();
				return user;
			}
		}
		return null;
	}
}