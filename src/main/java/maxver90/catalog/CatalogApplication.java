package maxver90.catalog;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Scanner;

public class CatalogApplication {

    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory("main-connection");

    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("""
                Добавить категорию [1]
                Редактировать категорию [2]
                Удалить категорию [3]
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

    private static void create(){
        //
    }

    private static void update(){
        //
    }

    private static void delete(){
        //
    }

}
