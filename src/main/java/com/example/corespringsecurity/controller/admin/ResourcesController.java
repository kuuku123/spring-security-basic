package com.example.corespringsecurity.controller.admin;


import com.example.corespringsecurity.domain.dto.ResourcesDto;
import com.example.corespringsecurity.domain.entity.Resources;
import com.example.corespringsecurity.domain.entity.ResourcesRole;
import com.example.corespringsecurity.domain.entity.Role;
import com.example.corespringsecurity.repository.RoleRepository;
import com.example.corespringsecurity.service.ResourcesService;
import com.example.corespringsecurity.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ResourcesController {
	
	@Autowired
	private ResourcesService resourcesService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RoleService roleService;


	@GetMapping(value="/admin/resources")
	public String getResources(Model model) throws Exception {

		List<Resources> resources = resourcesService.getResources();
		model.addAttribute("resources", resources);

		return "admin/resource/list";
	}

	@PostMapping(value="/admin/resources")
	public String createResources(ResourcesDto resourcesDto) throws Exception {

		ModelMapper modelMapper = new ModelMapper();
		Resources resources = modelMapper.map(resourcesDto, Resources.class);
		Role role = roleRepository.findByRoleName(resourcesDto.getRoleName());
		Set<ResourcesRole> resourcesRoles = new HashSet<>();
		ResourcesRole build = ResourcesRole.builder()
				.resources(resources)
				.role(role)
				.build();
		resourcesRoles.add(build);
		resources.setResourcesRoles(resourcesRoles);

		resourcesService.createResources(resources);
		resourcesService.updateResources();

		return "redirect:/admin/resources";
	}

	@GetMapping(value="/admin/resources/register")
	public String viewRoles(Model model) throws Exception {

		List<Role> roleList = roleService.getRoles();
		model.addAttribute("roleList", roleList);

		ResourcesDto resources = new ResourcesDto();
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(new Role());
		resources.setRoleSet(roleSet);
		model.addAttribute("resources", resources);

		return "admin/resource/detail";
	}

	@GetMapping(value="/admin/resources/{id}")
	public String getResources(@PathVariable String id, Model model) throws Exception {

		List<Role> roleList = roleService.getRoles();
        model.addAttribute("roleList", roleList);
		Resources resources = resourcesService.getResources(Long.valueOf(id));

		ResourcesDto resourcesDto = getResourcesDto(resources);
		model.addAttribute("resources", resourcesDto);

		return "admin/resource/detail";
	}

	@GetMapping(value="/admin/resources/delete/{id}")
	public String removeResources(@PathVariable String id, Model model) throws Exception {

		Resources resources = resourcesService.getResources(Long.valueOf(id));
		resourcesService.deleteResources(Long.valueOf(id));
		resourcesService.updateResources();

		return "redirect:/admin/resources";
	}

	private ResourcesDto getResourcesDto(Resources resources) {

		ResourcesDto build = ResourcesDto.builder()
				.id(String.valueOf(resources.getId()))
				.resourceName(resources.getResourceName())
				.httpMethod(resources.getHttpMethod())
				.orderNum(resources.getOrderNum())
				.resourceType(resources.getResourceType())
				.roleName(resources.getResourceName())
				.roleSet(resources.getResourcesRoles().stream()
						.map(resourcesRole -> resourcesRole.getRole())
						.collect(Collectors.toSet()))
				.build();
		return build;
	}
}
