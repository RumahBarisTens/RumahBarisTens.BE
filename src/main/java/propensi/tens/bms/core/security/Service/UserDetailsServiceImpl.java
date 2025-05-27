package propensi.tens.bms.core.security.Service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import propensi.tens.bms.features.account_management.models.Admin;
import propensi.tens.bms.features.account_management.models.Barista;
import propensi.tens.bms.features.account_management.models.CLevel;
import propensi.tens.bms.features.account_management.models.EndUser;
import propensi.tens.bms.features.account_management.models.HeadBar;
import propensi.tens.bms.features.account_management.models.ProbationBarista;
import propensi.tens.bms.features.account_management.repositories.CLevelDb;
import propensi.tens.bms.features.account_management.repositories.EndUserDb;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private EndUserDb userDb;

    @Autowired
    private CLevelDb cLevelDb;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        EndUser user = userDb.findByUsername(username);

        Set<GrantedAuthority> authorities = new HashSet<>();
    
        if (user instanceof Admin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_Admin"));
        } else if (user instanceof CLevel) {
            CLevel cLevelUser = cLevelDb.findById(user.getId()).orElse(null);
        
            if (cLevelUser != null) {
                String cLevelType = cLevelUser.getCLevelType();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + cLevelType.toUpperCase()));
            } else {
                throw new RuntimeException("CLevel tidak ditemukan");
            }
        } else if (user instanceof HeadBar) {
            authorities.add(new SimpleGrantedAuthority("ROLE_HeadBar"));
        } else if (user instanceof Barista) {
            authorities.add(new SimpleGrantedAuthority("ROLE_Barista"));
        } else if (user instanceof ProbationBarista) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ProbationBarista"));
        }
    
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
    
}
