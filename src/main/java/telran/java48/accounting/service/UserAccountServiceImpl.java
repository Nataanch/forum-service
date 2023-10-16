package telran.java48.accounting.service;


import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java48.accounting.dao.UserAccountRepository;
import telran.java48.accounting.dto.RolesDto;
import telran.java48.accounting.dto.UserDto;
import telran.java48.accounting.dto.UserEditDto;
import telran.java48.accounting.dto.UserRegisterDto;
import telran.java48.accounting.dto.exeptions.UserExistsExeption;
import telran.java48.accounting.model.UserAccount;
import telran.java48.post.dto.exceptions.UserNotFoundExeption;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService,CommandLineRunner {

	final UserAccountRepository userAccountRepository;
	final ModelMapper modelMapper;
	final PasswordEncoder passwordEncoder;

	@Override
	public UserDto register(UserRegisterDto userRegisterDto) {
		if (userAccountRepository.existsById(userRegisterDto.getLogin())) {
			throw new UserExistsExeption();
		}
		UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
		userAccount.addRole("USER");
		String passwordString = passwordEncoder.encode(userRegisterDto.getPassword());	
		userAccount.setPassword(passwordString);
				userAccountRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto getUser(String login) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto removeUser(String login) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		userAccountRepository.delete(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public UserDto updateUser(String login, UserEditDto userEditDto) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		if (userEditDto.getFirstName() != null) {
			userAccount.setFirstName(userEditDto.getFirstName());

		}
		if (userEditDto.getLastName() != null) {
			userAccount.setLastName(userEditDto.getLastName());
		}
		userAccountRepository.save(userAccount);
		return modelMapper.map(userAccount, UserDto.class);
	}

	@Override
	public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		boolean res;
		// tak kak set - on udalaet ili true ili false
		if (isAddRole) {
			res = userAccount.addRole(role.toUpperCase());
		} else {
			res = userAccount.removeRole(role.toUpperCase());
		}
		if (res) {
			userAccountRepository.save(userAccount);
		}
		return modelMapper.map(userAccount, RolesDto.class);
	}

	@Override
	public void changePassword(String login, String newPassword) {
		UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundExeption::new);
		String passwordString = passwordEncoder.encode(newPassword);	
		userAccount.setPassword(passwordString);
		userAccountRepository.save(userAccount);

	}

	//teper pri starte appl - poyavitsa admin
	@Override
	public void run(String... args) throws Exception {
		String password = passwordEncoder.encode("admin");
		if(!userAccountRepository.existsById("admin")) {
			UserAccount userAccount = new UserAccount("admin", password, "", "");
			userAccount.addRole("USER");
			userAccount.addRole("ADMINISTRATOR");
			userAccount.addRole("MODERATOR");
			userAccountRepository.save(userAccount);
		}
		
	}

}
