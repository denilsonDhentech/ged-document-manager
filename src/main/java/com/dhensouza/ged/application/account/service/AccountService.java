package com.dhensouza.ged.application.account.service;

import com.dhensouza.ged.api.controller.account.dto.request.CreateAccountRequest;
import com.dhensouza.ged.api.controller.account.dto.request.UpdateAccountRequest;
import com.dhensouza.ged.application.account.dto.response.AccountResponse;
import com.dhensouza.ged.domain.entity.Account;
import com.dhensouza.ged.domain.exception.BusinessRuleException;
import com.dhensouza.ged.domain.exception.EntityNotFoundException;
import com.dhensouza.ged.domain.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;


public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        log.info("Tentativa de criação de nova conta: usuário '{}', perfil '{}'", request.username(), request.role());

        if (repository.findByUsername(request.username()).isPresent()) {
            log.warn("Falha na criação: o nome de usuário '{}' já está em uso", request.username());
            throw new BusinessRuleException("Username already exists.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Account account = Account.create(
                request.username(),
                encodedPassword,
                request.role(),
                request.tenantId()
        );

        Account savedAccount = repository.save(account);
        log.info("Conta criada com sucesso! ID: {}, Tenant: {}", savedAccount.getId(), savedAccount.getTenantId());

        return AccountResponse.fromEntity(savedAccount);
    }

    public List<AccountResponse> findAll() {
        log.debug("Listando todas as contas do sistema");
        return repository.findAll()
                .stream()
                .map(AccountResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AccountResponse update(UUID id, UpdateAccountRequest request) {
        log.info("Iniciando atualização da conta ID: {}", id);

        Account account = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Falha na atualização: conta com ID {} não encontrada", id);
                    return new EntityNotFoundException("User not found");
                });

        if (!account.getUsername().equals(request.username())) {
            log.debug("Alteração de username detectada: de '{}' para '{}'", account.getUsername(), request.username());
            repository.findByUsername(request.username()).ifPresent(u -> {
                log.warn("Falha na atualização: novo username '{}' já existe no sistema", request.username());
                throw new BusinessRuleException("Username already exists.");
            });
            account.setUsername(request.username());
        }

        account.changeRole(request.role());
        account.changeTenantId(request.tenantId());

        if (request.password() != null && !request.password().isBlank()) {
            log.debug("Atualizando senha para o usuário: {}", request.username());
            account.setPassword(passwordEncoder.encode(request.password()));
        }

        Account updated = repository.save(account);
        log.info("Conta {} atualizada com sucesso", id);

        return AccountResponse.fromEntity(updated);
    }

    public void delete(UUID id) {
        log.info("Solicitação de exclusão para a conta ID: {}", id);

        if (!repository.existsById(id)) {
            log.warn("Tentativa de exclusão falhou: ID {} inexistente", id);
            throw new EntityNotFoundException("User not found with ID: " + id);
        }

        repository.deleteById(id);
        log.info("Conta ID: {} excluída permanentemente", id);
    }
}
