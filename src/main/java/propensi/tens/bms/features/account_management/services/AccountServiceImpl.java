package propensi.tens.bms.features.account_management.services;

import propensi.tens.bms.features.account_management.dto.request.ChangePasswordDTO;
import propensi.tens.bms.features.account_management.dto.request.CreateAccountRequestDTO;
import propensi.tens.bms.features.account_management.dto.request.UpdateAccountRoleStatusDTO;
import propensi.tens.bms.features.account_management.dto.request.UpdatePersonalDataDTO;
import propensi.tens.bms.features.account_management.dto.response.EndUserResponseDTO;
import propensi.tens.bms.features.account_management.models.*;
import propensi.tens.bms.features.account_management.repositories.*;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private EndUserDb endUserDb;

    @Autowired
    private AdminDb adminDb;

    @Autowired
    private BaristaDb baristaDb;

    @Autowired
    private CLevelDb cLevelDb;

    @Autowired
    private HeadBarDb headBarDb;

    @Autowired
    private ProbationBaristaDb probationBaristaDb;

    @Autowired
    private OutletDb outletDb;

    @Override
    public String hashPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Override
    public void createAccount(CreateAccountRequestDTO createAccountRequestDTO) throws Exception {
        String role = createAccountRequestDTO.getRole();
        String hashedPassword = hashPassword("newuser123");

        String fullName = createAccountRequestDTO.getFullName();
        Boolean gender = createAccountRequestDTO.getGender();
        String phoneNumber = createAccountRequestDTO.getPhoneNumber();
        String addresss = createAccountRequestDTO.getAddress();
        String status = createAccountRequestDTO.getStatus();
        String outletName = createAccountRequestDTO.getOutletName();
        Date dateOfBirth = createAccountRequestDTO.getDateOfBirth();

        String[] splitName = fullName.trim().split("\\s+");
        String firstName = splitName[0].toLowerCase();
        String lastName = splitName.length > 1 
            ? splitName[splitName.length - 1].toLowerCase() 
            : firstName;
        String username = firstName + "." + lastName;

        if (fullName == null || fullName.isEmpty()) throw new Exception("Name is required");
        if (gender == null) throw new Exception("Gender is required");
        if (role == null || role.isEmpty()) throw new Exception("Role is required");


        Outlet outlet = null;
        
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("clevel") &&
            !role.equalsIgnoreCase("hr") ) {
            
            if (outletName == null) throw new Exception("Outlet is required for this role");

            outlet = outletDb.findByName(outletName);

        }

        switch (role.toLowerCase()) {
            case "admin":
                Admin admin = new Admin();
                admin.setFullName(fullName);
                admin.setUsername(username);
                admin.setPassword(hashedPassword);
                admin.setGender(gender);
                admin.setPhoneNumber(phoneNumber);
                admin.setAddress(addresss);
                admin.setStatus(status);
                admin.setDateOfBirth(dateOfBirth);
                admin.setIsVerified(true);
                adminDb.save(admin);
                break;

            case "clevel":
            case "hr":
                CLevel clevel = new CLevel();
                clevel.setFullName(fullName);
                clevel.setUsername(username);
                clevel.setPassword(hashedPassword);
                clevel.setCLevelType(role);
                clevel.setGender(gender);
                clevel.setPhoneNumber(phoneNumber);
                clevel.setAddress(addresss);
                clevel.setStatus(status);
                clevel.setDateOfBirth(dateOfBirth);
                clevel.setIsVerified(false);
                cLevelDb.save(clevel);
                break;

            case "head bar":
                HeadBar headBar = new HeadBar();
                headBar.setFullName(fullName);
                headBar.setUsername(username);
                headBar.setPassword(hashedPassword);
                headBar.setGender(gender);
                headBar.setPhoneNumber(phoneNumber);
                headBar.setAddress(addresss);
                headBar.setStatus(status);
                headBar.setOutlet(outlet);
                headBar.setDateOfBirth(dateOfBirth);
                headBar.setIsVerified(false);
                headBarDb.save(headBar);

                outlet.setHeadbar(headBar);
			    outletDb.save(outlet);
                break;

            case "barista":
                Barista barista = new Barista();
                barista.setFullName(fullName);
                barista.setUsername(username);
                barista.setPassword(hashedPassword);
                barista.setGender(gender);
                barista.setPhoneNumber(phoneNumber);
                barista.setAddress(addresss);
                barista.setStatus(status);
                barista.setOutlet(outlet);
                barista.setDateOfBirth(dateOfBirth);
                barista.setIsVerified(false);
                barista.setIsIntern(false);
                baristaDb.save(barista);
                break;

            case "intern barista":
                Barista tbarista = new Barista();
                tbarista.setFullName(fullName);
                tbarista.setUsername(username);
                tbarista.setPassword(hashedPassword);
                tbarista.setGender(gender);
                tbarista.setPhoneNumber(phoneNumber);
                tbarista.setAddress(addresss);
                tbarista.setStatus(status);
                tbarista.setOutlet(outlet);
                tbarista.setDateOfBirth(dateOfBirth);
                tbarista.setIsVerified(false);
                tbarista.setIsIntern(true);
                baristaDb.save(tbarista);
                break;

            case "probation barista":
                ProbationBarista probationBarista = new ProbationBarista();
                probationBarista.setFullName(fullName);
                probationBarista.setUsername(username);
                probationBarista.setPassword(hashedPassword);
                probationBarista.setGender(gender);
                probationBarista.setPhoneNumber(phoneNumber);
                probationBarista.setAddress(addresss);
                probationBarista.setStatus(status);
                probationBarista.setOutlet(outlet);
                probationBarista.setDateOfBirth(dateOfBirth);
                probationBarista.setIsVerified(false);
                probationBaristaDb.save(probationBarista);
                break;

            default:
                throw new Exception("Invalid role");
        }
    }

    @Override
    public List<EndUserResponseDTO> getAllEndUsers(String role, String search) {
        List<EndUser> users;
        List<EndUserResponseDTO> result = new ArrayList<>();
        if (role == null || role.isEmpty()) {
            users = endUserDb.findAll();
        } else {
            switch (role.toLowerCase()) {
                case "admin":
                    users = new ArrayList<>(adminDb.findAll());
                    break;
                case "clevel":
                    users = new ArrayList<>(cLevelDb.findBycLevelType("CLEVEL"));
                    break;
                case "hr":
                    users = new ArrayList<>(cLevelDb.findBycLevelType("HR"));
                    break;
                case "head bar":
                    users = new ArrayList<>(headBarDb.findAll());
                    break;
                case "barista":
                    users = new ArrayList<>(baristaDb.findAll());
                    break;
                case "probation barista":
                    users = new ArrayList<>(probationBaristaDb.findAll());
                    break;
                default:
                    users = new ArrayList<>();
            }
        }

        if (search != null && !search.isEmpty()) {
            String lowerSearch = search.toLowerCase();
            users = users.stream()
                    .filter(user -> user.getFullName().toLowerCase().contains(lowerSearch) ||
                                    user.getUsername().toLowerCase().contains(lowerSearch))
                    .collect(Collectors.toList());
        }

        for (EndUser endUser : users) {
            result.add(mapToEndUserResponseDTO(endUser, false));
        }

        return result;
    }

    @Override
    public EndUserResponseDTO getAccountDetail(String username) throws EntityNotFoundException {
        EndUser account = endUserDb.findByUsername(username);

        if (account == null) {
            throw new EntityNotFoundException("account with username " + username + " not found");
        }

        return mapToEndUserResponseDTO(account, true);
    }

    private EndUserResponseDTO mapToEndUserResponseDTO(EndUser user, boolean isDetail) {
        EndUserResponseDTO dto = new EndUserResponseDTO();
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setStatus(user.getStatus());
        dto.setAddress(user.getAddress());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        if (isDetail) {
            dto.setGender(user.getGender());
            dto.setDateOfBirth(user.getDateOfBirth());
        }

        if (user instanceof Admin) {
            dto.setRole("Admin");
        } else if (user instanceof CLevel) {
            CLevel cLevelUser = cLevelDb.findById(user.getId()).orElse(null);
            dto.setRole(cLevelUser.getCLevelType());
        } else if (user instanceof HeadBar) {
            HeadBar headBar = headBarDb.findById(user.getId()).orElse(null);
            Outlet outlet = outletDb.findByOutletId(headBar.getOutlet().getOutletId());
            dto.setRole("Head Bar");
            dto.setOutlet(outlet.getName());
        } else if (user instanceof Barista) {
            Barista barista = baristaDb.findById(user.getId()).orElse(null);
            Outlet outlet = outletDb.findByOutletId(barista.getOutlet().getOutletId());
            dto.setOutlet(outlet.getName());
            if (barista.getIsIntern()) {
                dto.setRole("Intern Barista");
            } else {
                dto.setRole("Barista");
                
            }
        } else if (user instanceof ProbationBarista) {
            ProbationBarista barista = probationBaristaDb.findById(user.getId()).orElse(null);
            Outlet outlet = outletDb.findByOutletId(barista.getOutlet().getOutletId());
            dto.setOutlet(outlet.getName());
            dto.setRole("Probation Barista");
        }

        return dto;
    }

    @Override
public EndUser updateAccountRoleAndStatus(String username, UpdateAccountRoleStatusDTO dto) throws Exception {
    EndUser oldUser = endUserDb.findByUsername(username);

    String newRole = dto.getRole();
    String newStatus = dto.getStatus();
    String newOutletName = dto.getOutletName();
    
    String currentRole = "";
    if (oldUser instanceof Admin) {
        currentRole = "admin";
    } else if (oldUser instanceof HeadBar) {
        currentRole = "head bar";
    } else if (oldUser instanceof Barista) {
        if (((Barista) oldUser).getIsIntern()) {
            currentRole = "intern barista";
        } else {
            currentRole = "barista";
        } 
    } else if (oldUser instanceof ProbationBarista) {
        currentRole = "probation barista";
    } else if (oldUser instanceof CLevel) {
        currentRole = ((CLevel) oldUser).getCLevelType().toLowerCase();
    } else {
        throw new Exception("Unknown user type");
    }

    // Get old outlet if exists
    Outlet oldOutlet = null;
    if (oldUser instanceof Barista) {
        Barista oldBarista = baristaDb.findById(oldUser.getId()).orElse(null);
        if (oldBarista != null) {
            oldOutlet = oldBarista.getOutlet();
        }
    } else if (oldUser instanceof HeadBar) {
        HeadBar oldHeadBar = headBarDb.findById(oldUser.getId()).orElse(null);
        if (oldHeadBar != null) {
            oldOutlet = oldHeadBar.getOutlet();
        }
    }

    // Get new outlet if specified
    Outlet newOutlet = null;
    if (newOutletName != null) {
        newOutlet = outletDb.findByName(newOutletName);
        if (newOutlet == null) {
            throw new Exception("Outlet with ID " + newOutletName + " not found");
        }
    } else if (oldOutlet != null) {
        // Keep the old outlet if no new outlet specified
        newOutlet = oldOutlet;
    } else if (newRole.equalsIgnoreCase("barista") || newRole.equalsIgnoreCase("head bar")) {
        // Default to first outlet if role requires an outlet but none specified
        newOutlet = outletDb.findAll().get(0);
    }

    // If only status changes and role/outlet remain the same
    if (newRole.equalsIgnoreCase(currentRole) && 
       ((newOutletName == null) || (oldOutlet != null && oldOutlet.getOutletId().equals(newOutlet.getOutletId())))) {
        
        oldUser.setStatus(newStatus);
        EndUser updatedUser;
        
        if (oldUser instanceof Admin) {
            updatedUser = adminDb.save((Admin) oldUser);
        } else if (oldUser instanceof HeadBar) {
            updatedUser = headBarDb.save((HeadBar) oldUser);
        } else if (oldUser instanceof Barista) {
            updatedUser = baristaDb.save((Barista) oldUser);
        } else if (oldUser instanceof ProbationBarista) {
            updatedUser = probationBaristaDb.save((ProbationBarista) oldUser);
        } else if (oldUser instanceof CLevel) {
            updatedUser = cLevelDb.save((CLevel) oldUser);
        } else {
            throw new Exception("Unknown user type.");
        }
        return updatedUser;
    }

    // Remove user from old outlet if exists
    if (oldOutlet != null) {
        if (oldUser instanceof Barista) {
            oldOutlet.getListBarista().removeIf(b -> b.getId().equals(oldUser.getId()));
            outletDb.save(oldOutlet);
        } else if (oldUser instanceof HeadBar) {
            oldOutlet.setHeadbar(null);
            outletDb.save(oldOutlet);
        }
    }

    // Save user data before deletion
    UUID userId = oldUser.getId();
    String oldUsername = oldUser.getUsername();
    String oldPassword = oldUser.getPassword();
    String oldFullName = oldUser.getFullName();
    Boolean oldGender = oldUser.getGender();
    String oldPhoneNumber = oldUser.getPhoneNumber();
    String oldAddress = oldUser.getAddress();
    Date oldDateOfBirth = oldUser.getDateOfBirth();

    endUserDb.delete(oldUser);
    EndUser updatedUser;

    switch (newRole.toLowerCase()) {
        case "admin":
            Admin admin = new Admin();
            admin.setId(userId);
            admin.setUsername(oldUsername);
            admin.setPassword(oldPassword);
            admin.setFullName(oldFullName);
            admin.setGender(oldGender);
            admin.setPhoneNumber(oldPhoneNumber);
            admin.setAddress(oldAddress);
            admin.setDateOfBirth(oldDateOfBirth);
            admin.setStatus(newStatus);
            updatedUser = adminDb.save(admin);
            break;

        case "clevel":
        case "hr":
            CLevel cLevel = new CLevel();
            cLevel.setId(userId);
            cLevel.setUsername(oldUsername);
            cLevel.setPassword(oldPassword);
            cLevel.setFullName(oldFullName);
            cLevel.setGender(oldGender);
            cLevel.setPhoneNumber(oldPhoneNumber);
            cLevel.setAddress(oldAddress);
            cLevel.setDateOfBirth(oldDateOfBirth);
            cLevel.setStatus(newStatus);
            cLevel.setCLevelType(newRole);
            updatedUser = cLevelDb.save(cLevel);
            break;

        case "head bar":
            if (newOutlet == null) {
                throw new Exception("Head Bar must be assigned to an outlet");
            }
            
            // Check if the outlet already has a head bar
            if (newOutlet.getHeadbar() != null && !newOutlet.getHeadbar().getId().equals(userId)) {
                throw new Exception("Outlet already has a Head Bar assigned");
            }
            
            HeadBar headBar = new HeadBar();
            headBar.setId(userId);
            headBar.setUsername(oldUsername);
            headBar.setPassword(oldPassword);
            headBar.setFullName(oldFullName);
            headBar.setGender(oldGender);
            headBar.setPhoneNumber(oldPhoneNumber);
            headBar.setAddress(oldAddress);
            headBar.setDateOfBirth(oldDateOfBirth);
            headBar.setStatus(newStatus);
            headBar.setOutlet(newOutlet);
            updatedUser = headBarDb.save(headBar);
            
            newOutlet.setHeadbar(headBar);
            outletDb.save(newOutlet);
            break;

        case "barista":
        case "intern barista":
            if (newOutlet == null) {
                throw new Exception("Barista must be assigned to an outlet");
            }
            
            Barista barista = new Barista();
            barista.setId(userId);
            barista.setUsername(oldUsername);
            barista.setPassword(oldPassword);
            barista.setFullName(oldFullName);
            barista.setGender(oldGender);
            barista.setPhoneNumber(oldPhoneNumber);
            barista.setAddress(oldAddress);
            barista.setDateOfBirth(oldDateOfBirth);
            barista.setStatus(newStatus);
            barista.setOutlet(newOutlet);
            barista.setIsIntern(newRole.equalsIgnoreCase("intern barista"));
            updatedUser = baristaDb.save(barista);
            
            newOutlet.getListBarista().add(barista);
            outletDb.save(newOutlet);
            break;

        case "probation barista":
            ProbationBarista probationBarista = new ProbationBarista();
            probationBarista.setId(userId);
            probationBarista.setUsername(oldUsername);
            probationBarista.setPassword(oldPassword);
            probationBarista.setFullName(oldFullName);
            probationBarista.setGender(oldGender);
            probationBarista.setPhoneNumber(oldPhoneNumber);
            probationBarista.setAddress(oldAddress);
            probationBarista.setDateOfBirth(oldDateOfBirth);
            probationBarista.setStatus(newStatus);
            updatedUser = probationBaristaDb.save(probationBarista);
            break;

        default:
            throw new Exception("Invalid role: " + newRole);
    }

    return updatedUser;
}


    @Override
    public EndUser updateUserPersonalData(String username, UpdatePersonalDataDTO dto) throws Exception {
        EndUser user = endUserDb.findByUsername(username);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(hashPassword(dto.getPassword()));
            if (!user.getIsVerified()) {
                user.setIsVerified(true);
            }
        }

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAddress() != null && !dto.getAddress().isEmpty()) {
            user.setAddress(dto.getAddress());
        }

        if (dto.getDateOfBirth() != null) {
           
            user.setDateOfBirth(dto.getDateOfBirth());
        }

        endUserDb.save(user);
        return user;
    }

    @Override
    public EndUser changePassword(String username, ChangePasswordDTO dto) throws Exception {
        EndUser user = endUserDb.findByUsername(username);
        if (user == null) {
            throw new Exception("User dengan username " + username + " tidak ditemukan.");
        }

        String uniqueKey = user.getUsername() + "@" + user.getPhoneNumber().replaceFirst("^\\+62", "");
       
        if (!dto.getCombination().equals(uniqueKey)) {
            throw new Exception("kombinasi username dan nomor telepon tidak sesuai.");
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().isEmpty()) {
            throw new Exception("Password baru tidak boleh kosong.");
        }

        String hashedNewPassword = hashPassword(dto.getNewPassword());
        user.setPassword(hashedNewPassword);

        if (!user.getIsVerified()) {
            user.setIsVerified(true);
        }

        endUserDb.save(user);

        return user;
    }
}   
