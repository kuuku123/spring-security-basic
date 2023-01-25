package com.example.corespringsecurity.security.init;

import com.example.corespringsecurity.domain.entity.*;
import com.example.corespringsecurity.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ResourcesRepository resourcesRepository;

    private final RoleHierarchyRepository roleHierarchyRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccessIpRepository accessIpRepository;

    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        setupSecurityResources();

        alreadySetup = true;
    }



    private void setupSecurityResources() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles.add(adminRole);
        createResourceIfNotFound("/admin/**", "", roles, "url");
        Account account = createUserIfNotFound("admin", "1111", "admin@gmail.com", 10,  roles);
        
        Set<Role> roles1 = new HashSet<>();

        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        roles1.add(managerRole);
        createResourceIfNotFound("com.example.corespringsecurity.aopsecurity.AopMethodService.methodSecured", "", roles1, "method");
//        createResourceIfNotFound("io.security.corespringsecurity.aopsecurity.method.AopMethodService.innerCallMethodTest", "", roles1, "method");
        createResourceIfNotFound("execution(* io.security.corespringsecurity.aopsecurity.pointcut.*Service.*(..))", "", roles1, "pointcut");
        createUserIfNotFound("manager", "1111", "manager@gmail.com", 20, roles1);

        Set<Role> roles3 = new HashSet<>();

        Role childRole1 = createRoleIfNotFound("ROLE_USER", "회원");
        roles3.add(childRole1);
        createResourceIfNotFound("/users/**", "", roles3, "url");
        createResourceIfNotFound("/mypage/**", "", roles3, "url");

        createUserIfNotFound("user", "1111", "user@gmail.com", 30, roles3);

        createRoleHierarchyIfNotFound(childRole1,managerRole);
        createRoleHierarchyIfNotFound(managerRole,adminRole);
    }

    @Transactional
    public Role createRoleIfNotFound(String roleName, String roleDesc) {

        Role role = roleRepository.findByRoleName(roleName);

        if (role == null) {
            role = Role.builder()
                    .roleName(roleName)
                    .roleDesc(roleDesc)
                    .build();
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Account createUserIfNotFound(String userName, String password, String email, int age, Set<Role> roleSet) {

        Account account = userRepository.findByUsername(userName);
        Set<AccountRole> accountRoles = new HashSet<>();
        if (account == null) {
            account = Account.builder()
                    .username(userName)
                    .email(email)
                    .age(age)
                    .password(passwordEncoder.encode(password))
                    .accountRoles(accountRoles)
                    .build();
        }
        for (Role role : roleSet) {
            AccountRole build = AccountRole.builder()
                    .account(account)
                    .role(role)
                    .build();
            accountRoles.add(build);
        }
        return userRepository.save(account);
    }

    @Transactional
    public Resources createResourceIfNotFound(String resourceName, String httpMethod, Set<Role> roleSet, String resourceType) {
        Resources resources = resourcesRepository.findByResourceNameAndHttpMethod(resourceName, httpMethod);
        Set<ResourcesRole> resourcesRoles = new HashSet<>();
        if (resources == null) {
            resources = Resources.builder()
                    .resourceName(resourceName)
                    .resourcesRoles(resourcesRoles)
                    .httpMethod(httpMethod)
                    .resourceType(resourceType)
                    .orderNum(count.incrementAndGet())
                    .build();
        }
        for (Role role : roleSet) {
            ResourcesRole build = ResourcesRole.builder()
                    .resources(resources)
                    .role(role)
                    .build();
            resourcesRoles.add(build);
        }
        return resourcesRepository.save(resources);
    }

    @Transactional
    public void createRoleHierarchyIfNotFound(Role childRole, Role parentRole) {
        RoleHierarchy roleHierarchy = roleHierarchyRepository.findByChildName(parentRole.getRoleName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .childName(parentRole.getRoleName())
                    .build();
        }
        RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        roleHierarchy = roleHierarchyRepository.findByChildName(childRole.getRoleName());
        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .childName(childRole.getRoleName())
                    .build();
        }
        RoleHierarchy childRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);
        childRoleHierarchy.setParentName(parentRoleHierarchy);
    }

    private void setupAccessIpData() {
        AccessIp byIpAddress = accessIpRepository.findByIpAddress("0:0:0:0:0:0:0:1");
        if (byIpAddress == null) {
            AccessIp accessIp = AccessIp.builder()
                    .ipAddress("0:0:0:0:0:0:0:1")
                    .build();
            accessIpRepository.save(accessIp);
        }
    }

}