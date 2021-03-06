package maxver90.catalog;

import maxver90.catalog.entity.Category;
import maxver90.catalog.entity.Characteristic;
import maxver90.catalog.entity.Product;
import maxver90.catalog.entity.Value;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
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
                Отфильтровать [4]
                Выберите действие: \
                """);
        String actionNum = IN.nextLine();
        switch (actionNum) {
            case "1" -> create();
            case "2" -> update();
            case "3" -> delete();
            case "4" -> filter();
            default -> System.out.println("Такого действия не существует");
        }
    }

    private static void filter() {

        Long categoryId = 1L;
        String name = null;
        Integer minPrice = 75000;
        Integer maxPrice = 200000;

        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            CriteriaBuilder builder = manager.getCriteriaBuilder();
            CriteriaQuery<Product> criteriaQuery = builder.createQuery(Product.class);
            Root<Product> root = criteriaQuery.from(Product.class);
            List<Predicate> predicateList = new ArrayList<>();

            if (categoryId != null) {
                predicateList.add(builder.equal(root.get("category").get("id"), categoryId));
            }
            if (name != null) {
                predicateList.add(builder.like(root.get("name"), "%" + name + "%"));
            }
            if (minPrice != null) {
                predicateList.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicateList.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            Predicate[] predicates = new Predicate[predicateList.size()];
            for (int i = 0; i < predicateList.size(); i++) {
                predicates[i] = predicateList.get(i);
            }
            criteriaQuery.where(predicates);
            TypedQuery<Product> typedQuery = manager.createQuery(criteriaQuery);
            List<Product> products = typedQuery.getResultList();
            for (Product product : products) {
                System.out.println(product.getName());
            }
        } catch (
                Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            manager.close();
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
            while (true) {
                if (!choiceCategory.matches("\\d+")) {
                    System.out.println("Неверный формат!");
                    choiceCategory = IN.nextLine();
                    continue;
                }
                TypedQuery<Long> queryCategoriesId = manager.createQuery(
                        "select count(c.id) from Category c where c.id = ?1", Long.class
                );
                queryCategoriesId.setParameter(1, Long.parseLong(choiceCategory));
                Long countId = queryCategoriesId.getSingleResult();
                if (countId > 0) {
                    break;
                }
                System.out.println("Такой категории не существует!");
                choiceCategory = IN.nextLine();
            }
            Category category = manager.find(Category.class, Long.parseLong(choiceCategory));
            newProduct.setCategory(category);
            System.out.print("Введите название товара: ");
            String newName = IN.nextLine();
            newProduct.setName(newName);
            System.out.print("Введите цену: ");
            String newPrice = IN.nextLine();
            while (!newPrice.matches("\\d+")) {
                System.out.print("Неверный формат! Введите ещё раз: ");

                newPrice = IN.nextLine();
            }
            newProduct.setPrice(Integer.parseInt(newPrice));
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
        } catch (
                Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            manager.close();
        }
    }

    private static void update() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            System.out.print("Введите ID товара, который нужно обновить: ");
            String productId = IN.nextLine();
            while (!productId.matches("\\d+")) {
                System.out.print("Неверный формат! Введите ещё раз: ");
                productId = IN.nextLine();
            }
            Product product = manager.find(Product.class, Long.parseLong(productId));
            System.out.println(product.getName());
            System.out.print("Введите новое название: ");
            String newName = IN.nextLine();
            if (newName.isEmpty()) {
                product.setName(product.getName());
            } else {
                product.setName(newName);
            }
            System.out.println(product.getPrice());
            System.out.print("Введите новую цену: ");
            String newPrice = IN.nextLine();
            while (true) {
                if (!newPrice.matches("\\d+")) {
                    System.out.println("Неверный формат!");
                } else {
                    break;
                }
                System.out.println(product.getPrice());
                System.out.print("Введите новую цену: ");
                newPrice = IN.nextLine();
            }
            if (newPrice.isEmpty()) {
                product.setPrice(product.getPrice());
            } else {
                while (!newPrice.matches("\\d+")) {
                    System.out.print("Неверный формат! Введите ещё раз: ");
                    newPrice = IN.nextLine();
                }
                product.setPrice(Integer.parseInt(newPrice));
            }
            System.out.println(product.getDescription());
            System.out.print("Введите новое описание: ");
            String newDescription = IN.nextLine();
            if (newDescription.isEmpty()) {
                product.setDescription(product.getDescription());
            } else {
                product.setDescription(newDescription);
            }
            List<Characteristic> characteristics = product.getCategory().getCharacteristics();
            for (Characteristic characteristic : characteristics) {
                System.out.println(characteristic.getTitle());
                System.out.print("Обновите характеристику: ");
                String newValue = IN.nextLine();
                TypedQuery<Value> valueQuery = manager.createQuery(
                        "select v from Value v where v.product = ?1 and v.characteristic = ?2", Value.class
                );
                valueQuery.setParameter(1, product);
                valueQuery.setParameter(2, characteristic);
                valueQuery.setMaxResults(1);
                List<Value> valuesList = valueQuery.getResultList();
                if (valuesList.isEmpty()) {
                    Value value = new Value();
                    value.setValue(newValue);
                    value.setProduct(product);
                    value.setCharacteristic(characteristic);
                    manager.persist(value);
                } else {
                    Value value = valuesList.get(0);
                    if (newValue.isEmpty()) {
                        value.setValue(valuesList.get(0).getValue());
                    } else {
                        value.setValue(newValue);
                    }
                }
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            manager.close();
        }
    }

    private static void delete() {
        EntityManager manager = FACTORY.createEntityManager();
        try {
            manager.getTransaction().begin();
            System.out.print("Введите ID товара, который нужно удалить: ");
            String productId = IN.nextLine();
            while (!productId.matches("\\d+")) {
                System.out.print("Неверный формат! Введите ещё раз: ");
                productId = IN.nextLine();
            }
            Product product = manager.find(Product.class, Long.parseLong(productId));
            manager.remove(product);
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            manager.close();

        }
    }
}
