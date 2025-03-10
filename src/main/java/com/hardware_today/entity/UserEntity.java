package com.hardware_today.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.hardware_today.model.UserModel;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
public class UserEntity {
    public UserEntity(UserModel model) {
        this.firstName = model.getFirstName();
        this.lastName = model.getLastName();
        this.email = model.getEmail();
        this.password = hashPassword(model.getPassword());
        this.phone = model.getPhone();
        this.address = new AddressEntity(model.getAddress());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String password;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private AddressEntity address;
    
    private String hashPassword(String password) {
    	return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    public Boolean checkPassword(String rawPWord) {
    	return BCrypt.checkpw(rawPWord, this.password);
    }
}
