package maxver90.catalog;

import maxver90.catalog.entity.Category;
import maxver90.catalog.entity.Product;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CatalogApplication {

    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory("main-connection");

    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("""
                Добавить [1]
                Редактировать [2]
                Удалить [3]
                Выберите действие: \
                """);
        String actionNum = IN.nextLine();
        switch (actionNum) {
            case "1" -> create();
            case "2" -> update();
            case "3" -> delete();
            default -> System.out.println("Такого действия не существует");
        }
    }

    private static void create() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            TypedQuery<Category> query = manager.createQuery(
                    "select c from Category c order by c.name", Category.class
            );
            Product newProduct = new Product();
            List<Category> categories = query.getResultList();
            for (Category category : categories) {
                System.out.println(category.getName() + "[" + category.getId() + "]");
            }
            System.out.println("Выберите ID категории, в кторотую нужно добавить товар: ");
            String choiceCategory = IN.nextLine();
            Category category = manager.find(Category.class, Long.parseLong(choiceCategory));
            newProduct.setCategory(category);
            System.out.println("Введите название товара: ");
            String newName = IN.nextLine();
            newProduct.setName(newName);
            System.out.println("Введите цену: ");
            Integer newPrice = Integer.parseInt(IN.nextLine());
            newProduct.setPrice(newPrice);
            System.out.println("Введите описание: ");
            String newDescription = IN.nextLine();
            newProduct.setDescription(newDescription);
            manager.persist(newProduct);
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            manager.close();
        }
    }

    private static void update() {
        //
    }

    private static void delete() {
        //
    }

}
