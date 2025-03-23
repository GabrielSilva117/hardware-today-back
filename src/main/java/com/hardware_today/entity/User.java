package com.hardware_today.entity;

import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.hardware_today.model.UserModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public User(UserModel model) {
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.email = model.getEmail();
        this.password = hashPassword(model.getPassword());
        this.phone = model.getPhone();
        this.address = new Address(model.getAddress());
        this.enabled = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String phone;

    private String password;
    
    @ColumnDefault("true")
    private boolean enabled;
    
    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @JoinTable(
    			name = "user_roles",
    			joinColumns = @JoinColumn(name="user_id"),
    			inverseJoinColumns = @JoinColumn(name="role_id")
    		)
    private Set<Role> roles;
    
    private String hashPassword(String password) {
    	return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public Boolean checkPassword(String rawPWord) {
    	return BCrypt.checkpw(rawPWord, this.password);
    }
}
