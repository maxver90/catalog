package maxver90.catalog;

import maxver90.catalog.entity.Category;
import maxver90.catalog.entity.Characteristic;
import maxver90.catalog.entity.Product;
import maxver90.catalog.entity.Value;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
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
            TypedQuery<Category> queryCategories = manager.createQuery(
                    "select c from Category c order by c.name", Category.class
            );
            Product newProduct = new Product();
            List<Category> categories = queryCategories.getResultList();
            for (Category category : categories) {
                System.out.println(category.getName() + "[" + category.getId() + "]");
            }
            System.out.print("Выберите ID категории, в кторотую нужно добавить товар: ");
            String choiceCategory = IN.nextLine();
            Category category = manager.find(Category.class, Long.parseLong(choiceCategory));
            newProduct.setCategory(category);
            System.out.print("Введите название товара: ");
            String newName = IN.nextLine();
            newProduct.setName(newName);
            System.out.print("Введите цену: ");
            Integer newPrice = Integer.parseInt(IN.nextLine());
            newProduct.setPrice(newPrice);
            System.out.print("Введите описание: ");
            String newDescription = IN.nextLine();
            newProduct.setDescription(newDescription);
            manager.persist(newProduct);

            List<Characteristic> characteristics = category.getCharacteristics();
            for (Characteristic characteristic : characteristics) {
                Value newValue = new Value();
                System.out.print(characteristic.getTitle() + ": ");
                String newCharacteristic = IN.nextLine();
                newValue.setProduct(newProduct);
                newValue.setCharacteristic(characteristic);
                newValue.setValue(newCharacteristic);
                manager.persist(newValue);
            }

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
