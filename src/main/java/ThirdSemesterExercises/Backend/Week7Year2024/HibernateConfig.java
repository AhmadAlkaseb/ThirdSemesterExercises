package ThirdSemesterExercises.Backend.Week7Year2024;

import ThirdSemesterExercises.Backend.Week7Year2024.SchoolExercises.Day2.Employee;
import ThirdSemesterExercises.Backend.Week7Year2024.Day1.Exercise1.Unicorn;
import ThirdSemesterExercises.Backend.Week7Year2024.Day1.Exercise2.Student;
import ThirdSemesterExercises.Backend.Week7Year2024.Day2.Exercise4.Person;
import ThirdSemesterExercises.Backend.Week7Year2024.Day2.Exercise5.Student1;
import ThirdSemesterExercises.Backend.Week7Year2024.Day3.Exercise6.Package;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HibernateConfig {

    private static EntityManagerFactory entityManagerFactory;

    private static EntityManagerFactory buildEntityFactoryConfig() {
        try {
            Configuration configuration = new Configuration();

            Properties props = new Properties();

            props.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/exercise?currentSchema=public");
            props.put("hibernate.connection.username", "postgres");
            props.put("hibernate.connection.password", "postgres");
            props.put("hibernate.show_sql", "true"); // show sql in console
            props.put("hibernate.format_sql", "true"); // format sql in console
            props.put("hibernate.use_sql_comments", "true"); // show sql comments in console

            props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); // dialect for postgresql
            props.put("hibernate.connection.driver_class", "org.postgresql.Driver"); // driver class for postgresql
            props.put("hibernate.archive.autodetection", "class"); // hibernate scans for annotated classes
            props.put("hibernate.current_session_context_class", "thread"); // hibernate current session context
            props.put("hibernate.hbm2ddl.auto", "update"); // hibernate creates tables based on entities


            return getEntityManagerFactory(configuration, props);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory getEntityManagerFactory(Configuration configuration, Properties props) {
        configuration.setProperties(props);

        getAnnotationConfiguration(configuration);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        System.out.println("Hibernate Java Config serviceRegistry created");

        SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
        return sf.unwrap(EntityManagerFactory.class);
    }

    private static void getAnnotationConfiguration(Configuration configuration) {
        // add annotated classes
        // configuration.addAnnotatedClass(<YOUR ENTITY>.class);

        configuration.addAnnotatedClass(Unicorn.class);
        configuration.addAnnotatedClass(Student.class);
        configuration.addAnnotatedClass(Student1.class);
        configuration.addAnnotatedClass(Person.class);
        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(Employee.class);
        configuration.addAnnotatedClass(Package.class);
    }

    public static EntityManagerFactory getEntityManagerFactoryConfig() {
        if (entityManagerFactory == null) entityManagerFactory = buildEntityFactoryConfig();
        return entityManagerFactory;
    }
}