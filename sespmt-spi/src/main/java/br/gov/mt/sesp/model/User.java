package br.gov.mt.sesp.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String password;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_papel",
        joinColumns = @JoinColumn(name = "id_usuario",
                foreignKey = @ForeignKey(name = "fk_usuario_papel_usuario")),
        inverseJoinColumns = @JoinColumn(name = "id_papel",
                foreignKey = @ForeignKey(name = "fk_usuario_papel_papel")))
    private Set<Role> roles;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.email;
    }

    public void setUsername(String username) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        if (name == null || name.isEmpty()) {
            return null;
        }

        int spaceIndex = name.indexOf(" ");

        if (spaceIndex != -1) {
            return name.substring(0, spaceIndex);
        }

        return name;
    }

    public void setFirstName(String firstName) {
        this.name = firstName + getLastName();
    }

    public String getLastName() {
        if (name == null || name.isEmpty()) {
            return null;
        }

        int spaceIndex = name.indexOf(" ");

        if (spaceIndex != -1) {
            return name.substring(spaceIndex);
        }

        return null;
    }

    public void setLastName(String lastName) {
        this.name = getFirstName() + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id)
            && Objects.equals(name, user.name)
            && Objects.equals(email, user.email)
            && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password);
    }
}
