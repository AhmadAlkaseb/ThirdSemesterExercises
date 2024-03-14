package ThirdSemesterExercises.Backend.Week10Year2024.Day2.DAOs;

import ThirdSemesterExercises.Backend.Week10Year2024.Day2.Persistence.HibernateConfig;
import ThirdSemesterExercises.Backend.Week10Year2024.Day2.Persistence.Model.Role;
import ThirdSemesterExercises.Backend.Week10Year2024.Day2.Persistence.Model.User;
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO implements ISecurityDAO {

    private EntityManagerFactory emf;

    public UserDAO(EntityManagerFactory _emf) {
        this.emf = _emf;
    }

    @Override
    public User getVerifiedUser(String username, String password) throws EntityNotFoundException {
        try (EntityManager em = emf.createEntityManager()) {
            List<User> users = em.createQuery("SELECT u FROM User u").getResultList();
            users.forEach(user -> System.out.println(user.getUsername() + " " + user.getPassword()));
            User user = em.find(User.class, username);
            if (user == null) {
                throw new EntityNotFoundException("No user found with username: " + username);
            }
            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password)) {
                throw new EntityNotFoundException("Wrong password");
            }
            return user;
        }
    }



    @Override
    public User createUser(String username, String password) {
        try {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            User createdUser = new User(username, password);
            em.persist(createdUser);
            em.getTransaction().commit();
            return createdUser;
        } finally {
            emf.close();
        }
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);
        UserDAO dao = new UserDAO(emf);
        //User user = dao.createUser("holger", "1234");
        //System.out.println(user.getUsername());
        try {
            User user = dao.verifyUser("holger", "1234");
            System.out.println(user.getUsername());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Role createRole(String role) {
        return null;
    }

    @Override
    public User addUserRole(String username, String role) {
        return null;
    }

    public User addRoleToUser(String username, String role) {
        return null;
    }

    public User verifyUser(String username, String password) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        if (user == null) throw new EntityNotFoundException("No user found");
        if (!user.verifyPassword(password)) throw new EntityNotFoundException("Wrong password");
        return user;
    }
}

