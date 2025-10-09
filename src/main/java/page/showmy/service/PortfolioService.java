package page.showmy.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import page.showmy.dto.*;
import page.showmy.exception.ResourceNotFoundException;
import page.showmy.model.*;
import page.showmy.repository.*;
import page.showmy.repository.WorkExperienceRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PublicationRepository publicationRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final SkillRepository skillRepository;

    @Autowired
    private RedisService redisService;

    public PortfolioService(UserRepository userRepository, ProjectRepository projectRepository, PublicationRepository publicationRepository, SkillRepository skillRepository, WorkExperienceRepository workExperienceRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.publicationRepository = publicationRepository;
        this.workExperienceRepository = workExperienceRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioDTO getPortfolioByUsername(String username){
        try {
            PortfolioDTO cachedPortfolio = redisService.get(username, PortfolioDTO.class);
            if (cachedPortfolio != null) {
                return cachedPortfolio;
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException(username);
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found: "+username));
        UserProfileDTO userProfileDTO = UserProfileDTO.fromEntities(user, user.getUserProfile());

        Hibernate.initialize(user.getPublications());
        List<Project> projects = user.getProjects();
        projects.forEach(project -> project.getSkills().size());
        Map<SkillsCategory, List<Skill>> skillsByCategory = user.getSkills().stream()
                .collect(Collectors.groupingBy(Skill::getSkillsCategory));

        List<SkillsCategoryDTO> skillsDataDTO = skillsByCategory.entrySet().stream()
                .map(entry -> new SkillsCategoryDTO(
                        entry.getKey().getId(),
                        entry.getKey().getCategory(),
                        entry.getValue().stream().map(SkillDTO::fromEntity).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        PortfolioDTO portfolioFromDb = new PortfolioDTO(
                userProfileDTO,
                new ArrayList<>(user.getProjects()),
                skillsDataDTO,
                new ArrayList<>(user.getPublications()),
                new ArrayList<>(user.getWorkExperiences())
        );

        redisService.set(username, portfolioFromDb, 1800L);

        return portfolioFromDb;

    }

    @Transactional(readOnly = true)
    public List<UserProfileDTO> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(user -> UserProfileDTO.fromEntities(user, user.getUserProfile()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfileByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found: "+username));
        return UserProfileDTO.fromEntities(user, user.getUserProfile());
    }

    @Transactional
    public UserProfileDTO updateUserProfile(String username, UserProfileInputDTO profileInput) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setUserProfile(profile);
            profile.setUser(user);
        }

        if (profileInput.getName() != null) profile.setName(profileInput.getName());
        if (profileInput.getTitle() != null) profile.setTitle(profileInput.getTitle());
        if (profileInput.getResumeUrl() != null) profile.setResumeUrl(profileInput.getResumeUrl());
        if (profileInput.getProfilePhoto() != null) profile.setProfilePhoto(profileInput.getProfilePhoto());
        if (profileInput.getGithub() != null) profile.setGithub(profileInput.getGithub());
        if (profileInput.getLinkedin() != null) profile.setLinkedin(profileInput.getLinkedin());

        userRepository.save(user);
        return UserProfileDTO.fromEntities(user, profile);
    }

    @Transactional
    public Project addProject(String username, ProjectInputDTO projectInput) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Project project = new Project();
        project.setUser(user);

        updateProjectEntity(project, projectInput);

        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long projectId, String username, ProjectInputDTO projectInput) {
        if (projectInput == null) {
            throw new IllegalArgumentException("Project input cannot be null.");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to edit this project.");
        }

        updateProjectEntity(project, projectInput);

        return projectRepository.save(project);
    }

    private void updateProjectEntity(Project project, ProjectInputDTO projectInput) {
        if (projectInput.getName() != null) {
            project.setName(projectInput.getName());
        }
        if (projectInput.getDescription() != null) {
            project.setDescription(projectInput.getDescription());
        }
        if (projectInput.getIcon() != null) {
            project.setIcon(projectInput.getIcon());
        }
        if (projectInput.getLiveUrl() != null) {
            project.setLiveUrl(projectInput.getLiveUrl());
        }
        if (projectInput.getGithubUrl() != null) {
            project.setGithubUrl(projectInput.getGithubUrl());
        }
        if (projectInput.getVideoUrl() != null) {
            project.setVideoUrl(projectInput.getVideoUrl());
        }
        if (projectInput.getImageUrls() != null) {
            project.setImageUrls(projectInput.getImageUrls());
        }
        if (projectInput.getSkillIds() != null) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(projectInput.getSkillIds()));
            project.setSkills(skills);
        }
    }

    @Transactional
    public boolean deleteProject(Long projectId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to delete this project.");
        }

        user.getProjects().remove(project);
        return true;
    }

    @Transactional
    public Publication addPublication(String username, PublicationInputDTO publicationInput) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Publication publication = new Publication();
        publication.setUser(user);

        updatePublicationEntity(publication, publicationInput);

        return publicationRepository.save(publication);
    }

    @Transactional
    public Publication updatePublication(Long publicationId, String username, PublicationInputDTO publicationInput) {
        if(publicationInput == null) {
            throw new IllegalArgumentException("Publication input cannot be null.");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found: " + publicationId));

        if (publication.getUser() == null) {
            throw new IllegalStateException("Publication with ID " + publicationId + " has no associated user.");
        }

        if (!publication.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to edit this publication.");
        }

        updatePublicationEntity(publication, publicationInput);

        return publicationRepository.save(publication);
    }

    private void updatePublicationEntity(Publication publication, PublicationInputDTO publicationInput) {
        if (publicationInput.getTitle() != null) {
            publication.setTitle(publicationInput.getTitle());
        }
        if (publicationInput.getDescription() != null) {
            publication.setDescription(publicationInput.getDescription());
        }
        if (publicationInput.getUrl() != null) {
            publication.setUrl(publicationInput.getUrl());
        }
    }

    @Transactional
    public boolean deletePublication(Long publicationId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Publication not found: " + publicationId));

        if (!publication.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to delete this publication.");
        }

        user.getPublications().remove(publication);
        return true;
    }

    @Transactional
    public WorkExperience addWorkExperience(String username, WorkExperienceInputDTO workExperienceInput) {
        if(workExperienceInput == null) {
            throw new IllegalArgumentException("Work experience input cannot be null.");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        WorkExperience workExperience = new WorkExperience();
        workExperience.setUser(user);

        updateWorkExperienceEntity(workExperience, workExperienceInput);

        return workExperienceRepository.save(workExperience);
    }

    @Transactional
    public WorkExperience updateWorkExperience(Long workExperienceId, String username, WorkExperienceInputDTO workExperienceInput) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found with ID: " + workExperienceId));

        if(!workExperience.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to update this work experience.");
        }

        updateWorkExperienceEntity(workExperience, workExperienceInput);

        return workExperienceRepository.save(workExperience);
    }

    private void updateWorkExperienceEntity(WorkExperience workExperience, WorkExperienceInputDTO workExperienceInput) {
        if(workExperienceInput.getJobTitle() != null) {
            workExperience.setJobTitle(workExperienceInput.getJobTitle());
        }
        if(workExperienceInput.getCompanyName() != null) {
            workExperience.setCompanyName(workExperienceInput.getCompanyName());
        }
        if(workExperienceInput.getCompanyLogoUrl() != null) {
            workExperience.setCompanyLogoUrl(workExperienceInput.getCompanyLogoUrl());
        }
        if(workExperienceInput.getLocation() != null) {
            workExperience.setLocation(workExperienceInput.getLocation());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        try {
            if (workExperienceInput.getStartDate() != null && !workExperienceInput.getStartDate().isBlank()) {
                workExperience.setStartDate(formatter.parse(workExperienceInput.getStartDate()));
            }
            if (workExperienceInput.getEndDate() != null && !workExperienceInput.getEndDate().isBlank()) {
                workExperience.setEndDate(formatter.parse(workExperienceInput.getEndDate()));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM.", e);
        }
        if(workExperienceInput.getDescription() != null) {
            workExperience.setDescription(workExperienceInput.getDescription());
        }
        if (workExperienceInput.getSkillIds() != null) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(workExperienceInput.getSkillIds()));
            workExperience.setSkills(skills);
        }
    }

    @Transactional
    public boolean deleteWorkExperience(Long workExperienceId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        WorkExperience workExperience = workExperienceRepository.findById(workExperienceId)
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found with ID: " + workExperienceId));

        if(!workExperience.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to delete this work experience.");
        }

        user.getWorkExperiences().remove(workExperience);
        return true;
    }

    @Transactional
    public Set<Skill> updateUserSkills(String username, List<Long> skillIds) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<Skill> newSkills = new HashSet<>(skillRepository.findAllById(skillIds));
        user.setSkills(newSkills);
        userRepository.save(user);

        return user.getSkills();
    }
}
