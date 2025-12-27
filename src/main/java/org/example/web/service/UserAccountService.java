package org.example.web.service;

import lombok.RequiredArgsConstructor;
import org.example.web.data.entity.UserAccount;
import org.example.web.data.pojo.UserAccountDTO;
import org.example.web.data.request.UserInfo;
import org.example.web.mapper.UserAccountMapper;
import org.example.web.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public Page<UserAccountDTO> filter(
            String username,
            String fullName,
            String email,
            String role,
            Pageable pageable
    ) {

        List<Specification<UserAccount>> specs = new ArrayList<>();

        if (username != null && !username.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("username")),
                            "%" + username.toLowerCase() + "%"
                    )
            );
        }

        if (fullName != null && !fullName.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("fullName")),
                            "%" + fullName.toLowerCase() + "%"
                    )
            );
        }

        if (email != null && !email.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("email")),
                            "%" + email.toLowerCase() + "%"
                    )
            );
        }

        if (role != null && !role.isBlank()) {
            specs.add((root, query, cb) ->
                    cb.equal(root.get("role"), role)
            );
        }

        Specification<UserAccount> specification =
                Specification.allOf(specs);

        return userAccountRepository
                .findAll(specification, pageable)
                .map(UserAccountMapper::toDTO);
    }


    public UserAccount findByEmail(String email) {
        return userAccountRepository.findByEmail(email).orElse(null);
    }

    public UserAccount findById(Long id) {
        return userAccountRepository.findById(id).orElse(null);
    }

    public UserAccount update(Long id, UserAccount request) {
        UserAccount user = findById(id);

        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }

        return userAccountRepository.save(user);
    }

    public UserAccountDTO findByUsername(String username) {
        return userAccountRepository.findByUsername(username)
                .map(UserAccountMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserAccount findEntityById(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ===== UPDATE CURRENT USER =====
    public UserAccountDTO updateInfo(Long id, UserInfo request) {
        UserAccount user = findEntityById(id);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }

        if (request.getEmail() != null &&
                !request.getEmail().equalsIgnoreCase(user.getEmail())) {

            if (userAccountRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            user.setEmail(request.getEmail());
        }

        return UserAccountMapper.toDTO(
                userAccountRepository.save(user)
        );
    }

    public void  delete(UserAccount userAccount)
    {
        userAccountRepository.delete(userAccount);
    }
}
