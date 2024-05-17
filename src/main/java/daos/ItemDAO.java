package daos;

import persistence.model.Item;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import persistence.model.User;

import java.util.List;

public class ItemDAO extends AbstractDAO<Item> {

    private static ItemDAO instance;
    private static EntityManagerFactory emf;

    private ItemDAO(EntityManagerFactory _emf, Class<Item> entityClass) {
        super(_emf, entityClass);
    }

    public static ItemDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ItemDAO(emf, Item.class);
        }
        return instance;
    }

    public List<Item> getAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Item> query = em.createQuery("SELECT h FROM Item h", Item.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public int delete(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Item itemFound = em.find(Item.class, id);

            if (itemFound != null) {
                // Clear associations
                User user = itemFound.getUser();
                if (user != null) {
                    user.getItems().remove(itemFound);
                }
                itemFound.setUser(null);
                em.remove(itemFound);
                em.getTransaction().commit();
                return 1;
            }
            return 0;
        } finally {
            em.close();
        }
    }
}
